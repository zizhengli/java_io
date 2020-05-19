package nonblockingio.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 *
 */
public class WriteEventHandler implements EventHandler {

    private SelectionKey handle;

    public void setHandleEvent(SelectionKey handle) {
        this.handle = handle;
    }

    public void handle() {
        System.out.println("===== Write Event Handler =====");
        try {
            if(handle == null) {
                throw new IOException("Handle is null");
            }
            SocketChannel socketChannel = (SocketChannel) handle.channel();
            ByteBuffer inputBuffer = (ByteBuffer) handle.attachment();
            socketChannel.write(inputBuffer);
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
