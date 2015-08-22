# zkunit
Unit test utilities for the ZK web framework.


## Requirements
ZK 7.0.3
JUnit 4.12
Hamcrest 1.3
Powermock 1.6.1


## User Guide
zkunit has three main components which can be used individually or combined as needed.


### 1) ZKTest
ZK utilizes a number of static classes which makes testing difficult. `org.zkoss.zkunit.ZKTest` is an abstract class
which your JUnit tests extend to quickly and easily mock out these static classes.

More than just saving you the boilerplate of mocking out the static classes, some of the functionality is implemented
back in to help in particular kinds of test.


#### `Selectors`
Mocked out with no functionality implemented. Use Mockito to inject your `@Wire` and `@WireVariable` dependencies.


#### `Clients`
Mocked out with no functionality implemented. Very useful for verifying ZK interaction with the client.

Consider the following toy example:
````
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
    
    public class WinnerButton extends Button {
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
````
Here we verify that a client side alert is displayed when the user clicks the `WinnerButton`.

Other `Clients` functions that are useful to verify include `Clients.showBusy`, `Clients.clearBusy`,
`Clients.showNotification`, `Clients.scrollIntoView`, and `Clients.evalJavaScript`.


### `Executions`
Mocked out with no functionality implemented. Very useful for verifying ZK execution behavior.

Consider another toy example with the same structure as the previous:
````
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
````
Here, we are verifying that the user is redirected when they click the `LogoutButton`.

Another common use of the `Executions` static in ZK is `Executions.getCurrent()`. The uses of this are so varied,
however, we didn't provide any default mock behavior. Instead, it's best you provide your own if needed, e.g.:
````
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
````

#### `Events`

#### `EventQueues`

### 2) `ZKUtils`
`org.zkoss.zkunit.ZKUtils` is a trivially simple set of utilities for working ZK in unit tests. In a live ZK
environment, ZK will work some magic for you which isn't available in unit tests. These utilities reimplement some of
that magic to simplify testing.


#### `ZKUtils.simulateEvent`
This can be a real lifesaver as you saw in the `ZKTest` examples above. Long story short, we enable you to fire events
on Components, simulating user interactions of server side events.

Here's a simple example where we simulate the user clicking a button and verify the button's behavior:
````
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
````
Note that, if there is no `Events.ON_CLICK` event listener registered on the button, an assertion error will fail the
test giving you a meaningful failure message.


#### `ZKUtils.getGrid()`
Returns a new ZK `Grid` initialized with `Columns` and `Rows` children.

In a live ZK environment, you can declare a `grid` in your zul file and ZK will add in the `columns` and `rows` for you.
In a unit test, however, if you create a `Grid` object, it has no children; you must add the `Columns` and `Rows`
manually before interacting with the grid.


### 3) `ZKAssert`
`org.zkoss.zkunit.ZKAssert` provides a number of assertions useful for validating the state of your ZK user interface. 
Here I will demonstrate the use of a few, but it's best to familiarize yourself with the API to fully understand what
you can assert.

Here's an example where, similar to the examples above, we simulate the user clicking a button, but we then use
`ZKAssert` to verify the changes the button made to the user interface:
````
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
````
