package com.company.system.repository;

import com.company.system.model.Contract;

import java.sql.SQLException;
import java.util.List;

public interface ContractRepository {

    List<Contract> findAll() throws SQLException;

    List<Contract> findExpiringByEmployeeId(int employeeId) throws SQLException;

    Contract findLatestByEmployeeId(int employeeId) throws SQLException;

    boolean save(Contract contract) throws SQLException;

    boolean update(Contract contract) throws SQLException;

    boolean deleteById(int contractId) throws SQLException;
}
