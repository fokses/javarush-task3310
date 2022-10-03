package com.javarush.task.task33.task3310.strategy;

import java.sql.*;

public class JdbcStorageStrategy implements StorageStrategy {
    private final Connection connection;

    public JdbcStorageStrategy() throws SQLException {
        String url = "jdbc:h2:mem:";
        String user = "sa";
        String pass = "sa";

        connection = DriverManager.getConnection(url, user, pass);
        String createTable = "CREATE TABLE test (key INTEGER PRIMARY KEY, value TEXT NOT NULL)";

        executeUpdate(createTable);
    }

    private void executeUpdate(String sql) throws SQLException {
        Statement statement = connection.createStatement();

        statement.executeUpdate(sql);
    }

    private String getValueFromDb(Long key) {
        String query = "SELECT value FROM test WHERE key = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, key);
            boolean hasResult = statement.execute();
            if (!hasResult) return null;

            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next())
                return resultSet.getString("value");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Long getKeyFromDb(String value) {
        String query = "SELECT key FROM test WHERE value = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, value);
            boolean hasResult = statement.execute();
            if (!hasResult) return null;

            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next())
                return resultSet.getLong("key");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean containsKey(Long key) {
        return getValueFromDb(key) != null;
    }

    @Override
    public boolean containsValue(String value) {
        return getKeyFromDb(value) != null;
    }

    @Override
    public void put(Long key, String value) {
        String sql = String.format("INSERT INTO test VALUES (%s, '%s')", key.toString(), value);
        try {
            executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Long getKey(String value) {
        return getKeyFromDb(value);
    }

    @Override
    public String getValue(Long key) {
        return getValueFromDb(key);
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
