package ru.phi.modules.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Proxy;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import ru.phi.modules.oauth2.UserGetter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(name = "User")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {
        "profile", "elements", "elementCategories",
        "qualities", "statistics", "errors", "geoPoints", "endPoints"
})
@ToString(exclude = {
        "profile", "elements", "elementCategories",
        "qualities", "statistics", "errors", "geoPoints", "endPoints"
})
@NoArgsConstructor
@Proxy(lazy = false)
public final class User extends AbstractEntity implements UserGetter {

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
    @Column(name = "password", nullable = false, length = 1024)
    private String password;

    @Column(name = "phone")
    private String phone;

    @NotNull
    @NonNull
    @Column(name = "role", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRole role = UserRole.user;

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
    @OrderBy("id")
    private Set<Element> elements;

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
    @OrderBy("id")
    private Set<Statistic> statistics;

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
    @OrderBy("id")
    private Set<ElementCategory> elementCategories;

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
    @OrderBy("id")
    private Set<Quality> qualities;

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
    @OrderBy("id")
    private Set<Error> errors;

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
    @OrderBy("id")
    private Set<GeoPoint> geoPoints;

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
    @OrderBy("id")
    private Set<EndPoint> endPoints;

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user")
    private Profile profile;

    @Override
    public User user() {
        return this;
    }
}
