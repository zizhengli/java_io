package nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SelectorThread implements Runnable {

    private BlockingQueue<Channel> channelQueue;
    // One Selector per thread
    private Selector selector = null;
    private SelectorThreadGroup selectorThreadGroup;

    SelectorThread(SelectorThreadGroup stg) {
        try {
            channelQueue = new LinkedBlockingQueue();
            selectorThreadGroup = stg;
            selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                // Step 1
                int nums = selector.select(); // a system call, Blocking till there is at least one connection.
                // Step 2
                if (nums > 0) {
                    Set<SelectionKey> keys = selector.selectedKeys(); // Get all the file descriptors
                    Iterator<SelectionKey> iter = keys.iterator();
                    while (iter.hasNext()) {  //线程处理的过程
                        SelectionKey key = iter.next();
                        iter.remove();
                        if (key.isAcceptable()) {  //复杂,接受客户端的过程（接收之后，要注册，多线程下，新的客户端，注册到那里呢？）
                            acceptHandler(key);
                        } else if (key.isReadable()) {
                            readHandler(key);
                        } else if (key.isWritable()) {

                        }
                    }
                }
                // Step 3
                if (!channelQueue.isEmpty()) {
                    Channel c = channelQueue.take();
                    if (c instanceof ServerSocketChannel) {
                        ServerSocketChannel server = (ServerSocketChannel) c;
                        server.register(selector, SelectionKey.OP_ACCEPT);
                        System.out.println(Thread.currentThread().getName() + " register listen");
                    } else if (c instanceof SocketChannel) {
                        SocketChannel client = (SocketChannel) c;
                        ByteBuffer buffer = ByteBuffer.allocateDirect(4096);
                        client.register(selector, SelectionKey.OP_READ, buffer);
                        System.out.println(Thread.currentThread().getName() + " register client: " + client.getRemoteAddress());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Selector getSelector() {
        return selector;
    }

    public BlockingQueue getChannelQueue() {
        return channelQueue;
    }

    public void setGroup(SelectorThreadGroup group) {
        selectorThreadGroup = group;
    }

    private void acceptHandler(SelectionKey key) {
        try {
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel client = server.accept();
            client.configureBlocking(false);
            selectorThreadGroup.nextSelector(client); // TODO

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readHandler(SelectionKey key) {
        System.out.println(Thread.currentThread().getName() + " read......");
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        SocketChannel client = (SocketChannel) key.channel();
        buffer.clear();
        while (true) {
            try {
                int num = client.read(buffer);
                if (num > 0) {
                    buffer.flip();
                    while (buffer.hasRemaining()) {
                        client.write(buffer);
                    }
                    buffer.clear();
                } else if (num == 0) {
                    break;
                } else if (num < 0) {
                    System.out.println("client: " + client.getRemoteAddress() + "closed......");
                    key.cancel();
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
