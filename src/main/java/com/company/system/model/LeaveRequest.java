package com.company.system.model;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;

public class LeaveRequest {

    private final int id;
    private final int employeeId;
    private final String employeeName;
    private final String requestType;
    private final Date startDate;
    private final Date endDate;
    private final String reason;
    private final String status;
    private final String adminResponse;
    private final Timestamp requestedAt;
    private final Timestamp reviewedAt;

    public LeaveRequest(
            int id,
            int employeeId,
            String employeeName,
            String requestType,
            Date startDate,
            Date endDate,
            String reason,
            String status,
            String adminResponse,
            Timestamp requestedAt,
            Timestamp reviewedAt
    ) {
        this.id = id;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.requestType = requestType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = status;
        this.adminResponse = adminResponse;
        this.requestedAt = requestedAt;
        this.reviewedAt = reviewedAt;
    }

    public int getId() { return id; }

    public int getEmployeeId() { return employeeId; }

    public String getEmployeeName() { return employeeName; }

    public String getRequestType() { return requestType; }

    public Date getStartDate() { return startDate; }

    public Date getEndDate() { return endDate; }

    public String getReason() { return reason; }

    public String getStatus() { return status; }

    public String getAdminResponse() { return adminResponse; }

    public Timestamp getRequestedAt() { return requestedAt; }

    public Timestamp getReviewedAt() { return reviewedAt; }

    public int getWorkingDays() {
        if (startDate == null || endDate == null) {
            return 0;
        }

        LocalDate current = startDate.toLocalDate();
        LocalDate end = endDate.toLocalDate();
        int days = 0;

        while (!current.isAfter(end)) {
            DayOfWeek day = current.getDayOfWeek();
            if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
                days++;
            }
            current = current.plusDays(1);
        }

        return days;
    }
}
