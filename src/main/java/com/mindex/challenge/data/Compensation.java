package com.mindex.challenge.data;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

/**
 * Domain object for Compensation. Uses a manual reference (employeeId) to associate a Compensation with an Employee.
 * @param compensationId
 * @param employeeId
 * @param salary
 * @param effectiveDate
 */
public record Compensation(
        String compensationId,
        String employeeId,
        long salary,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate effectiveDate
) {
}
