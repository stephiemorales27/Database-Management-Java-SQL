package com.lab;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class BankingSystemTest {
    @Test
    public void createDatabaseIssuesExpectedSql() throws Exception {
        List<String> sql = new ArrayList<>();
        Statement statement = (Statement) Proxy.newProxyInstance(
                getClass().getClassLoader(), new Class<?>[] { Statement.class },
                (proxy, method, args) -> {
                    if (method.getName().equals("executeUpdate")) {
                        sql.add((String) args[0]);
                        return 1;
                    }
                    return null;
                });
        Connection connection = (Connection) Proxy.newProxyInstance(
                getClass().getClassLoader(), new Class<?>[] { Connection.class },
                (proxy, method, args) -> method.getName().equals("createStatement") ? statement : null);

        BankingSystem.createDatabase(connection);

        assertTrue(sql.contains("CREATE DATABASE IF NOT EXISTS BankDB"));
    }
}
