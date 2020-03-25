package game.field.event;

import game.field.Creature;
import game.field.FieldObj;
import game.field.GameObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by MechAviv on 3/19/2020.
 */
public abstract class EventManager2 extends Creature {
    private final Map<Integer, EventInfo> eventInfos;

    public final static Lock lock = new ReentrantLock();
    private static final AtomicInteger eventIdCounter = new AtomicInteger();
    private final static List<Event2> timers = new ArrayList<>();
    private static long lastUpdate = System.currentTimeMillis();

    public EventManager2() {
        eventInfos = new HashMap<>();
    }

    public static int setTime(EventManager2 em, long eventTime) {
        int eventSN = 0;
        lock.lock();
        try {
            eventSN = eventIdCounter.incrementAndGet();
            timers.add(new Event2(em, eventSN, eventTime));
        } finally {
            lock.unlock();
        }
        return eventSN;
    }

    public static void update(long cur) {
        List<Event2> finished = new ArrayList<>();
        lock.lock();
        try {
            if (cur - lastUpdate >= 1000) {
                for (Iterator<Event2> it = timers.iterator(); it.hasNext();) {
                    Event2 event = it.next();
                    if (event.getEventTime() - cur < 0 || event.getEventTime() == cur) {
                        finished.add(event);
                        it.remove();
                    }
                }
                lastUpdate = System.currentTimeMillis();

                for (Event2 event : finished) {
                    event.getMain().onTime(event.getEventSN());
                }
            }
        } finally {
            lock.unlock();
        }
        finished.clear();
    }

    public abstract void onTime(int eventSN);

    public Map<Integer, EventInfo> getEventInfos() {
        return eventInfos;
    }

}
