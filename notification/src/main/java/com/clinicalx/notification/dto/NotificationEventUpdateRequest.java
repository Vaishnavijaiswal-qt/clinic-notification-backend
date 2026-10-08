package com.clinicalx.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record NotificationEventUpdateRequest(

        @NotBlank(message = "Event name is required")
        @Size(min = 3, max = 100,
                message = "Event name must be between 3 and 100 characters")
        @Pattern(
                regexp = "^[A-Za-z][A-Za-z0-9' -]*$",
                message = "Event name must start with a letter and can contain letters, numbers, spaces, apostrophe and hyphen"
        )
        String eventName,

        @NotBlank(message = "Description is required")
        @Size(max = 500,
                message = "Description must not exceed 500 characters")
        String description
) {
}