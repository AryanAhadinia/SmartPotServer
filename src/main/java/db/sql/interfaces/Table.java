package db.sql.interfaces;

import java.sql.SQLException;

public interface Table<T> extends PostgresConnectable {

    void createTable() throws SQLException;

    void insert(T instance) throws SQLException;

    T read(T partialInstance) throws SQLException;

    void update(T instance) throws SQLException;

    void delete(T instance) throws SQLException;
}
