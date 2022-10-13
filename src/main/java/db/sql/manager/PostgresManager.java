package db.sql.manager;

import db.sql.tables.DeviceTable;
import db.sql.tables.SubscriptionTable;
import db.sql.tables.UserTable;

import java.sql.SQLException;

public class PostgresManager {

    public static void createTables() throws SQLException {
        UserTable.getInstance().createTable();
        DeviceTable.getInstance().createTable();
        SubscriptionTable.getInstance().createTable();
    }
}
