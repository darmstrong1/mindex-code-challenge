package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.error.ApiError;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for CompensationServiceImpl
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationServiceImplTest {

    String compensationUrl;
    String compensationIdUrl;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        compensationUrl = "http://localhost:" + port + "/compensation";
        compensationIdUrl = "http://localhost:" + port + "/compensation/";
    }

    @Test
    public void testCreateRead() {

        // Put in 3 compensations for John Lennon.
        var expectedCompensations = new ArrayList<Compensation>();
        var employeeId = "16a596ae-edd3-4847-99fe-c4518e82c86f";
        var compensation = new Compensation(
                null,
                employeeId,
                200000,
                LocalDate.now().minusYears(2L)
        );
        var createdCompensation = restTemplate.postForEntity(compensationUrl, compensation, Compensation.class).getBody();
        assert createdCompensation != null;
        assertNotNull(createdCompensation.compensationId());
        expectedCompensations.add(createdCompensation);

        compensation = new Compensation(
                null,
                employeeId,
                220000,
                LocalDate.now().minusYears(1L)
        );
        createdCompensation = restTemplate.postForEntity(compensationUrl, compensation, Compensation.class).getBody();
        assert createdCompensation != null;
        assertNotNull(createdCompensation.compensationId());
        expectedCompensations.add(createdCompensation);

        compensation = new Compensation(
                null,
                employeeId,
                240000,
                LocalDate.now()
        );
        createdCompensation = restTemplate.postForEntity(compensationUrl, compensation, Compensation.class).getBody();
        assert createdCompensation != null;
        assertNotNull(createdCompensation.compensationId());
        expectedCompensations.add(createdCompensation);

        // Now, read all compensations for John Lennon.
        ParameterizedTypeReference<List<Compensation>> responseType = new ParameterizedTypeReference<>() {};

        var compensations = restTemplate.exchange(
                compensationIdUrl + employeeId,
                HttpMethod.GET,
                null,
                responseType).getBody();
        assertEquals(expectedCompensations, compensations);
    }

    // Make sure an invalid employeeId returns a 404.
    @Test
    public void testCreateNonexistentEmployee() {
        var employeeId = "16a596ae-edd3-4847-99fe-c4518e82c86z";
        var compensation = new Compensation(
                null,
                employeeId,
                240000,
                LocalDate.now()
        );
        var response = restTemplate.postForEntity(compensationUrl, compensation, ApiError.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}