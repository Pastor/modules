package ru.phi.modules.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Proxy;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(name = "User")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"tokens", "profile", "news"})
@ToString(exclude = {"tokens", "profile", "news"})
@NoArgsConstructor
@Proxy(lazy = false)
public final class User extends AbstractEntity {

    @Email(message = "Не верно введен email адрес")
    @Column(name = "email")
    private String email;

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

    @Column(name = "phone", nullable = true)
    private String phone;

    @NotNull
    @NonNull
    @Column(name = "role", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRole role = UserRole.USER;

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
    @OrderBy("id")
    private Set<Token> tokens;

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
    @OrderBy("id")
    private Set<News> news;

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
    private Profile profile;
}
