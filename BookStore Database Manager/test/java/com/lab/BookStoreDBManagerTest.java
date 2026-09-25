package com.lab;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class BookStoreDBManagerTest {
    @Test
    public void schemaOperationsIssueExpectedSql() {
        List<String> sql = new ArrayList<>();
        Connection connection = connectionRecording(sql);

        BookStoreDBManager.createDatabase(connection);
        BookStoreDBManager.createTable(connection);
        BookStoreDBManager.addGenreColumn(connection);
        BookStoreDBManager.modifyPricePrecision(connection);
        BookStoreDBManager.addPublishedDateColumn(connection);
        BookStoreDBManager.dropAuthorColumn(connection);

        assertTrue(sql.contains("CREATE DATABASE IF NOT EXISTS BookstoreDB"));
        assertTrue(sql.contains("USE BookstoreDB"));
        assertTrue(sql.contains("ALTER TABLE books ADD COLUMN genre VARCHAR(100)"));
        assertTrue(sql.contains("ALTER TABLE books MODIFY COLUMN price DECIMAL(8, 2)"));
        assertTrue(sql.contains("ALTER TABLE books ADD COLUMN published_date DATE "));
        assertTrue(sql.contains("ALTER TABLE books DROP COLUMN author  "));
    }

    private static Connection connectionRecording(List<String> sql) {
        Statement statement = (Statement) Proxy.newProxyInstance(
                BookStoreDBManagerTest.class.getClassLoader(),
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
                BookStoreDBManagerTest.class.getClassLoader(),
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
