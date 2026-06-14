package com.company.system.models.mappers;

import com.company.system.models.Salary;
import com.company.system.models.dto.SalaryResponseDto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SalaryMapper implements Mapper<Salary> {

    @Override
    public Salary fromResultSet(ResultSet rs) throws SQLException {
        return new Salary(
                rs.getInt("id"),
                rs.getInt("employee_id"),
                rs.getString("employee_name"),
                rs.getDouble("gross_salary"),
                rs.getDouble("bonus"),
                rs.getDouble("deductions"),
                rs.getInt("worked_days"),
                rs.getInt("vacation_days"),
                rs.getDouble("work_hours"),
                rs.getDouble("overtime_hours"),
                rs.getDouble("daily_rate"),
                rs.getDouble("overtime_pay"),
                rs.getDouble("net_salary"),
                rs.getDate("payment_date")
        );
    }

    @Override
    public SalaryResponseDto toDto(Salary salary) {
        return new SalaryResponseDto(
                salary.getId(),
                salary.getEmployeeId(),
                salary.getEmployeeName(),
                salary.getGrossSalary(),
                salary.getBonus(),
                salary.getDeductions(),
                salary.getWorkedDays(),
                salary.getVacationDays(),
                salary.getWorkHours(),
                salary.getOvertimeHours(),
                salary.getDailyRate(),
                salary.getOvertimePay(),
                salary.getNetSalary(),
                salary.getPaymentDate()
        );
    }
}
