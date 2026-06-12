package com.company.system.mapper;

import com.company.system.model.Contract;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ContractMapper implements Mapper<Contract> {

    @Override
    public Contract fromResultSet(ResultSet rs) throws SQLException {
        return new Contract(
                rs.getInt("id"),
                rs.getInt("employee_id"),
                rs.getString("employee_name"),
                rs.getString("contract_type"),
                rs.getDate("start_date"),
                rs.getDate("end_date"),
                rs.getDouble("salary"),
                rs.getString("status")
        );
    }
}
