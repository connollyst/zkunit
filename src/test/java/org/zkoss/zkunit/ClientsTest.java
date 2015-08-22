package org.zkoss.zkunit;

import org.junit.Test;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

/**
 * The {@link Clients} static class should be stubbed out.
 *
 * @author Sean Connolly
 */
public class ClientsTest extends ZKTest {

    @Test
    public void shouldStubClientsAlert() {
        Clients.alert("hello world");
    }

    @Test
    public void shouldStubClientsClearBusy() {
        Clients.clearBusy();
    }

    @Test
    public void shouldStubClientsShowNotification() {
        Clients.showNotification("hello world");
    }

    @Test
    public void shouldStubClientsScrollIntoView() {
        Clients.scrollIntoView(mock(Label.class));
    }

    @Test
    public void shouldStubClientsEvalJavaScript() {
        Clients.evalJavaScript("alert('hello world')");
    }

    @Test
    public void shouldVeryClientsAlert() {
        // Given
        Button button = new WinnerButton();
        // When
        ZKUtils.simulateEvent(new Event(Events.ON_CLICK, button));
        // Then
        verifyStatic(times(1));
        Clients.alert("You win!");
    }

    private static class WinnerButton extends Button {
        public WinnerButton() {
            super("Click me!");
            addEventListener(Events.ON_CLICK, new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    Clients.alert("You win!");
                }
            });
        }
    }

}
