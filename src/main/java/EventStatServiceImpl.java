import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public class EventStatServiceImpl implements EventStatService {

    private final Logger logger = Logger.getLogger(EventStatServiceImpl.class.getName());
    private ConcurrentHashMap<Long, Long> eventsHashMap;
    private final ScheduledExecutorService cleanService;

    /**
     * An implementation of EventStatService that uses ConcurrentHashMap to store events.
     * To optimize the memory, use the scheduled cleanup task. The task starts every hour.
     * For the first time, the task runs 24 hours after the EventStatsServiceImpl is initialized.
     */
    public EventStatServiceImpl() {
        this.eventsHashMap = new ConcurrentHashMap<>();
        cleanService = Executors.newSingleThreadScheduledExecutor();
        Runnable runnable = () -> {
            if (eventsHashMap.isEmpty()) {
                return;
            }
            eventsHashMap.forEach(
                    1,
                    (k, v) -> {
                        if (k < getCurrentTimeInSeconds() - Constants.SEC_IN_DAY) {
                            eventsHashMap.remove(k);
                        }
                    });
        };
        cleanService.scheduleAtFixedRate(runnable, 24, 1, TimeUnit.HOURS);
    }

    public void addEvent(long eventTime) {
        long eventTimeInSec = eventTime / 1_000;
        if (eventTimeInSec <= 0 || eventTimeInSec > getCurrentTimeInSeconds()) {
            logger.warning(String.format("Invalid event time: %s", eventTime));
            return;
        }
        eventsHashMap.compute(
                eventTimeInSec,
                (key, value) -> (value == null) ? 1L : value + 1);
    }

    public long getEventsQuantityInLastMinute() {
        return getEventsQuantity(Constants.SEC_IN_MINUTE);
    }

    public long getEventsQuantityInLastHour() {
        return getEventsQuantity(Constants.SEC_IN_HOUR);
    }

    public long getEventsQuantityInLastDay() {
        return getEventsQuantity(Constants.SEC_IN_DAY);
    }

    private long getCurrentTimeInSeconds() {
        return System.currentTimeMillis() / 1_000;
    }

    /**
     * Returns quantity of events in particular time interval
     *
     * @param timeInterval time interval, in seconds
     * @return quantity of events
     */
    private long getEventsQuantity(long timeInterval) {
        if (timeInterval <= 0) {
            logger.warning(String.format("Invalid time interval: %s", timeInterval));
            return 0;
        }
        long currentTime = getCurrentTimeInSeconds();
        AtomicLong quantity = new AtomicLong();
        eventsHashMap.forEach(
                1,
                (k, v) -> {
                    if (currentTime - timeInterval <= k && k <= currentTime) {
                        quantity.updateAndGet(q -> q + v);
                    }
                });
        return quantity.get();
    }

    /**
     * Stops deleting outdated events
     */
    public void stopClean() {
        try {
            cleanService.shutdown();
            cleanService.awaitTermination(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.warning(e.getMessage() == null ? e.toString() : e.getMessage());
        }
        if (!cleanService.isShutdown()) {
            cleanService.shutdownNow();
        }
    }
}
