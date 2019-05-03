/*Castagna 06/2011 >*/
package alice.tuprolog.interfaces.event;

import alice.tuprolog.event.ExceptionEvent;

import java.util.EventListener;

public interface ExceptionListener extends EventListener {
    void onException(ExceptionEvent e);
}
/**/