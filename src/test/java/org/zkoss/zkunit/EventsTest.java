package org.zkoss.zkunit;

import org.junit.Test;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * The {@link Events} static class should be stubbed out.
 *
 * @author Sean Connolly
 */
public class EventsTest extends ZKTest {

    @Test
    public void shouldAlwaysReportEventsAsValid() {
        assertEquals(true, Events.isValid("hello world"));
    }

    @Test
    public void shouldNotHandleMockEventsInSend() {
        Events.sendEvent(mock(Event.class));
    }

    @Test
    public void shouldNotHandleMockEventsInEcho() {
        Events.echoEvent(mock(Event.class));
    }

    @Test
    public void shouldNotHandleMockEventsInPost() {
        Events.postEvent(mock(Event.class));
    }

    @Test
    public void shouldImmediatelyExecuteSendEvent1() {
        // Given
        Button button = new Button("Click me!");
        TriggeredListener listener = new TriggeredListener();
        button.addEventListener(Events.ON_CLICK, listener);
        // When
        Events.sendEvent(new Event(Events.ON_CLICK, button));
        // Then
        assertTrue(listener.isTriggered());
    }

    @Test
    public void shouldImmediatelyExecuteSendEvent2() {
        // Given
        Button button = new Button("Click me!");
        TriggeredListener listener = new TriggeredListener();
        button.addEventListener(Events.ON_CLICK, listener);
        // When
        Events.sendEvent(button, new Event(Events.ON_CLICK, button));
        // Then
        assertTrue(listener.isTriggered());
    }

    @Test
    public void shouldImmediatelyExecuteSendEvent3() {
        // Given
        Button button = new Button("Click me!");
        TriggeredListener listener = new TriggeredListener();
        button.addEventListener(Events.ON_CLICK, listener);
        // When
        Events.sendEvent(Events.ON_CLICK, button, "Hello world.");
        // Then
        assertTrue(listener.isTriggered());
    }

    @Test
    public void shouldImmediatelyExecuteEchoEvent1() {
        // Given
        Button button = new Button("Click me!");
        TriggeredListener listener = new TriggeredListener();
        button.addEventListener(Events.ON_CLICK, listener);
        // When
        Events.echoEvent(new Event(Events.ON_CLICK, button));
        // Then
        assertTrue(listener.isTriggered());
    }

    @Test
    public void shouldImmediatelyExecuteEchoEvent2() {
        // Given
        Button button = new Button("Click me!");
        TriggeredListener listener = new TriggeredListener();
        button.addEventListener(Events.ON_CLICK, listener);
        // When
        Events.echoEvent(Events.ON_CLICK, button, new Object());
        // Then
        assertTrue(listener.isTriggered());
    }

    @Test
    public void shouldImmediatelyExecutePostEvent1() {
        // Given
        Button button = new Button("Click me!");
        TriggeredListener listener = new TriggeredListener();
        button.addEventListener(Events.ON_CLICK, listener);
        // When
        Events.postEvent(new Event(Events.ON_CLICK, button));
        // Then
        assertTrue(listener.isTriggered());
    }

    @Test
    public void shouldImmediatelyExecutePostEvent2() {
        // Given
        Button button = new Button("Click me!");
        TriggeredListener listener = new TriggeredListener();
        button.addEventListener(Events.ON_CLICK, listener);
        // When
        Events.postEvent(Events.ON_CLICK, button, new Object());
        // Then
        assertTrue(listener.isTriggered());
    }

    private static final class TriggeredListener implements EventListener<Event> {

        private boolean triggered = false;

        @Override
        public void onEvent(Event event) {
            triggered = true;
        }

        boolean isTriggered() {
            return triggered;
        }

    }

}