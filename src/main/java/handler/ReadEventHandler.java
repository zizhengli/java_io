package handler;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 *
 */
public class ReadEventHandler implements EventHandler {

    private Selector demultiplexor;
    private ByteBuffer inputBuffer = ByteBuffer.allocate(2048);

    public ReadEventHandler(Selector demultiplexor) {
        this.demultiplexor = demultiplexor;
    }

    public void handleEvent(SelectionKey handle) throws Exception {
        System.out.println("===== Read Event Handler =====");
        SocketChannel socketChannel = (SocketChannel) handle.channel();
        socketChannel.read(inputBuffer); // Read data from client.client
        inputBuffer.flip();
        // Rewind the buffer to start reading from the beginning
        byte[] buffer = new byte[inputBuffer.limit()];
        inputBuffer.get(buffer);
        System.out.println("Received message from client.client : " + new String(buffer));
        inputBuffer.flip();
        socketChannel.register(demultiplexor, SelectionKey.OP_WRITE, inputBuffer);
    }
}
