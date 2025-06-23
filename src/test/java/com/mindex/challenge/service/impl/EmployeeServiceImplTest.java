package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;
    private String numberOfReportsUrl;

    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        numberOfReportsUrl = "http://localhost:" + port + "/employee/numberOfReports/{id}";
    }

    @Test
    public void testCreateReadUpdate() {
        Employee testEmployee = new Employee(
                null,
                "John",
                "Doe",
                "Developer",
                "Engineering",
                Collections.emptyList()
        );

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        assert createdEmployee != null;
        assertNotNull(createdEmployee.employeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee);


        // Read checks
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.employeeId()).getBody();
        assert readEmployee != null;
        assertEquals(createdEmployee.employeeId(), readEmployee.employeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);


        // Update checks
        readEmployee = new Employee(
                readEmployee.employeeId(),
                readEmployee.firstName(),
                readEmployee.lastName(),
                "Development Manager",
                readEmployee.department(),
                readEmployee.directReports()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee updatedEmployee =
                restTemplate.exchange(employeeIdUrl,
                        HttpMethod.PUT,
                        new HttpEntity<Employee>(readEmployee, headers),
                        Employee.class,
                        readEmployee.employeeId()).getBody();

        assert updatedEmployee != null;
        assertEmployeeEquivalence(readEmployee, updatedEmployee);
    }

    @Test
    public void testNumberOfReports() {
        // John Lennon has four total reports, so test that.
        var id = "16a596ae-edd3-4847-99fe-c4518e82c86f";
        var expectedEmp = restTemplate.getForEntity(employeeIdUrl, Employee.class, id).getBody();
        assert expectedEmp != null;
        var empWithAllReports = getEmpWithAllReports(expectedEmp);
        var report = restTemplate.getForEntity(numberOfReportsUrl, ReportingStructure.class, id).getBody();
        assert report != null;
        assertReportingStructureEquivalence(expectedEmp, empWithAllReports, report);

        // Pete Best has no reports. Verify this.
        id  = "62c1084e-6e34-4630-93fd-9153afb65309";
        expectedEmp = restTemplate.getForEntity(employeeIdUrl, Employee.class, id).getBody();
        assert expectedEmp != null;
        empWithAllReports = getEmpWithAllReports(expectedEmp);
        report = restTemplate.getForEntity(numberOfReportsUrl, ReportingStructure.class, id).getBody();
        assert report != null;
        assertReportingStructureEquivalence(expectedEmp, empWithAllReports, report);

        // Ringo Starr has 2 total reports, who are direct reports.
        id = "03aa1462-ffa9-4978-901b-7c001562cf6f";
        expectedEmp = restTemplate.getForEntity(employeeIdUrl, Employee.class, id).getBody();
        assert expectedEmp != null;
        empWithAllReports = getEmpWithAllReports(expectedEmp);
        report = restTemplate.getForEntity(numberOfReportsUrl, ReportingStructure.class, id).getBody();
        assert report != null;
        assertReportingStructureEquivalence(expectedEmp, empWithAllReports, report);
    }

    // Get a "recursive" employee, that has all directReports and all their directReports, so we can test to make sure
    // number of reports is correct.
    private Employee getEmpWithAllReports(Employee emp) {
        var reports = emp.directReports().stream()
                .map(e -> getEmpWithAllReports(
                        Objects.requireNonNull(restTemplate.getForEntity(employeeIdUrl, Employee.class,
                                e.employeeId()).getBody()))).toList();
        return new Employee(
                emp.employeeId(),
                emp.firstName(),
                emp.lastName(),
                emp.position(),
                emp.department(),
                reports
        );
    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.firstName(), actual.firstName());
        assertEquals(expected.lastName(), actual.lastName());
        assertEquals(expected.department(), actual.department());
        assertEquals(expected.position(), actual.position());
    }

    private static void assertReportingStructureEquivalence(Employee expectedEmp, Employee empFullReports, ReportingStructure actual) {
        assertEmployeeEquivalence(expectedEmp, actual.employee());
        var expected = new ReportingStructure(expectedEmp, totalReports(empFullReports));
        assertEquals(expected, actual);
    }

    // We'll get the count of reports in the test cases the old-fashioned way so we're not testing it by using streams,
    // the same way it was implemented.
    private static long totalReports(Employee emp) {
        long count = emp.directReports().size();

        for(var e: emp.directReports()) {
            count += totalReports(e);
        }

        return count;
    }
}
