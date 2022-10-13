package db.sql.interfaces;

import java.sql.SQLException;
import java.util.List;

public interface ManyToManyTable<T, S> extends PostgresConnectable {

    void createTable() throws SQLException;

    void insert(T t, S s) throws SQLException;

    boolean isRelated(T t, S s) throws SQLException;

    List<S> getRelated1(T t) throws SQLException;

    List<T> getRelated2(S t) throws SQLException;

    void delete(T t, S s) throws SQLException;

    void delete1(T t) throws SQLException;

    void delete2(S s) throws SQLException;
}
