package org.zkoss.zkunit;

import org.junit.Test;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

import java.io.IOException;
import java.util.Collection;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * The {@link Executions} static class should be stubbed out.
 *
 * @author Sean Connolly
 */
public class ExecutionsTest extends ZKTest {

    @Test
    public void shouldStubExecutionsForward() throws IOException {
        Executions.forward("/index.zul");
    }

    @Test
    public void shouldStubExecutionsActivate() throws InterruptedException {
        Executions.activate(mock(Desktop.class));

    }

    @Test
    public void shouldStubExecutionsGetCurrent() {
        Executions.getCurrent();
    }

    @Test
    public void shouldVeryExecutionsSendRedirect() {
        // Given
        Button button = new LogoutButton();
        // When
        ZKUtils.simulateEvent(new Event(Events.ON_CLICK, button));
        // Then
        verifyStatic(times(1));
        Executions.sendRedirect("/logout.zul");
    }

    private static class LogoutButton extends Button {
        public LogoutButton() {
            super("Click me!");
            addEventListener(Events.ON_CLICK, new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    Executions.sendRedirect("/logout.zul");
                }
            });
        }
    }

    @Test
    public void shouldReturnProvidedMockExecution() {
        // Given
        String query = "ZK";
        Execution current = mock(Execution.class);
        when(current.getParameter("query")).thenReturn(query);
        when(Executions.getCurrent()).thenReturn(current);
        SearchService searchService = mock(SearchService.class);
        // When
        new SearchComposer(searchService);
        // Then
        verify(searchService, times(1)).search(query);
    }

    private static class SearchComposer extends SelectorComposer<Window> {

        public SearchComposer(SearchService searchService) {
            String query = Executions.getCurrent().getParameter("query");
            Collection results = searchService.search(query);
            // display results ..
        }
    }

    private interface SearchService {
        Collection<String> search(String query);
    }

}
