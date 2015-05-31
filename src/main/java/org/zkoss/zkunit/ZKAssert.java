package org.zkoss.zkunit;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.HtmlNativeComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Rows;

/**
 * A set of utilities for asserting the state of ZK user interfaces.<br/>
 * ZK's POJO model makes it very easy for us to programmatically build, interact with, and examine user interface
 * structures. These utilities handle common such actions.
 *
 * @author Sean Connolly
 */
public class ZKAssert {

	/**
	 * Assert that the component is stubbed out.
	 *
	 * @param component the component under assertion
	 */
	public static void assertStubonly(Component component) {
		assertEquals("true", component.getStubonly());
	}

	/**
	 * Assert that the component has the expected style class.
	 *
	 * @param component the parent under assertion
	 * @param expectedStyle the expected style
	 */
	public static void assertStyle(HtmlBasedComponent component, String expectedStyle) {
		String style = expectedStyle == null ? null : expectedStyle;
		assertEquals(style, component.getStyle());
	}

	/**
	 * Assert that the component has the expected style class.
	 *
	 * @param component the parent under assertion
	 * @param expectedClass the expected style class
	 */
	public static void assertStyleClass(HtmlBasedComponent component, String expectedClass) {
		String style = expectedClass == null ? null : expectedClass;
		assertEquals(style, component.getSclass());
	}

	/**
	 * Assert that the {@code container} has no children.
	 *
	 * @param container the parent under assertion
	 */
	public static void assertHasNoChildren(Component container) {
		List<Component> children = container.getChildren();
		assertTrue("expected no children, found: " + children, children.isEmpty());
	}

	public static <T> T assertHasChildOfType(Component container, Class<T> expectedClass) {
		List<Component> children = container.getChildren();
		assertThat("container should have at least one child", children.size(), greaterThan(0));
		for(Component child : children) {
			if(expectedClass.isInstance(child)) {
				return expectedClass.cast(child);
			}
		}
		fail("container doesn't contain a " + expectedClass.getSimpleName());
		return null;
	}

	public static <T> void assertHasNoChildOfType(Component container, Class<T> expectedClass) {
		List<Component> children = container.getChildren();
		for(Component child : children) {
			if(expectedClass.isInstance(child)) {
				fail("container contain a " + expectedClass.getSimpleName());
			}
		}
	}

	/**
	 * Assert the only child of the given {@code container} is of the Class {@code expectedClass} and return it.
	 *
	 * @param container the parent under assertion
	 * @param expectedClass the Class the container's only child should be
	 * @return the only child, cast to it's true type
	 */
	public static <T> T assertOnlyChildIsOfType(Component container, Class<T> expectedClass) {
		List<Component> children = container.getChildren();
		assertEquals("should have one child (" + children + ") ", 1, children.size());
		return assertChildIsOfType(container, 0, expectedClass);
	}

	/**
	 * Assert the first child of the given {@code container} is of the Class {@code expectedClass} and return it.<br/>
	 * Note, we do not make any assertions regarding the rest of the container's children, if there are any.
	 *
	 * @param container the parent under assertion
	 * @param expectedClass the Class the container's first child should be
	 * @return the child, cast to their true type
	 */
	public static <T> T assertFirstChildIsOfType(Component container, Class<T> expectedClass) {
		return assertChildIsOfType(container, 0, expectedClass);
	}

	/**
	 * Assert the child at the specified index of the given {@code container} is of the Class {@code expectedClass} and
	 * return it.<br/>
	 * Note, we do not make any assertions regarding the rest of the container's children, if there are any.
	 *
	 * @param container the parent under assertion
	 * @param index the child index
	 * @param expectedClass the Class the container's first child should be
	 * @return the child, cast to their true type
	 */
	public static <T> T assertChildIsOfType(Component container, int index, Class<T> expectedClass) {
		List<Component> children = container.getChildren();
		assertTrue("expected at least " + (index + 1) + " children, found " + children.size(), children.size() > index);
		Component child = children.get(index);
		assertThat("child is of wrong class", child, instanceOf(expectedClass));
		return expectedClass.cast(child);
	}

	/**
	 * Assert that <i>all</i> of the children of the given {@code container} are of the Class {@code expectedClass} and
	 * that there are exactly {@code expectedCount} of them.
	 *
	 * @param container the parent under assertion
	 * @param expectedClass the Class all of the container's children should be
	 * @param expectedCount the number of children expected
	 * @return the children, cast to their true type
	 */
	public static <T> List<T> assertChildrenAreOfType(Component container, Class<T> expectedClass, int expectedCount) {
		List<T> childList = new ArrayList<T>();
		List<Component> children = container.getChildren();
		assertEquals("container has the incorrect number of children", expectedCount, children.size());
		for(Component nextChild : children) {
			childList.add(expectedClass.cast(nextChild));
		}
		return childList;
	}

	/**
	 * For efficiency, we often using 'native HTML components'.<br/>
	 * Let's assert the only child of the given {@code container} is a {@link HtmlNativeComponent} of the expected
	 * {@code type}.
	 *
	 * @param container the parent under assertion
	 * @param type the type of the expected child
	 * @return the only child, cast to an {@link HtmlNativeComponent}
	 */
	public static HtmlNativeComponent assertOnlyChildIsNativeHtmlComponent(Component container, String type) {
		List<Component> children = container.getChildren();
		assertEquals("container should only have one child", 1, children.size());
		Component onlyChild = children.get(0);
		return getNativeHtmlComponent(onlyChild, type);
	}

	/**
	 * Assert that <i>all</i> of the children of the given {@code container} are {@link HtmlNativeComponent} objects
	 * with the expected tag type and that there are exactly {@code expectedCount} of them.
	 *
	 * @param container the parent under assertion
	 * @param type the html tag all of the container's children should be
	 * @param expectedCount the number of children expected
	 * @return the children, cast to their true type
	 */
	public static List<HtmlNativeComponent> assertChildrenAreNativeHtmlComponents(Component container, String type,
			int expectedCount) {
		List<HtmlNativeComponent> childList = new ArrayList<>();
		List<Component> children = container.getChildren();
		assertEquals("container has the incorrect number of children", expectedCount, children.size());
		for(Component nextChild : children) {
			childList.add(getNativeHtmlComponent(nextChild, type));
		}
		return childList;
	}

	private static HtmlNativeComponent getNativeHtmlComponent(Component component, String type) {
		assertTrue("not an HtmlNativeComponent (got " + component.getClass().getSimpleName() + ")",
				component instanceof HtmlNativeComponent);
		HtmlNativeComponent nativeChild = (HtmlNativeComponent)component;
		String tag = nativeChild.getTag();
		assertEquals("HtmlNativeComponent is not of the type '" + type + "'", type, tag);
		return nativeChild;
	}

	/**
	 * Assert that an event listener for the given name is registered on the specified component.
	 *
	 * @param component the component with the listener registered
	 * @param eventName the name of the event
	 * @return the event listener
	 */
	public static EventListener assertHasEventListener(Component component, String eventName) {
		Iterator<EventListener<?>> listeners = component.getEventListeners(eventName).iterator();
		assertTrue("expected '" + eventName + "' event registered on " + component, listeners.hasNext());
		return listeners.next();
	}

	/**
	 * Assert that an event listener for the given name is registered on the specified component.
	 *
	 * @param component the component with the listener registered
	 * @param eventName the name of the event
	 * @param listenerClass the expected class of the listener
	 * @return the event listener
	 */
	public static <T extends EventListener> T assertHasEventListener(Component component, String eventName,
			Class<T> listenerClass) {
		EventListener listener = assertHasEventListener(component, eventName);
		assertTrue("expected '" + eventName + "' event registered as " + listenerClass.getName() + ", found "
				+ listener.getClass().getName(), listenerClass.isInstance(listener));
		return listenerClass.cast(listener);
	}

	/**
	 * Assert that no event listener for the given name is registered on the specified component.
	 *
	 * @param component the component with the listener registered
	 * @param eventName the name of the event
	 */
	public static void assertHasNoEventListeners(Component component, String eventName) {
		Iterator<EventListener<?>> listeners = component.getEventListeners(eventName).iterator();
		assertFalse("expected no '" + eventName + "' event registered on " + component, listeners.hasNext());
	}

	/**
	 * Simulate a ZK event.<br/>
	 * Normally this is handled by ZK but in tests we don't have an active ZK environment.
	 *
	 * @param eventName the name of the event, an event listener must be registered for this event
	 * @param target the target of the event
	 * @param data the data to send with the event
	 */
	public static void simulateEvent(String eventName, Component target, Object data) {
		simulateEvent(new Event(eventName, target, data));
	}

	@SuppressWarnings("unchecked")
	public static void simulateEvent(Event event) {
		EventListener listener = assertHasEventListener(event.getTarget(), event.getName());
		try {
			listener.onEvent(event);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Create a mock ZK {@link Grid} object for testing with.<br/>
	 * A grid created in a normal ZK environment comes with {@link Rows} and {@link Columns}, even if not explicitly
	 * defined in ZUL. However, a grid created outside any ZK environment (such as a vanilla unit test) does not have
	 * any of these. This function thus returns a grid with columns and rows initialized for us.
	 *
	 * @return an initialized grid with no columns or rows
	 */
	public static Grid createMockGrid() {
		return createMockGrid(0);
	}

	/**
	 * Create a mock ZK {@link Grid} object for testing with.<br/>
	 * A grid created in a normal ZK environment comes with {@link Rows} and {@link Columns}, even if not explicitly
	 * defined in ZUL. However, a grid created outside any ZK environment (such as a vanilla unit test) does not have
	 * any of these. This function thus returns a grid with columns and rows initialized for us.
	 *
	 * @param columns the number of columns required
	 * @return an initialized grid with no rows, but the given number of columns
	 */
	public static Grid createMockGrid(int columns) {
		Grid grid = new Grid();
		Rows rows = new Rows();
		Columns cols = new Columns();
		for(int i = 0; i < columns; i++) {
			cols.appendChild(new Column());
		}
		grid.appendChild(rows);
		grid.appendChild(cols);
		return grid;
	}

}
