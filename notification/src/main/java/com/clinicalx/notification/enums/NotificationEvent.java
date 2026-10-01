package com.clinicalx.notification.enums;

public enum NotificationEvent {

    PATIENT_REGISTRATION("Patient Registration"),
    PATIENT_APPOINTMENT("Patient Appointment"),
    APPOINTMENT_RESCHEDULED("Appointment Rescheduled"),
    APPOINTMENT_CANCELLED("Appointment Cancelled");

    private final String displayName;

    NotificationEvent(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}