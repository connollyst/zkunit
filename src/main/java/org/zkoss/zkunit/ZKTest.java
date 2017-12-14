package org.zkoss.zkunit;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Filedownload;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.reflect.internal.WhiteboxImpl.findMethod;

/**
 * <p>
 * Tests which involve interactions with ZK components should extend this class. It mocks out the static classes used by
 * an active ZK environment.
 * </p>
 * <p>
 * Note that {@link Selectors} is used a lot to autowire UI components and Spring managed beans. In tests extending this
 * class, those dependencies will not be injected and will need to be provided directly.
 * </p>
 *
 * @author Sean Connolly
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Selectors.class, Sessions.class, Clients.class, Events.class, EventQueues.class, Executions.class, Filedownload.class})
public abstract class ZKTest {

    private static final Answer<Void> IMMEDIATE_ANSWER = new ImmediateEventAnswer();

    private final EventQueueExistsAnswer existsAnswer = new EventQueueExistsAnswer();
    private final EventQueueLookupAnswer lookupAnswer = new EventQueueLookupAnswer();
    private final EventQueueRemoveAnswer removeAnswer = new EventQueueRemoveAnswer();

    private final Map<String, EventQueue> queues = new HashMap<>();

    @Before
    public void mockZKEnvironment() throws Exception {
        mockStatic(Selectors.class);
        mockStatic(Sessions.class);
        mockStatic(Clients.class);
        mockStatic(Events.class);
        mockStatic(EventQueues.class);
        mockStatic(Executions.class);
        mockStatic(Filedownload.class);
        mockEventQueues();
        mockEvents();
    }

    private void mockEventQueues() {
        queues.clear();
        when(EventQueues.exists(anyString())).thenAnswer(existsAnswer);
        when(EventQueues.lookup(anyString())).thenAnswer(lookupAnswer);
        when(EventQueues.lookup(anyString(), anyBoolean())).thenAnswer(lookupAnswer);
        when(EventQueues.lookup(anyString(), anyString(), anyBoolean())).thenAnswer(lookupAnswer);
        when(EventQueues.remove(anyString())).thenAnswer(removeAnswer);
        when(EventQueues.remove(anyString(), anyString())).thenAnswer(removeAnswer);
        when(EventQueues.remove(anyString(), any(WebApp.class))).thenAnswer(removeAnswer);
        when(EventQueues.remove(anyString(), any(Session.class))).thenAnswer(removeAnswer);
    }

    private void mockEvents() throws Exception {
        when(Events.isValid(anyString())).thenReturn(true);
        handleEventImmediately("sendEvent", Event.class);
        Events.sendEvent(any(Event.class));
        handleEventImmediately("sendEvent", Component.class, Event.class);
        Events.sendEvent(any(Component.class), any(Event.class));
        handleEventImmediately("sendEvent", String.class, Component.class, Object.class);
        Events.sendEvent(anyString(), any(Component.class), anyObject());
        handleEventImmediately("echoEvent", Event.class);
        Events.echoEvent(any(Event.class));
        handleEventImmediately("echoEvent", String.class, Component.class, Object.class);
        Events.echoEvent(anyString(), any(Component.class), anyObject());
        handleEventImmediately("postEvent", Event.class);
        Events.postEvent(any(Event.class));
        handleEventImmediately("postEvent", String.class, Component.class, Object.class);
        Events.postEvent(anyString(), any(Component.class), anyObject());
    }

    private void handleEventImmediately(String methodName, Class<?>... parameterTypes) throws Exception {
        doAnswer(IMMEDIATE_ANSWER).when(Events.class, findMethod(Events.class, methodName, parameterTypes));
    }

    private class EventQueueExistsAnswer implements Answer<Boolean> {

        @Override
        public Boolean answer(InvocationOnMock invocationOnMock) {
            String name = invocationOnMock.getArguments()[0].toString();
            return queues.containsKey(name);
        }
    }

    private class EventQueueLookupAnswer implements Answer<EventQueue> {
        @Override
        public EventQueue answer(InvocationOnMock invocationOnMock) {
            String name = invocationOnMock.getArguments()[0].toString();
            if (!queues.containsKey(name)) {
                queues.put(name, new SynchronousEventQueue());
            }
            return queues.get(name);
        }

    }

    private class EventQueueRemoveAnswer implements Answer<Boolean> {

        @Override
        public Boolean answer(InvocationOnMock invocationOnMock) {
            String name = invocationOnMock.getArguments()[0].toString();
            if (queues.containsKey(name)) {
                queues.remove(name);
            }
            return true;
        }
    }

    private static final class ImmediateEventAnswer implements Answer<Void> {

        @Override
        @SuppressWarnings("unchecked")
        public Void answer(InvocationOnMock invocation) {
            Component eventTarget = getEventTarget(invocation.getArguments());
            String eventName = getEventName(invocation.getArguments());
            Object eventData = getEventData(invocation.getArguments());
            if (eventTarget != null && eventTarget.getEventListeners(eventName) != null) {
                for (EventListener listener : eventTarget.getEventListeners(eventName)) {
                    try {
                        listener.onEvent(new Event(eventName, eventTarget, eventData));
                    } catch (Exception e) {
                        fail(e.getMessage());
                    }
                }
            }
            return null;
        }

        private Component getEventTarget(Object... arguments) {
            for (Object argument : arguments) {
                if (argument instanceof Event) {
                    return ((Event) argument).getTarget();
                } else if (argument instanceof Component) {
                    return (Component) argument;
                }
            }
            throw new RuntimeException("Could not resolve event target from arguments: " + Arrays.toString(arguments));
        }

        private String getEventName(Object... arguments) {
            for (Object argument : arguments) {
                if (argument instanceof Event) {
                    return ((Event) argument).getName();
                } else if (argument instanceof String) {
                    return (String) argument;
                }
            }
            throw new RuntimeException("Could not resolve event target from arguments: " + Arrays.toString(arguments));
        }

        private Object getEventData(Object... arguments) {
            // Reverse iterate to avoid returning the event name as the data
            for (int i = arguments.length - 1; i >= 0; i--) {
                Object argument = arguments[i];
                if (argument instanceof Event) {
                    return ((Event) argument).getData();
                } else if (!(argument instanceof Component)) {
                    return argument;
                }
            }
            return null;
        }

    }
}
