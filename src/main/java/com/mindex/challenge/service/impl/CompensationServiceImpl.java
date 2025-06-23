package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * CompensationService implementation. It has a method to create a Compensation and to get all Compensations for an
 * employee.
 */
@Service
public class CompensationServiceImpl implements CompensationService {

    private static final Logger LOG = LoggerFactory.getLogger(CompensationServiceImpl.class);

    @Autowired
    CompensationRepository compensationRepository;

    @Autowired
    EmployeeService employeeService;

    @Override
    public Compensation create(Compensation compensation) {
        LOG.debug("Creating compensation [{}]", compensation);

        // Make sure the employee exists. This throws an exception if the employee is not found, so no compensation will
        // be persisted.
        employeeService.read(compensation.employeeId());

        compensation = new Compensation(
                UUID.randomUUID().toString(),
                compensation.employeeId(),
                compensation.salary(),
                compensation.effectiveDate()
        );

        compensationRepository.insert(compensation);

        return compensation;
    }

    @Override
    public List<Compensation> read(String employeeId) {
        LOG.debug("Finding compensations with employeeId [{}]", employeeId);

        return compensationRepository.findByEmployeeId(employeeId);
    }
}
