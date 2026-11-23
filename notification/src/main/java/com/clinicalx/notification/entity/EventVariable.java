
package com.clinicalx.notification.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(
        name = "event_variables",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"event_id", "variable_key"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventVariable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private NotificationEventConfig event;

    @Column(name = "variable_key", nullable = false)
    private String variableKey;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "data_type")
    private String dataType;

    @Column(nullable = false)
    private boolean required = false;

    @Column(nullable = false)
    private boolean active = true;
}
