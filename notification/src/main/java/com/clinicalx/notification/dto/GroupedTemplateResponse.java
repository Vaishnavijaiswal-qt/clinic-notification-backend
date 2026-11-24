package com.clinicalx.notification.dto;

import java.util.List;

public class GroupedTemplateResponse {

    private String notificationEvent;
    private List<TemplateItemResponse> templates;

    public GroupedTemplateResponse(
            String notificationEvent,
            List<TemplateItemResponse> templates) {
        this.notificationEvent = notificationEvent;
        this.templates = templates;
    }

    public String getNotificationEvent() {
        return notificationEvent;
    }

    public List<TemplateItemResponse> getTemplates() {
        return templates;
    }
}