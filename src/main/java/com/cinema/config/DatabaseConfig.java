package com.cinema.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class DatabaseConfig {
    
    @Value("${MYSQL_PUBLIC_URL:}")
    private String mysqlPublicUrl;
    
    @Value("${DB_HOST:localhost}")
    private String dbHost;
    
    @Value("${DB_PORT:3306}")
    private String dbPort;
    
    @Value("${DB_NAME:cinema_db}")
    private String dbName;
    
    @Value("${DB_USERNAME:root}")
    private String dbUsername;
    
    @Value("${DB_PASSWORD:root}")
    private String dbPassword;
    
    @Bean
    public DataSource dataSource() {
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        
        // Nếu có MYSQL_PUBLIC_URL thì parse từ URL
        if (mysqlPublicUrl != null && !mysqlPublicUrl.isEmpty()) {
            try {
                URI uri = new URI(mysqlPublicUrl.replace("mysql://", "http://"));
                
                String host = uri.getHost();
                int port = uri.getPort();
                String database = uri.getPath().substring(1); // Remove leading "/"
                String[] userInfo = uri.getUserInfo().split(":");
                String username = userInfo[0];
                String password = userInfo.length > 1 ? userInfo[1] : "";
                
                String jdbcUrl = String.format(
                    "jdbc:mysql://%s:%d/%s?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                    host, port, database
                );
                
                dataSourceBuilder.url(jdbcUrl);
                dataSourceBuilder.username(username);
                dataSourceBuilder.password(password);
            } catch (URISyntaxException e) {
                throw new RuntimeException("Invalid MYSQL_PUBLIC_URL format", e);
            }
        } else {
            // Fallback: Dùng các biến riêng lẻ
            String jdbcUrl = String.format(
                "jdbc:mysql://%s:%s/%s?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                dbHost, dbPort, dbName
            );
            
            dataSourceBuilder.url(jdbcUrl);
            dataSourceBuilder.username(dbUsername);
            dataSourceBuilder.password(dbPassword);
        }
        
        dataSourceBuilder.driverClassName("com.mysql.cj.jdbc.Driver");
        return dataSourceBuilder.build();
    }
}
