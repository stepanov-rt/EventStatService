public interface EventStatService {

    /**
     * Adds event to statistics service
     *
     * @param eventTime time of event, in milliseconds
     */
    void addEvent(long eventTime);

    /**
     * Returns quantity of events in last minute
     *
     * @return quantity of events in last minute
     */
    long getEventsQuantityInLastMinute();

    /**
     * Returns quantity of events in last hour
     *
     * @return quantity of events in last hour
     */
    long getEventsQuantityInLastHour();

    /**
     * Returns quantity of events in last day
     *
     * @return quantity of events in last day
     */
    long getEventsQuantityInLastDay();

}
