package org.zkoss.zkunit;

import org.junit.Test;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zul.Window;

import static org.mockito.Mockito.mock;

/**
 * The {@link Selectors} static class should be stubbed out.
 *
 * @author Sean Connolly
 */
public class SelectorsTest extends ZKTest {

    @Test
    public void shouldStubSelectorsWireComponents1() {
        Selectors.wireComponents(mock(Page.class), null, true);
    }

    @Test
    public void shouldStubSelectorsWireComponents2() {
        Selectors.wireComponents(mock(Window.class), null, true);
    }

    @Test
    public void shouldStubSelectorsWireEventListeners() {
        Selectors.wireEventListeners(mock(Window.class), null);
    }

    @Test
    public void shouldStubSelectorsWireVariables1() {
        Selectors.wireVariables(mock(Page.class), null, null);
    }

    @Test
    public void shouldStubSelectorsWireVariables2() {
        Selectors.wireVariables(mock(Window.class), null, null);
    }

}
