package reactor;

import handler.EventHandler;

import java.nio.channels.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class Acceptor implements Runnable {

    private Map<Integer, EventHandler> registeredHandlers;
    private Selector selector;
    private BlockingQueue<EventHandler> handlerTaskQueue;

    public Acceptor(BlockingQueue<EventHandler> queue) throws Exception {
        this.handlerTaskQueue = queue;
        this.selector = Selector.open();
        this.registeredHandlers = new ConcurrentHashMap<Integer, EventHandler>();
    }

    public void registerEventHandler(int eventType, EventHandler eventHandler) {
        registeredHandlers.put(eventType, eventHandler);
    }

    public void registerChannel(int eventType, SelectableChannel channel) throws Exception {
        channel.register(selector, eventType);
    }

    public Selector getSelector() {
        return selector;
    }

    public void run() {
        while (true) {
            try {
                selector.select();
                Set<SelectionKey> readyHandles = selector.selectedKeys();
                Iterator<SelectionKey> handleIterator = readyHandles.iterator();
                while (handleIterator.hasNext()) {
                    SelectionKey handle = handleIterator.next();
                    handleIterator.remove(); // Remove the current handle
                    if (handle.isAcceptable()) {
                        EventHandler handler = registeredHandlers.get(SelectionKey.OP_ACCEPT);
                        handler.setHandleEvent(handle);
                        handler.handle();
                    }
                    if (handle.isReadable()) {
                        EventHandler handler = registeredHandlers.get(SelectionKey.OP_READ);
                        handler.setHandleEvent(handle);
                        handler.handle();
                    }
                    if (handle.isWritable()) {
                        EventHandler handler = registeredHandlers.get(SelectionKey.OP_WRITE);
                        handler.setHandleEvent(handle);
                        handler.handle();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}