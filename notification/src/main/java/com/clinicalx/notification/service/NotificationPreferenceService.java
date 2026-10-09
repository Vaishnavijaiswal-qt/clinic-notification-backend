
package com.clinicalx.notification.service;

import com.clinicalx.notification.dto.*;
import com.clinicalx.notification.entity.*;
import com.clinicalx.notification.exception.ResourceNotFoundException;
import com.clinicalx.notification.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationPreferenceService {

    private final ClinicRepository clinicRepository;
    private final CommunicationPreferenceRepository preferenceRepository;

    public NotificationPreferenceResponse savePreferences(
            NotificationPreferenceUpdateRequest request) {

        Clinic clinic = clinicRepository.findById(request.getClinicId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Clinic not found"));

        List<CommunicationPreference> preferences =
                new ArrayList<>();

        for (NotificationPreferenceItem item : request.getPreferences()) {

            CommunicationPreference preference =
                    preferenceRepository
                            .findByClinic_IdAndNotificationEvent(
                                    clinic.getId(),
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

            preferences.add(preference);
        }

        List<CommunicationPreference> savedPreferences =
                preferenceRepository.saveAll(preferences);

        return buildResponse(savedPreferences);
    }

    private NotificationPreferenceResponse buildResponse(
            List<CommunicationPreference> preferences) {

        if (preferences.isEmpty()) {
            throw new IllegalArgumentException(
                    "Cannot build response without preferences");
        }

        CommunicationPreference first = preferences.get(0);
        Clinic clinic = first.getClinic();

        List<NotificationPreferenceDetailResponse> details =
                new ArrayList<>();

        for (CommunicationPreference preference : preferences) {

            NotificationPreferenceDetailResponse detail =
                    new NotificationPreferenceDetailResponse(
                            preference.getId(),
                            preference.getNotificationEvent(),
                            preference.getWhatsappEnabled(),
                            preference.getSmsEnabled(),
                            preference.getEmailEnabled()
                    );

            details.add(detail);
        }

        return new NotificationPreferenceResponse(
                first.getClientId(),
                clinic.getClient().getName(),
                clinic.getId(),
                clinic.getName(),
                details
        );
    }

    public NotificationPreferenceResponse getPreferences(Long clinicId) {

        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Clinic not found"));

        List<CommunicationPreference> preferences =
                preferenceRepository.findByClinic_Id(clinicId);

        List<NotificationPreferenceDetailResponse> details =
                new ArrayList<>();

        for (CommunicationPreference preference : preferences) {

            NotificationPreferenceDetailResponse detail =
                    new NotificationPreferenceDetailResponse(
                            preference.getId(),
                            preference.getNotificationEvent(),
                            preference.getWhatsappEnabled(),
                            preference.getSmsEnabled(),
                            preference.getEmailEnabled()
                    );

            details.add(detail);
        }

        return new NotificationPreferenceResponse(
                clinic.getClient().getId(),
                clinic.getClient().getName(),
                clinic.getId(),
                clinic.getName(),
                details
        );
    }
}