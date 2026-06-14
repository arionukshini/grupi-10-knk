package com.company.system.models.mappers;

import com.company.system.models.Contract;
import com.company.system.models.dto.ContractResponseDto;

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

    @Override
    public ContractResponseDto toDto(Contract contract) {
        return new ContractResponseDto(
                contract.getId(),
                contract.getEmployeeId(),
                contract.getEmployeeName(),
                contract.getContractType(),
                contract.getStartDate(),
                contract.getEndDate(),
                contract.getSalary(),
                contract.getStatus()
        );
    }
}
