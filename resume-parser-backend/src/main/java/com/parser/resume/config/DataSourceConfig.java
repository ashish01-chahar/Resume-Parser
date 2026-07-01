package com.parser.resume.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url}")
    private String mysqlUrl;

    @Value("${spring.datasource.username}")
    private String mysqlUser;

    @Value("${spring.datasource.password}")
    private String mysqlPassword;

    @Value("${spring.datasource.driver-class-name}")
    private String mysqlDriver;

    @Bean
    @Primary
    public DataSource dataSource() {
        System.out.println("=================================================");
        System.out.println("  ATTEMPTING TO CONNECT TO LOCAL MYSQL DATABASE  ");
        System.out.println("=================================================");
        try {
            // Try loading driver
            Class.forName(mysqlDriver);
            // Try opening connection to verify credentials
            try (Connection conn = DriverManager.getConnection(mysqlUrl, mysqlUser, mysqlPassword)) {
                System.out.println(">>> MYSQL CONNECTION SUCCESSFUL! <<<");
                HikariDataSource ds = new HikariDataSource();
                ds.setJdbcUrl(mysqlUrl);
                ds.setUsername(mysqlUser);
                ds.setPassword(mysqlPassword);
                ds.setDriverClassName(mysqlDriver);
                return ds;
            }
        } catch (Exception e) {
            System.err.println(">>> MYSQL CONNECTION FAILED: " + e.getMessage() + " <<<");
            System.err.println(">>> FALLING BACK TO H2 IN-MEMORY DATABASE <<<");
            System.out.println("=================================================");
            
            HikariDataSource ds = new HikariDataSource();
            ds.setJdbcUrl("jdbc:h2:mem:resume_parser;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
            ds.setUsername("sa");
            ds.setPassword("");
            ds.setDriverClassName("org.h2.Driver");
            return ds;
        }
    }
}
