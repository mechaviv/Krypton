package game.field.event;

import game.field.FieldSet;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by MechAviv on 2/2/2020.
 */
public abstract class EventManager {
    private final Map<Integer, EventInfo> eventInfos;

    public final static Lock lock = new ReentrantLock();
    private static final AtomicInteger eventIdCounter = new AtomicInteger();
    private final static List<Event> timers = new ArrayList<>();
    private static long lastUpdate = System.currentTimeMillis();
    public EventManager() {
        eventInfos = new HashMap<>();
    }

    public static void resetEvent(String fieldSetName) {
        if (fieldSetName != null && !fieldSetName.isEmpty()) {
            lock.lock();
            try {
                for (Iterator<Event> it = timers.iterator(); it.hasNext();) {
                    Event event = it.next();
                    if (event != null) {
                        if (event.getMain() instanceof FieldSet) {
                            FieldSet fieldSet = (FieldSet) event.getMain();
                            if (fieldSet.getFieldSetName().equals(fieldSetName)) {
                                it.remove();
                            }
                        }
                    }
                }
            } finally {
                lock.unlock();
            }
        }
    }

    public static int setTime(EventManager em, long eventTime) {
        int eventSN = 0;
        lock.lock();
        try {
            eventSN = eventIdCounter.incrementAndGet();
            timers.add(new Event(em, eventSN, eventTime));
        } finally {
            lock.unlock();
        }
        return eventSN;
    }

    public static void update(long cur) {
        List<Event> finished = new ArrayList<>();
        lock.lock();
        try {
            if (cur - lastUpdate >= 1000) {
                for (Iterator<Event> it = timers.iterator(); it.hasNext();) {
                    Event event = it.next();
                    if (event.getEventTime() - cur < 0 || event.getEventTime() == cur) {
                        finished.add(event);
                        it.remove();
                    }
                }
                lastUpdate = System.currentTimeMillis();

                for (Event event : finished) {
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
