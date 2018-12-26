package handler;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 *
 */
public class AcceptEventHandler implements EventHandler {

    private Selector demultiplexor;
    public AcceptEventHandler(Selector demultiplexor) {
        this.demultiplexor = demultiplexor;
    }

    public void handleEvent(SelectionKey handle) throws Exception {
        System.out.println("===== Accept Event Handler =====");
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) handle.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        if (socketChannel != null) {
            socketChannel.configureBlocking(false);
            socketChannel.register(demultiplexor, SelectionKey.OP_READ);
        }
    }
}
