package handler;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Created by lim20 on 12/26/2018.
 */
public class WriteEventHandler implements EventHandler {
    public void handleEvent(SelectionKey handle) throws Exception {
        System.out.println("===== Write Event Handler =====");
        SocketChannel socketChannel = (SocketChannel) handle.channel();
        ByteBuffer inputBuffer = (ByteBuffer) handle.attachment();
        socketChannel.write(inputBuffer);
        socketChannel.close();
    }
}
