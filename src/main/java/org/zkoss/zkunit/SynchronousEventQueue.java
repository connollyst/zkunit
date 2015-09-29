package org.zkoss.zkunit;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;

import java.util.HashMap;
import java.util.Map;

/**
 * A synchronous {@link org.zkoss.zk.ui.event.EventQueue} for testing purposes.<br/>
 * Listeners and callbacks are executed immediately and synchronously when an {@link org.zkoss.zk.ui.event.Event} is
 * published.
 *
 * @author Sean Connolly
 */
public class SynchronousEventQueue<T extends Event> implements EventQueue<T> {

	private final Map<EventListener<T>, EventListener<T>> subscriptions = new HashMap<>();

	@Override
	public void publish(T event) {
		try {
			for(EventListener<T> listener : subscriptions.keySet()) {
				listener.onEvent(event);
				EventListener<T> callback = subscriptions.get(listener);
				if(callback != null) {
					callback.onEvent(event);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void subscribe(EventListener<T> listener) {
		subscriptions.put(listener, null);
	}

	@Override
	public void subscribe(EventListener<T> listener, EventListener<T> callback) {
		subscriptions.put(listener, callback);
	}

	@Override
	public void subscribe(EventListener<T> listener, boolean async) {
		subscriptions.put(listener, null);
	}

	@Override
	public boolean unsubscribe(EventListener<T> listener) {
		subscriptions.remove(listener);
		return true;
	}

	@Override
	public boolean isSubscribed(EventListener<T> listener) {
		return subscriptions.containsKey(listener);
	}

	@Override
	public void close() {
		// do nothing
	}

	@Override
	public boolean isClose() {
		return false;
	}

}
