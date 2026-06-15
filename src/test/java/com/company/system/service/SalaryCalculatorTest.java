package com.company.system.service;

import com.company.system.exceptions.InvalidPaymentException;
import com.company.system.exceptions.InvalidSalaryException;
import com.company.system.models.Salary;
import com.company.system.models.dto.SalaryCalculationRequestDto;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SalaryCalculatorTest {

    @Test
    void calculatesSalaryWithVacationAndOvertime() {
        SalaryCalculationRequestDto request = new SalaryCalculationRequestDto(
                7,
                3,
                2200.0,
                20,
                2,
                160.0,
                4.0,
                100.0,
                50.0,
                Date.valueOf(LocalDate.of(2026, 6, 30))
        );

        Salary salary = SalaryCalculator.calculate(request);

        assertEquals(100.0, salary.getDailyRate(), 0.001);
        assertEquals(75.0, salary.getOvertimePay(), 0.001);
        assertEquals(2275.0, salary.getGrossSalary(), 0.001);
        assertEquals(2325.0, salary.getNetSalary(), 0.001);
    }

    @Test
    void rejectsMoreThanStandardPaidDays() {
        SalaryCalculationRequestDto request = validRequest(20, 3);

        assertThrows(InvalidPaymentException.class, () -> SalaryCalculator.calculate(request));
    }

    @Test
    void rejectsNegativeSalaryInputs() {
        SalaryCalculationRequestDto request = new SalaryCalculationRequestDto(
                1,
                1,
                -1.0,
                20,
                0,
                160.0,
                0.0,
                0.0,
                0.0,
                Date.valueOf(LocalDate.of(2026, 6, 30))
        );

        assertThrows(InvalidSalaryException.class, () -> SalaryCalculator.calculate(request));
    }

    private static SalaryCalculationRequestDto validRequest(int workedDays, int vacationDays) {
        return new SalaryCalculationRequestDto(
                1,
                1,
                2200.0,
                workedDays,
                vacationDays,
                160.0,
                0.0,
                0.0,
                0.0,
                Date.valueOf(LocalDate.of(2026, 6, 30))
        );
    }
}
