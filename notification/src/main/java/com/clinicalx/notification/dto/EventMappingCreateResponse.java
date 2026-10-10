package com.clinicalx.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventMappingCreateResponse {

    private Long clientId;
    private String clientName;
    private Long clinicId;
    private String clinicName;
    private List<MappingItem> mappings;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MappingItem {

        private Long id;
        private Long eventId;
        private String eventName;
        private List<String> notificationTypes;
    }
}
