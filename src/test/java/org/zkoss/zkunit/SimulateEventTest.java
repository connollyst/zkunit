package org.zkoss.zkunit;

import org.junit.Test;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;

import static org.junit.Assert.assertTrue;

/**
 * @author Sean Connolly
 */
public class SimulateEventTest {

    @Test
    public void shouldDisableButtonOnClick() {
        // Given
        SingleUseButton button = new SingleUseButton();
        // When
        ZKUtils.simulateEvent(new Event(Events.ON_CLICK, button));
        // Then
        assertTrue(button.isDisabled());
    }

    private static class SingleUseButton extends Button {

        private SingleUseButton() {
            super("Click me once, shame on you.");
            addEventListener(Events.ON_CLICK, new EventListener<Event>() {
                @Override
                public void onEvent(Event event) {
                    setDisabled(true);
                    setLabel("Click me twice, well, you can get clicked again.");
                }
            });
        }
    }

}
