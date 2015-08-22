package org.zkoss.zkunit;

import org.junit.Test;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * The {@link Events} static class should be stubbed out.
 *
 * @author Sean Connolly
 */
public class EventsTest extends ZKTest {

    @Test
    public void shouldStubEventsEchoEvent() {
        Events.echoEvent(mock(Event.class));
    }

    @Test
    public void shouldStubEventsPostEvent() {
        Events.postEvent(mock(Event.class));
    }

    @Test
    public void shouldStubEventsSendEvent() {
        Events.sendEvent(mock(Event.class));
    }

    @Test
    public void shouldStubClientsShowNotification() {
        assertEquals(true, Events.isValid("hello world"));
    }

}
