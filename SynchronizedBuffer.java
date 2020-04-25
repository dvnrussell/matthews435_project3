import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SynchronizedBuffer<T> {


    private int sizeLimit;
    private ArrayList<T> bufferList;

    private ReentrantLock lock;
    private Condition notFull, notEmpty;

    public SynchronizedBuffer(int sizeLimit) {
        this.sizeLimit = sizeLimit;
        bufferList = new ArrayList<T>();
        lock = new ReentrantLock();
        notFull = lock.newCondition();
        notEmpty = lock.newCondition();
    }

    private boolean isFull() {
        return bufferList.size() == sizeLimit;
    }

    private boolean isEmpty() {
        return bufferList.size() == 0;
    }

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