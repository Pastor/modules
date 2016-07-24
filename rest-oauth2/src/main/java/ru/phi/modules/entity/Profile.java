package ru.phi.modules.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.Proxy;
import org.hibernate.validator.constraints.Email;

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

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "profile", cascade = CascadeType.ALL)
    @OrderBy("id")
    private Set<News> news;

    @JsonProperty("accessibility")
    @NotNull
    @NonNull
    @Column(name = "accessibility", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Accessibility accessibility = Accessibility.normal;

    @JsonProperty("quality")
    @PrimaryKeyJoinColumn(name = "quality_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private Quality quality;

    @JsonIgnore
    @NotNull
    @NonNull
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH, optional = false)
    private User user;

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    @PrimaryKeyJoinColumn
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "profile")
    private Settings settings;
}
