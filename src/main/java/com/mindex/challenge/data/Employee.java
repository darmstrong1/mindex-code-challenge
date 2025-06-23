package com.mindex.challenge.data;

import java.util.Collections;
import java.util.List;

public record Employee(
    String employeeId,
    String firstName,
    String lastName,
    String position,
    String department,
    List<Employee> directReports
) {
    public Employee {
        // Ensure that directReports is never null. If not, make a copy of the list to ensure immutability.
        directReports = directReports == null ? Collections.emptyList() : List.copyOf(directReports);
    }
}
