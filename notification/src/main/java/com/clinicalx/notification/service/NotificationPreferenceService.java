package com.clinicalx.notification.service;

import com.clinicalx.notification.dto.*;
import com.clinicalx.notification.entity.*;
import com.clinicalx.notification.exception.ResourceNotFoundException;
import com.clinicalx.notification.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationPreferenceService {

    private final ClinicRepository clinicRepository;
    private final CommunicationPreferenceRepository preferenceRepository;

    public List<CommunicationPreference> savePreferences(
            NotificationPreferenceUpdateRequest request) {

        Clinic clinic = clinicRepository.findById(request.getClinicId())
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found"));

        List<CommunicationPreference> preferences = request.getPreferences().stream()
                .map(item -> {

                    CommunicationPreference preference =
                            preferenceRepository
                                    .findByClinic_IdAndNotificationEvent((
                                            clinic.getId()),
                                            item.getNotificationEvent())
                                    .orElseGet(CommunicationPreference::new);

                    // Set Client ID
                    preference.setClientId(clinic.getClient().getId());

                    // Set Clinic
                    preference.setClinic(clinic);

                    // Set notification details
                    preference.setNotificationEvent(item.getNotificationEvent());
                    preference.setWhatsappEnabled(item.getWhatsappEnabled());
                    preference.setSmsEnabled(item.getSmsEnabled());
                    preference.setEmailEnabled(item.getEmailEnabled());

                    return preference;
                })
                .toList();

        return preferenceRepository.saveAll(preferences);
    }
}