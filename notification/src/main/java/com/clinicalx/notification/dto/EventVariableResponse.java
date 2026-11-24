package com.clinicalx.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class EventVariableResponse {

    private Long eventId;
    private String eventName;
    private List<VariableDetails> variables;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class VariableDetails {

        private Long id;
        private String variableKey;
        private String displayName;
        private String dataType;
        private boolean required;
    }
}