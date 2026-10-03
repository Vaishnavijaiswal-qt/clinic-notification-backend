package com.clinicalx.notification.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NotificationPreferenceUpdateRequest {

    @NotNull
    private Long clientId;

    @NotNull
    private Long clinicId;

    @NotEmpty
    @Valid
    private List<NotificationPreferenceItem> preferences;
}