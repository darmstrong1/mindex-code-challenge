package com.mindex.challenge.service;

import com.mindex.challenge.data.Compensation;

import java.util.List;

/**
 * CompensationService interface that defines the create and read method signatures.
 */
public interface CompensationService {
    Compensation create(Compensation compensation);
    List<Compensation> read(String employeeId);
}
