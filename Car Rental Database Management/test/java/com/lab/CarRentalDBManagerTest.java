package com.lab;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class CarRentalDBManagerTest {
    @Test
    public void schemaOperationsIssueExpectedSql() throws Exception {
        List<String> sql = new ArrayList<>();
        Connection connection = connectionRecording(sql);

        CarRentalDBManager.createDatabase(connection);
        CarRentalDBManager.createTables(connection);

        assertTrue(sql.contains("CREATE DATABASE IF NOT EXISTS CarRentalDB"));
        assertTrue(sql.contains("USE CarRentalDB"));
        assertTrue(sql.stream().anyMatch(statement -> statement.startsWith("CREATE TABLE IF NOT EXISTS cars")));
        assertTrue(sql.stream().anyMatch(statement -> statement.startsWith("CREATE TABLE IF NOT EXISTS customers")));
    }

    private static Connection connectionRecording(List<String> sql) {
        Statement statement = (Statement) Proxy.newProxyInstance(
                CarRentalDBManagerTest.class.getClassLoader(),
                new Class<?>[] { Statement.class },
                (proxy, method, args) -> {
                    if (method.getName().equals("executeUpdate")) {
                        sql.add((String) args[0]);
                        return 1;
                    }
                    if (method.getName().equals("close")) return null;
                    return defaultValue(method.getReturnType());
                });
        return (Connection) Proxy.newProxyInstance(
                CarRentalDBManagerTest.class.getClassLoader(),
                new Class<?>[] { Connection.class },
                (proxy, method, args) -> {
                    if (method.getName().equals("createStatement")) return statement;
                    if (method.getName().equals("close")) return null;
                    return defaultValue(method.getReturnType());
                });
    }

    private static Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) return null;
        if (type == boolean.class) return false;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == double.class) return 0D;
        if (type == float.class) return 0F;
        if (type == short.class) return (short) 0;
        if (type == byte.class) return (byte) 0;
        return '\0';
    }
}
