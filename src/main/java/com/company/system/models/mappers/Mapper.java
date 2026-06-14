package com.company.system.models.mappers;

import com.company.system.models.dto.IRequestDto;
import com.company.system.models.dto.IResponseDto;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface Mapper<T> {

    T fromResultSet(ResultSet rs) throws SQLException;

    default T getFromResultSet(ResultSet rs) {
        try {
            return fromResultSet(rs);
        } catch (SQLException e) {
            throw new IllegalStateException("Could not map result set.", e);
        }
    }

    default T fromDto(IRequestDto dto) {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support fromDto.");
    }

    default T fromDto(T entity, IRequestDto dto) {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support fromDto updates.");
    }

    default IResponseDto toDto(T entity) {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support toDto.");
    }
}
