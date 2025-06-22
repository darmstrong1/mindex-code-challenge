package com.mindex.challenge.data;

import java.util.List;

public record Employee(
    String employeeId,
    String firstName,
    String lastName,
    String position,
    String department,
    List<Employee> directReports
) {}
