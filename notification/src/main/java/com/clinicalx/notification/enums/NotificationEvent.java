package com.clinicalx.notification.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum NotificationEvent {

    PATIENT_REGISTRATION("patient_registration", "Patient Registration"),
    PATIENT_APPOINTMENT("patient_appointment", "Patient Appointment"),
    APPOINTMENT_RESCHEDULED("appointment_rescheduled", "Appointment Rescheduled"),
    APPOINTMENT_CANCELLED("appointment_cancelled", "Appointment Cancelled");

    private final String value;
    private final String displayName;

    NotificationEvent(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }
    @JsonCreator
    public static NotificationEvent fromValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Notification event cannot be empty"
            );
        }

        for (NotificationEvent event : NotificationEvent.values()) {
            if (event.value.equalsIgnoreCase(value)
                    || event.name().equalsIgnoreCase(value)
                    || event.displayName.equalsIgnoreCase(value)) {
                return event;
            }
        }

        throw new IllegalArgumentException(
                "Unknown notification event: " + value
        );
    }


}