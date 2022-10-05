package com.javarush.task.task33.task3310.strategy;

import java.util.concurrent.atomic.AtomicInteger;

public class HashMapDoubleThreadStorageStrategy implements StorageStrategy {
    static final int DEFAULT_INITIAL_CAPACITY = 16;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    Entry[] table = new Entry[DEFAULT_INITIAL_CAPACITY];
    AtomicInteger size = new AtomicInteger(0);
    int threshold = (int) (DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
    float loadFactor = DEFAULT_LOAD_FACTOR;
    Thread checkAndResizeThread;

    public HashMapDoubleThreadStorageStrategy() {
        checkAndResizeThread = new Thread(() -> {
            HashMapDoubleThreadStorageStrategy monitor = HashMapDoubleThreadStorageStrategy.this;
            while (!Thread.interrupted()) {
                synchronized (monitor) {
                    while (size.get() < threshold) {
                        try {
                            monitor.notify(); //We don't need to resize the table
                            monitor.wait(); //Let's wait
                        } catch (InterruptedException e) { return; }
                    }

                    resize(threshold * 2);
                    monitor.notify(); //Wake up main thread
                }
            }
        });

        checkAndResizeThread.start();
    }

    int hash(Long k) {
        return k.hashCode();
    }

    int indexFor(int hash, int length) {
        return hash & (length - 1);
    }

    Entry getEntry(Long key) {
        int hash = hash(key);
        int index = indexFor(hash, table.length);

        Entry next = table[index];
        if (next == null)
            return null;

        do {
            if (next.hash == hash)
                return next;
        } while ((next = next.next) != null);

        return null;
    }

    void resize(int newCapacity) {

        Entry[] newTable = new Entry[newCapacity];
        transfer(newTable);
        table = newTable;
        threshold = (int) (newCapacity * loadFactor);
    }

    void transfer(Entry[] newTable) {
        int length = newTable.length;
        for (Entry entry : table) {
            Entry e = entry;
            if (e == null)
                continue;

            do {

                Entry next = e.next;

                int index = indexFor(e.hash, length);
                e.next = newTable[index];
                newTable[index] = e;

                e = next;
            } while (e != null);
        }
    }

    void addEntry(int hash, Long key, String value, int bucketIndex) {
        createEntry(hash, key, value, bucketIndex);
        size.incrementAndGet();
    }

    void createEntry(int hash, Long key, String value, int bucketIndex) {
        Entry current = table[bucketIndex];
        table[bucketIndex] = new Entry(hash, key, value, current);
    }

    @Override
    public synchronized boolean containsKey(Long key) {

        Entry entry = getEntry(key);
        return entry != null;
    }

    @Override
    public synchronized boolean containsValue(String value) {

        for (Entry entry : table) {
            Entry next = entry;
            if (next == null) continue;

            do {
                if (next.getValue().equals(value))
                    return true;
            } while ((next = next.next) != null);
        }

        return false;
    }

    @Override
    public synchronized void put(Long key, String value) {

        Entry entry = getEntry(key);

        if (entry != null)
            entry.value = value;
        else {
            int hash = hash(key);
            addEntry(hash, key, value, indexFor(hash, table.length));
            notify(); //Notify thread to check whether we need to resize the table
            try {
                wait(); //Wait for probable resize
            } catch (InterruptedException e) { return; }
        }
    }

    @Override
    public synchronized Long getKey(String value) {
        for (Entry entry : table) {
            Entry next = entry;
            if (next == null) continue;

            do {
                if (next.getValue().equals(value))
                    return next.getKey();
            } while ((next = next.next) != null);
        }

        return null;
    }

    @Override
    public synchronized String getValue(Long key) {
        Entry entry = getEntry(key);
        return entry == null ? null : entry.getValue();
    }

    @Override
    public void close() {
        checkAndResizeThread.interrupt();
    }

    public static void main(String[] args) {
        StorageStrategy hashMap = new HashMapDoubleThreadStorageStrategy();

        for (int i = 0; i < 200; i++) {
            hashMap.put((long) i, Integer.toString(i));
        }

        hashMap.close();
    }
}
