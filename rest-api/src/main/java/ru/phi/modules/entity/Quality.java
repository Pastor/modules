package ru.phi.modules.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Proxy;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "Quality")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor
@Proxy(lazy = false)
public final class Quality extends AbstractEntity {

    @NotNull
    @NonNull
    @NotEmpty
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "template")
    private String template;

    @NotNull
    @NonNull
    @Column(name = "accessibility", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Accessibility accessibility;

    @JsonIgnore
    @NotNull
    @NonNull
    @PrimaryKeyJoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private User user;
}
