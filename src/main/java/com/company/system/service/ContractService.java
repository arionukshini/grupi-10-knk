package com.company.system.service;


import com.company.system.utils.AppLogger;
import com.company.system.exceptions.DatabaseOperationException;
import com.company.system.models.Contract;
import com.company.system.repository.ContractRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ContractService {

    private static final ContractRepository contractRepository = new ContractRepository();

    public static List<Contract> getAllContracts() {
        try {
            return contractRepository.findAll();
        } catch (SQLException e) {
            AppLogger.error("Unexpected error", e);
            return new ArrayList<>();
        }
    }

    public static List<Contract> getExpiringContractsForEmployee(int employeeId) {
        try {
            return contractRepository.findExpiringByEmployeeId(employeeId);
        } catch (SQLException e) {
            AppLogger.error("Unexpected error", e);
            return new ArrayList<>();
        }
    }

    public static boolean addContract(Contract contract) {
        try {
            boolean saved = contractRepository.save(contract);
            if (!saved) {
                throw new DatabaseOperationException("exception.contract.update.notFound");
            }
            return true;
        } catch (SQLException e) {
            throw new DatabaseOperationException("exception.contract.update.database", e);
        }
    }

    public static boolean updateContract(Contract contract) {
        try {
            return contractRepository.update(contract);
        } catch (SQLException e) {
            AppLogger.error("Unexpected error", e);
            return false;
        }
    }

    public static Contract getLatestContractByEmployeeId(int employeeId) {
        try {
            return contractRepository.findLatestByEmployeeId(employeeId);
        } catch (SQLException e) {
            AppLogger.error("Unexpected error", e);
            return null;
        }
    }

    public static boolean deleteContract(int id) {
        try {
            return contractRepository.deleteById(id);
        } catch (SQLException e) {
            AppLogger.error("Unexpected error", e);
            return false;
        }
    }
}
