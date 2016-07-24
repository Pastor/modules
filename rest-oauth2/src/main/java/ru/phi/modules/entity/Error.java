package ru.phi.modules.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "Error")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Proxy(lazy = false)
public final class Error extends AbstractEntity {
    @NotNull
    @NonNull
    @Column(name = "code", nullable = false)
    @JsonProperty("code")
    private String code;

    @NotNull
    @NonNull
    @JsonProperty("description")
    @Column(name = "description", length = 4096)
    private String description;

    @Column(name = "status")
    @JsonProperty("status")
    private String status;

    @Column(name = "stack_trace", length = 4096)
    @JsonIgnore
    private String trace;

    @JsonIgnore
    @PrimaryKeyJoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private User user;
}
