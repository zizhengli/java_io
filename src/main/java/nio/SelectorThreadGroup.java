package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Channel;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

/*
* The class is in charge of create I/O thread that are to accept connections, read and write from/to clients,
* and the selector group needs to handle listening port as well see bind method below.
* */
public class SelectorThreadGroup {

    private AtomicInteger groupIndex = new AtomicInteger(0);
    private ServerSocketChannel serverChannel = null;
    private SelectorThread[] selectorTreads;
    
    SelectorThreadGroup(int num) {
        selectorTreads = new SelectorThread[num];
        for (int i = 0; i < num; i++) {
            selectorTreads[i] = new SelectorThread(this);
            new Thread(selectorTreads[i]).start();
        }
    }

    public void bind(int port) {
        try {
            serverChannel = ServerSocketChannel.open(); // open a channel on server side
            serverChannel.configureBlocking(false);
            serverChannel.bind(new InetSocketAddress(port));
            // IMPORTANT: need to pick a selector thread to accept connections from clients
            nextSelector(serverChannel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void nextSelector(Channel channel) {
        /** IMPORTANT:
         * 1. Since we are in multi thread context (main thead and separate selector thread) and selector
         * thread is blocked because of selector.select(), so we need to call wakeup here to unblock the selector
         * thread.
         * 2. channel.register(selector, SelectionKey.OP_ACCEPT) will be blocked if Selector Thread execution is
         * blocked (selector.select()), so it's better that we let selector thread to register the current channel
         * in its own selector, we wake up the selector here and the channel will be registered in selector thread
         * see SelectorThread.run() step 3
         * 3. The blocking queue is used to transfer channels in turn (connections from clients) to selector thread.
         **/
        try {
            SelectorThread st = next();  //listening: pick a selector thread to accept the connection from clients
            st.getChannelQueue().put(channel);
            st.getSelector().wakeup();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private SelectorThread next() {
        int index = groupIndex.getAndIncrement() % selectorTreads.length;
        return selectorTreads[index];
    }
}
