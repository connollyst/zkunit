package org.zkoss.zkunit;

import org.junit.Test;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * The {@link EventQueues} static class should be stubbed out.
 *
 * @author Sean Connolly
 */
public class EventQueuesTest extends ZKTest {

    private final String randomId() {
        return UUID.randomUUID().toString();
    }

    @Test
    public void shouldReturnEventQueueOnLookup() {
        // When
        EventQueue queue = EventQueues.lookup(randomId());
        // Then
        assertNotNull(queue);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldProcessEventQueueSynchronously() throws Exception {
        // Given
        EventListener listener = mock(EventListener.class);
        EventQueue queue = EventQueues.lookup(randomId());
        queue.subscribe(listener);
        // When
        Event event = mock(Event.class);
        queue.publish(event);
        // Then
        verify(listener, times(1)).onEvent(event);
    }

    @Test
    public void shouldReportUnknownEventQueueDoesntExist() throws Exception {
        // When
        boolean exists = EventQueues.exists(randomId());
        // Then
        assertFalse(exists);
    }

    @Test
    public void shouldReportKnownEventQueueExists() throws Exception {
        // Given
        String id = randomId();
        EventQueues.lookup(id);
        // When
        boolean exists = EventQueues.exists(id);
        // Then
        assertTrue(exists);
    }

    @Test
    public void shouldReportRemovedEventQueueDoesntExist() throws Exception {
        // Given
        String id = randomId();
        EventQueues.lookup(id);
        EventQueues.remove(id);
        // When
        boolean exists = EventQueues.exists(id);
        // Then
        assertFalse(exists);
    }

}
