package com.company.system.repository;

import java.sql.SQLException;
import java.util.List;

public interface IRepository<T> {

    List<T> findAll() throws SQLException;

    T findById(int id) throws SQLException;

    boolean save(T entity) throws SQLException;

    boolean update(T entity) throws SQLException;

    boolean deleteById(int id) throws SQLException;
}
