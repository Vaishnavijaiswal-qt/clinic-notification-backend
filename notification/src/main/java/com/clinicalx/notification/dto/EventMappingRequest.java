package com.clinicalx.notification.dto;

import com.clinicalx.notification.enums.NotificationType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EventMappingRequest {

    private Long clientId;

    private Long clinicId;

    // Used for POST
    private List<EventMappingItem> mappings;

    // Used for PUT
    private Long eventId;

    private List<NotificationType> notificationTypes;

    @Getter
    @Setter
    public static class EventMappingItem {

        private Long eventId;

        private List<NotificationType> notificationTypes;
    }
}