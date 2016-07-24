package ru.phi.modules;

import ru.phi.modules.entity.UserRole;

public interface Constants {
    String STORAGE_HIBERNATE_DIALECT = "storage.hibernate.dialect";
    String STORAGE_HIBERNATE_DDL = "storage.hibernate.ddl";
    String STORAGE_HIBERNATE_SHOW_SQL = "storage.hibernate.sow_sql";

    String STORAGE_JPA_PACKAGES_SCAN = "storage.jpa.packages_scan";

    String STORAGE_JDBC_URL = "storage.jdbc.url";
    String STORAGE_JDBC_DRIVER = "storage.jdbc.driver_class";
    String STORAGE_JDBC_USERNAME = "storage.jdbc.username";
    String STORAGE_JDBC_PASSWORD = "storage.jdbc.password";

    String CLIENT_ID = "public_client";
    String CLIENT_SECRET = "123456";
    String[] CLIENT_SCOPES = {
            "empty",
            "read:settings",
            "write:settings",
            "read:profile",
            "write:profile",
            "delete:quality",
            "write:quality",
            "read:statistic",
            "read:error",
            "read:ping",
            "read:user",
            "write:user",
            "write:category",
            "delete:category"
    };
    String[] CLIENT_AUTHORISES = {
            UserRole.admin.name(),
            UserRole.user.name(),
            UserRole.content.name(),
            UserRole.device.name()
    };

    String RESOURCE_ID = "rest-resource";
}
