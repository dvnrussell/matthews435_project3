import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents a buffer whose actions (add and remove) are performed atomically,
 * allowing multiple threads to operate with one set of data
 */
public class SynchronizedBuffer<T> {


    private int sizeLimit;
    private ArrayList<T> bufferList;

    private ReentrantLock lock;
    private Condition notFull, notEmpty;

    /**
     * Constructor
     * @param sizeLimit the maximum size of the buffer object
     */
    public SynchronizedBuffer(int sizeLimit) {
        this.sizeLimit = sizeLimit;
        bufferList = new ArrayList<T>();
        lock = new ReentrantLock();
        notFull = lock.newCondition();
        notEmpty = lock.newCondition();
    }

    /**
     * Checks if the number of items in the buffer is the same as the size limit
     * @return true when the number of items equals the buffer's size limit
     */
    private boolean isFull() {
        return bufferList.size() == sizeLimit;
    }

    /**
     * Checks if the number of items in the buffer is 0
     * @return true when the number of items equals 0
     */
    private boolean isEmpty() {
        return bufferList.size() == 0;
    }

    /**
     * Adds an item to the buffer atomically
     * @param item the object/data to be added to the buffer
     * @throws InterruptedException throws if the atomic action is interrupted
     */
    public void add(T item) throws InterruptedException {
        lock.lock();
        try {
            while(isFull()) {
                notFull.await();
            }
            bufferList.add(item);
        } catch (InterruptedException e) {
            lock.unlock();
            throw e;
        }

        notEmpty.signalAll();

        lock.unlock();
    }

    /**
     * Removes an item from the buffer atomically and returns it
     * @param item the object/data to be removed from the buffer
     * @return the item removed from the buffer
     * @throws InterruptedException throws if the atomic action is interrupted
     */
    public T remove() throws InterruptedException {
        lock.lock();
        T item = null;
        try {
            while(isEmpty()) {
                notEmpty.await();
            }
            item = bufferList.remove(0);
        } catch (InterruptedException e) {
            lock.unlock();
            throw e;
        }

        notFull.signalAll();

        lock.unlock();
        return item;
    }

}