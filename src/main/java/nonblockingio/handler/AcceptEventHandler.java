package nonblockingio.handler;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 *
 */
public class AcceptEventHandler implements EventHandler {

    private Selector selector;
    private SelectionKey handle;

    public AcceptEventHandler(Selector selector) {
        this.selector = selector;
    }

    public void setHandleEvent(SelectionKey handle) {
        this.handle = handle;
    }

    public void handle() {
        System.out.println("===== Accept Event Handler =====");
        try {
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) handle.channel();
            SocketChannel socketChannel = serverSocketChannel.accept();
            if (socketChannel != null) {
                socketChannel.configureBlocking(false);
                socketChannel.register(selector, SelectionKey.OP_READ);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
