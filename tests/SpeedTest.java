package com.javarush.task.task33.task3310.tests;

import com.javarush.task.task33.task3310.Helper;
import com.javarush.task.task33.task3310.Shortener;
import com.javarush.task.task33.task3310.strategy.HashBiMapStorageStrategy;
import com.javarush.task.task33.task3310.strategy.HashMapStorageStrategy;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class SpeedTest {
    public long getTimeToGetIds(Shortener shortener, Set<String> strings, Set<Long> ids) {
        Date start = new Date();

        for (String s : strings)
            ids.add(shortener.getId(s));

        Date end = new Date();

        return (end.getTime() - start.getTime());
    }

    public long getTimeToGetStrings(Shortener shortener, Set<Long> ids, Set<String> strings) {
        Date start = new Date();

        for (Long l : ids)
            strings.add(shortener.getString(l));

        Date end = new Date();

        return (end.getTime() - start.getTime());
    }
    @Test
    public void testHashMapStorage() {
        Shortener shortener1 = new Shortener(new HashMapStorageStrategy());
        Shortener shortener2 = new Shortener(new HashBiMapStorageStrategy());

        Set<String> origStrings = new HashSet<>();

        for (int i = 0; i < 10000; i++) {
            origStrings.add(Helper.generateRandomString());
        }

        Set<Long> origIds1 = new HashSet<>();
        Set<Long> origIds2 = new HashSet<>();

        long timeToGetIds1 = getTimeToGetIds(shortener1, origStrings, origIds1);
        long timeToGetIds2 = getTimeToGetIds(shortener2, origStrings, origIds2);

        Assert.assertTrue(timeToGetIds1 > timeToGetIds2);

        long timeToSetStrings1 = getTimeToGetStrings(shortener1, origIds1, new HashSet<>());
        long timeToSetStrings2 = getTimeToGetStrings(shortener2, origIds2, new HashSet<>());

        Assert.assertEquals(timeToSetStrings1, timeToSetStrings2, 30);

    }

}
