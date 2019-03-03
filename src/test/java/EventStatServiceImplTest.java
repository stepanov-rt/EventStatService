import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

public class EventStatServiceImplTest {

    private EventStatServiceImpl eventStatService;
    private final long eventsTestQuantity = 1_000_000;

    @Before
    public void setUp() {
        eventStatService = new EventStatServiceImpl();
    }

    @After
    public void tearDown() {
        eventStatService.stopClean();
    }

    @Test
    public void addSeveralEventsTest() {
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);
        for (int i = 0; i < eventsTestQuantity; i++) {
            executorService.submit(() -> eventStatService.addEvent(System.currentTimeMillis()));
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            fail(e.getMessage() == null ? e.toString() : e.getMessage());
        }
        assertEquals(eventStatService.getEventsQuantityInLastMinute(), eventsTestQuantity);
        assertEquals(eventStatService.getEventsQuantityInLastDay(), eventsTestQuantity);
        assertEquals(eventStatService.getEventsQuantityInLastHour(), eventsTestQuantity);

    }

    @Test
    public void addOneEventTest() {
        eventStatService.addEvent(System.currentTimeMillis());

        assertEquals(eventStatService.getEventsQuantityInLastMinute(), 1);
        assertEquals(eventStatService.getEventsQuantityInLastDay(), 1);
        assertEquals(eventStatService.getEventsQuantityInLastHour(), 1);
    }

    @Test
    public void addInvalidEvent() {
        eventStatService.addEvent(0);

        assertEquals(eventStatService.getEventsQuantityInLastMinute(), 0);
        assertEquals(eventStatService.getEventsQuantityInLastDay(), 0);
        assertEquals(eventStatService.getEventsQuantityInLastHour(), 0);
    }

    @Test
    public void addSeveralEventsAndGetWithDelayTest() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);
        for (int i = 0; i < eventsTestQuantity; i++) {
            executorService.submit(() -> eventStatService.addEvent(System.currentTimeMillis()));
        }
        Thread.sleep(61_000);
        for (int i = 0; i < eventsTestQuantity; i++) {
            executorService.submit(() -> eventStatService.addEvent(System.currentTimeMillis()));
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            fail(e.getMessage() == null ? e.toString() : e.getMessage());
        }

        assertEquals(eventStatService.getEventsQuantityInLastMinute(), eventsTestQuantity);
        assertEquals(eventStatService.getEventsQuantityInLastDay(), eventsTestQuantity * 2);
        assertEquals(eventStatService.getEventsQuantityInLastHour(), eventsTestQuantity * 2);
    }
}