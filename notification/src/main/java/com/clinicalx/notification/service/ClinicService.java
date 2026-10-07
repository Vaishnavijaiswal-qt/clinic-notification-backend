package com.clinicalx.notification.service;

import com.clinicalx.notification.dto.ClinicResponse;
import com.clinicalx.notification.repository.ClinicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClinicService {

    private final ClinicRepository clinicRepository;

    public List<ClinicResponse> getClinicsByClient(Long clientId) {

        List<ClinicResponse> responses = new ArrayList<>();

        for (var clinic : clinicRepository.findByClientIdAndActiveTrue(clientId)) {
            responses.add(
                    new ClinicResponse(clinic.getId(), clinic.getName())
            );
        }

        return responses;
    }
}