package ru.phi.modules.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "Token")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {})
@NoArgsConstructor
public final class Token extends AbstractEntity {
    @NotNull
    @NonNull
    @JsonProperty("expected_at")
    private LocalDateTime expiredAt;

    @NotNull
    @NonNull
    @JsonProperty("key")
    private String key;

    @JsonIgnore
    @NotNull
    @NonNull
    @PrimaryKeyJoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private User user;
}
