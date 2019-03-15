# EventStatService
Task:

Write a utility to collect statistics on some events. For example, the client can use it to monitor the publication of posts in the news feed. The utility provides clients  a class with an interface:
1) Add an event. The parameter is the time of the event;
2) Get the number of events in the last minute (60 seconds)
3) Get the number of events in the last hour (60 min)
4) Get the number of events for the last day (24 hours)

Events can be added to the system asynchronously at any time. The load can be 10,000 events in 1 second and 2 events per hour. The utility does not provide customers with the ability to permanently save events and statistics on events.
