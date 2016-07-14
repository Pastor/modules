package ru.phi.modules.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Proxy;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(name = "Element")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"categories", "creator", "accessibilityProcesses"})
@ToString(exclude = {"categories", "creator", "accessibilityProcesses"})
@NoArgsConstructor
@Proxy(lazy = false)
public final class Element extends AbstractEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "full_name")
    private String fullName;

    @NotNull
    @NonNull
    @NotEmpty
    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "info")
    private String info;

    @NotNull
    @NonNull
    @NotEmpty
    @Column(name = "longitude", nullable = false)
    private double longitude;

    @NotNull
    @NonNull
    @NotEmpty
    @Column(name = "latitude", nullable = false)
    private double latitude;

    @JsonIgnore
    @NotNull
    @NonNull
    @PrimaryKeyJoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private User creator;

    @Setter(value = AccessLevel.PUBLIC)
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("id")
    private Set<ElementCategory> categories;

    @Setter(value = AccessLevel.PUBLIC)
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("id")
    private Set<AccessibilityProcess> accessibilityProcesses;
}
