package nonblockingio.handler;

import java.nio.channels.SelectionKey;

/**
 *
 */
public interface EventHandler {
    public void setHandleEvent(SelectionKey handle);
    public void handle();
}
