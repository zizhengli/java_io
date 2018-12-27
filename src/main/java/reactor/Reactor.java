package reactor;

import handler.AcceptEventHandler;
import handler.EventHandler;
import handler.ReadEventHandler;
import handler.WriteEventHandler;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 *
 */
public class Reactor {

    private final static int SERVER_PORT = 8888;
    private final static int QUEUE_SIZE = 10;
    private BlockingQueue<EventHandler> handlerTaskQueue;

    private Reactor() {
        this.handlerTaskQueue = new ArrayBlockingQueue<EventHandler>(QUEUE_SIZE);
    }

    public void startReactor() throws Exception {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(SERVER_PORT));
        serverSocketChannel.configureBlocking(false);
        Acceptor acceptor = new Acceptor(handlerTaskQueue);
        acceptor.registerChannel(SelectionKey.OP_ACCEPT, serverSocketChannel);

        acceptor.registerEventHandler(SelectionKey.OP_ACCEPT, new AcceptEventHandler(acceptor.getSelector()));
        acceptor.registerEventHandler(SelectionKey.OP_READ, new ReadEventHandler(acceptor.getSelector()));
        acceptor.registerEventHandler(SelectionKey.OP_WRITE, new WriteEventHandler());
        new Thread(acceptor).start(); // Run the dispatcher loop

        new Thread(new ReactorEventHandler(this.handlerTaskQueue)).start();
    }

    public static void main(String[] args) {
        System.out.println("Server Started at port : " + SERVER_PORT);
        try {
            new Reactor().startReactor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
