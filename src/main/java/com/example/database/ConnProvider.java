package com.example.database;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class ConnProvider {
    
    private static final HikariDataSource ds;

    static {
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(System.getenv("JAVACONDB_URL"));
        cfg.setUsername(System.getenv("JAVACONDB_USERNAME"));
        cfg.setPassword(System.getenv("JAVACONDB_PASSWORD"));
        cfg.setMaximumPoolSize(10);
        ds = new HikariDataSource(cfg);
    }

    public DataSource getDataSource() {
        return ds;
    }

}