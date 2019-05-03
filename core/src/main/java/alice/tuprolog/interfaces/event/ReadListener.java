package alice.tuprolog.interfaces.event;

import alice.tuprolog.event.ReadEvent;

import java.util.EventListener;

public interface ReadListener extends EventListener {

    public void readCalled(ReadEvent event);
}
