package org.zkoss.zkunit;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.util.Clients;

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
@PrepareForTest({ Selectors.class, Clients.class, Events.class, EventQueues.class, Executions.class })
public abstract class ZKTest {

	private final EventQueueLookupAnswer lookupAnswer = new EventQueueLookupAnswer();
	private final Map<String, EventQueue> queues = new HashMap<>();

	@Before
	public void mockZKEnvironment() {
		queues.clear();
		mockStatic(Selectors.class);
		mockStatic(Clients.class);
		mockStatic(Events.class);
		mockStatic(EventQueues.class);
		mockStatic(Executions.class);
		when(Events.isValid(anyString())).thenReturn(true);
		when(EventQueues.lookup(anyString())).thenAnswer(lookupAnswer);
		when(EventQueues.lookup(anyString(), anyBoolean())).thenAnswer(lookupAnswer);
		when(EventQueues.lookup(anyString(), anyString(), anyBoolean())).thenAnswer(lookupAnswer);
	}

	private class EventQueueLookupAnswer implements Answer<EventQueue> {

		@Override
		public EventQueue answer(InvocationOnMock invocationOnMock) throws Throwable {
			String name = invocationOnMock.getArguments()[0].toString();
			if(!queues.containsKey(name)) {
				queues.put(name, new SynchronousEventQueue());
			}
			return queues.get(name);
		}
	}

}
