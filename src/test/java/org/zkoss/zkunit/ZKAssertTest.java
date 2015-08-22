package org.zkoss.zkunit;

import org.junit.Test;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Vlayout;

/**
 * @author Sean Connolly
 */
public class ZKAssertTest extends ZKTest {

    @Test
    public void shouldClearMessagesWhenClearButtonClicked() {
        // Given
        Component messagesList = new Vlayout();
        for (int i = 0; i < 10; i++) {
            messagesList.appendChild(new A("Message #" + i));
        }
        Button clearButton = new ClearMessagesButton(messagesList);
        // When
        ZKUtils.simulateEvent(new Event(Events.ON_CLICK, clearButton));
        // Then
        ZKAssert.assertHasNoChildren(messagesList);
    }

    private static class ClearMessagesButton extends Button {


        public ClearMessagesButton(final Component messagesList) {
            super("Clear Messages");
            addEventListener(Events.ON_CLICK, new EventListener<Event>() {
                @Override
                public void onEvent(Event event) {
                    messagesList.getChildren().clear();
                    Clients.showNotification("Messages cleared..");
                }
            });
        }
    }

}
