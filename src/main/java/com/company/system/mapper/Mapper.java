package com.company.system.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface Mapper<T> {

    T fromResultSet(ResultSet rs) throws SQLException;
}
