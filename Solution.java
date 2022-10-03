package com.javarush.task.task33.task3310;

import com.javarush.task.task33.task3310.strategy.*;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Solution {
    public static void main(String[] args) throws SQLException {
        testStrategy(new JdbcStorageStrategy(), 10000);
        testStrategy(new HashMapStorageStrategy(), 10000);
        testStrategy(new OurHashMapStorageStrategy(), 10000);
        testStrategy(new FileStorageStrategy(), 100);
        testStrategy(new OurHashBiMapStorageStrategy(), 10000);
        testStrategy(new HashBiMapStorageStrategy(), 10000);
        testStrategy(new DualHashBidiMapStorageStrategy(), 10000);

    }

    public static Set<Long> getIds(Shortener shortener, Set<String> strings) {
        Set<Long> result = new HashSet<>();

        for (String string : strings)
            result.add(shortener.getId(string));

        return result;
    }

    public static Set<String> getStrings(Shortener shortener, Set<Long> keys) {
        Set<String> result = new HashSet<>();

        for (Long key : keys)
            result.add(shortener.getString(key));

        return result;
    }

    public static void testStrategy(StorageStrategy strategy, long elementsNumber) {
        Helper.printMessage(strategy.getClass().getSimpleName());
        Set<String> testSet = new HashSet<>();
        for (int i = 0; i < elementsNumber; i++) {
            testSet.add(Helper.generateRandomString());
        }

        Shortener shortener = new Shortener(strategy);

        Date startGetIds = new Date();
        Set<Long> setIds = getIds(shortener, testSet);
        Date endGetIds = new Date();
        Helper.printMessage(Long.valueOf(endGetIds.getTime() - startGetIds.getTime()).toString());

        Date startGetValues = new Date();
        Set<String> setValues = getStrings(shortener, setIds);
        Date endGetValues = new Date();
        Helper.printMessage(Long.valueOf(endGetValues.getTime() - startGetValues.getTime()).toString());

        if (testSet.equals(setValues))
            Helper.printMessage("Тест пройден.");
        else
            Helper.printMessage("Тест не пройден.");

        strategy.close();
    }

}
