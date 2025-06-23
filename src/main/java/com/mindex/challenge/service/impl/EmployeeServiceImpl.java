package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee = new Employee(
                UUID.randomUUID().toString(),
                employee.firstName(),
                employee.lastName(),
                employee.position(),
                employee.department(),
                employee.directReports()
        );
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Creating employee with id [{}]", id);

        var employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        // I decided against this because of potential performance issues (N+1 query problem, network overhead, etc)
//        // We'll fill out all the direct reports if there are any.
//        var directReports = employee.directReports();
//        if(!directReports.isEmpty()) {
//            var fullReports = directReports.stream().map(emp -> read(emp.employeeId())).toList();
//            employee = new Employee(
//                    employee.employeeId(),
//                    employee.firstName(),
//                    employee.lastName(),
//                    employee.position(),
//                    employee.department(),
//                    fullReports);
//        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

    @Override
    public ReportingStructure numberOfReports(String id) {
        LOG.debug("Getting number of reports for employee with id of [{}]", id);

        Employee employee = read(id);

        // Employee can't be null here. If null, read() throws an exception.
        return new ReportingStructure(
                employee,
                totalReports(employee) // directReports will never be null.
        );
    }

    private long totalReports(Employee emp) {
        var directReports = emp.directReports();
        // Use streams and recursion to get the total number of reports.
        return directReports.size() +
                directReports.stream()
                        // I need to get the employee of each employee id since this is for total, not just direct reports.
                        .map(e -> read(e.employeeId()))
                        .mapToLong(this::totalReports)
                        .sum();
    }
}
