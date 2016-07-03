package ru.phi.modules.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Proxy;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(name = "User")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"tokens"})
@ToString(exclude = {"tokens"})
@NoArgsConstructor
@Proxy(lazy = false)
public final class User extends AbstractEntity {

    @NotNull
    @NonNull
    @NotEmpty
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @NotNull
    @NonNull
    @NotEmpty
    @Column(name = "password", nullable = false)
    private String password;

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
    @OrderBy("id")
    private Set<Token> tokens;
}
