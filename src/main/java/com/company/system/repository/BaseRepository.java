package com.company.system.repository;

import java.sql.SQLException;
import java.util.List;

public abstract class BaseRepository<T> implements IRepository<T> {

    @Override
    public List<T> findAll() throws SQLException {
        throw unsupported("findAll");
    }

    @Override
    public T findById(int id) throws SQLException {
        throw unsupported("findById");
    }

    @Override
    public boolean save(T entity) throws SQLException {
        throw unsupported("save");
    }

    @Override
    public boolean update(T entity) throws SQLException {
        throw unsupported("update");
    }

    @Override
    public boolean deleteById(int id) throws SQLException {
        throw unsupported("deleteById");
    }

    private UnsupportedOperationException unsupported(String method) {
        return new UnsupportedOperationException(getClass().getSimpleName() + " does not support " + method + ".");
    }
}
