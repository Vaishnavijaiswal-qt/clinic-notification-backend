package com.clinicalx.notification.entity;

import com.clinicalx.notification.enums.NotificationEvent;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "communication_preferences",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"clinic_id", "notification_event"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommunicationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "clinic_id", nullable = false)
    @JsonIgnore
    private Clinic clinic;

    @Column(name = "notification_event", nullable = false)
    private String notificationEvent;

    @Column(name = "whatsapp_enabled", nullable = false)
    private Boolean whatsappEnabled = false;

    @Column(name = "sms_enabled", nullable = false)
    private Boolean smsEnabled = false;

    @Column(name = "email_enabled", nullable = false)
    private Boolean emailEnabled = false;

    @JsonProperty("clinicId")
    public Long getClinicId() {
        return clinic != null ? clinic.getId() : null;
    }
}