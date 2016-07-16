package ru.phi.modules.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.Proxy;
import org.hibernate.validator.constraints.Email;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "Version")
@Data
@EqualsAndHashCode(callSuper = true, of = {"major", "minor", "build"})
@NoArgsConstructor
@Proxy(lazy = false)
public final class Version extends AbstractEntity {

    @NotNull
    @NonNull
    @Column(name = "major", nullable = false)
    private Long major;

    @NotNull
    @NonNull
    @Column(name = "minor", nullable = false)
    private Long minor;

    @NotNull
    @NonNull
    @Column(name = "build", nullable = false)
    private Long build;

    @Column(name = "rc", nullable = true)
    private Boolean rc;

    @Column(name = "ms", nullable = true)
    private Integer ms;

    @Email(message = "Не верно введен email адрес")
    @Column(name = "support")
    private String support;
}
