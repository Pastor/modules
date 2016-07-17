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
@Table(name = "Profile")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"user", "quality", "settings", "news"})
@ToString(exclude = {"quality", "settings", "news"})
@NoArgsConstructor
@Proxy(lazy = false)
public final class Profile extends AbstractEntity {

    @Email(message = "Не верно введен email адрес")
    @Column(name = "email")
    private String email;

    @NotNull
    @NonNull
    @NotEmpty
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotNull
    @NonNull
    @NotEmpty
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "middle_name", nullable = true)
    private String middleName;

    @Column(name = "address", nullable = true)
    private String address;

    @Column(name = "city", nullable = true)
    private String city;

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "profile", cascade = CascadeType.ALL)
    @OrderBy("id")
    private Set<News> news;

    @NotNull
    @NonNull
    @Column(name = "accessibility", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Accessibility accessibility = Accessibility.NORMAL;

    @JsonIgnore
    @PrimaryKeyJoinColumn(name = "quality_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Quality quality;

    @JsonIgnore
    @NotNull
    @NonNull
    @PrimaryKeyJoinColumn(name = "user_id", referencedColumnName = "id")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, optional = false)
    private User user;

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "profile", cascade = CascadeType.ALL)
    private Settings settings;
}
