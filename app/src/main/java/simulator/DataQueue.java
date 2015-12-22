package simulator;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * Created by AlessandroSam on 11.08.2015.
 */
public class DataQueue {
    // TODO: some multithreading - wait/notify, if necessary
    private static DataQueue instance = null;
    private Queue<ACDataContainer> container;
    private int timeInterval;
    private int maxTimeInterval;

    public static DataQueue getInstance() {
        if (instance == null) {
            instance = new DataQueue();
        }
        return instance;
    }

    protected DataQueue() {
        container = new LinkedList();
        timeInterval = 50;      // some defaults, used in early versions of the backend
        maxTimeInterval = 150;  // limits queue length, so the received message isn't too old
    }

    public void setTimeInterval(int millis) {
        timeInterval = millis;
        if (timeInterval > maxTimeInterval) maxTimeInterval = timeInterval;
    }

    public void setMaxTimeInterval(int millis) {
        if (millis < timeInterval) maxTimeInterval = timeInterval;
        else maxTimeInterval = millis;
    }

    public synchronized int getSize() {
        return container.size();
    }

    public synchronized boolean isAvailable() {
        return (container.size() * timeInterval < maxTimeInterval);
    }

    public synchronized ACDataContainer pop() {
        try {
            return container.remove();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public synchronized void push(ACDataContainer data) {
        if (container.size() * timeInterval < maxTimeInterval)
            if (data != null) container.add(data);
        // else drop it
    }

}
