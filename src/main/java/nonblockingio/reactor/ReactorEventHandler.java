package nonblockingio.reactor;

import nonblockingio.handler.EventHandler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 */
@Deprecated
public class ReactorEventHandler implements Runnable {

    private final static int POOL_SIZE = 10;
    private BlockingQueue<EventHandler> handleEventQueue;
    private ExecutorService handlerThreadPool;

    public ReactorEventHandler(BlockingQueue<EventHandler> queue) {
        this.handleEventQueue = queue;
        this.handlerThreadPool = Executors.newFixedThreadPool(POOL_SIZE);
    }

    public void run() {
        int count = 0;
        while(true) {
            try {
                EventHandler eventHandle = this.handleEventQueue.take();
                //this.handlerThreadPool.execute(eventHandle);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
