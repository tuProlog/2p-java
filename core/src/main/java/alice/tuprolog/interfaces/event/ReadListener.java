package alice.tuprolog.interfaces.event;

import alice.tuprolog.event.ReadEvent;

import java.util.EventListener;

public interface ReadListener extends EventListener {

    void readCalled(ReadEvent event);
}
