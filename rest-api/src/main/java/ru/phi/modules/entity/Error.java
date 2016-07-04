package ru.phi.modules.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.Proxy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
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
    private String description;

    @Column(name = "stack_trace")
    @JsonIgnore
    private String trace;
}
