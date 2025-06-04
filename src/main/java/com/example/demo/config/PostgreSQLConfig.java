package com.example.demo.config;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.boot.model.TypeContributor;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.jdbc.spi.JdbcTypeRegistry;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PostgreSQLConfig implements TypeContributor {

    @Override
    public void contribute(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
        JdbcTypeRegistry jdbcTypeRegistry = typeContributions.getTypeConfiguration()
                .getJdbcTypeRegistry();
        
        // Register enum types
        jdbcTypeRegistry.addDescriptor(SqlTypes.OTHER, jdbcTypeRegistry.getDescriptor(SqlTypes.VARCHAR));
    }
}
