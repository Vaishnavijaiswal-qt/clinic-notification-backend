
package com.clinicalx.notification.dto;

import com.clinicalx.notification.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class TemplateItemResponse {

    private Long id;
    private NotificationType notificationType;
    private String subject;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
