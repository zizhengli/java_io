package handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 *
 */
public class ReadEventHandler implements EventHandler {

    private ByteBuffer inputBuffer = ByteBuffer.allocate(4096);
    private SelectionKey handle;
    private Selector selector;

    public ReadEventHandler(Selector selector) {
        this.selector = selector;
    }

    public void setHandleEvent(SelectionKey handle) {
        this.handle = handle;
    }

    public void handle() {
        System.out.println("===== Read Event Handler =====");
        try {
            if(handle == null) {
                throw new IOException("Handle is null");
            }
            SocketChannel socketChannel = (SocketChannel) handle.channel();
            socketChannel.read(inputBuffer); // Read data from client.client
            inputBuffer.flip();
            // Rewind the buffer to start reading from the beginning
            byte[] buffer = new byte[inputBuffer.limit()];
            inputBuffer.get(buffer);
            System.out.println("Received message from client : " + new String(buffer));
            inputBuffer.flip();
            socketChannel.register(selector, SelectionKey.OP_WRITE, inputBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
