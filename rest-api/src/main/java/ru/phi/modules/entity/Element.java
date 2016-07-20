package ru.phi.modules.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.Proxy;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(name = "Element")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {
        "categories", "user", "accessibilityProcesses", "endPoints", "polygon", "point"
})
@ToString(exclude = {
        "categories", "user", "accessibilityProcesses", "endPoints", "polygon", "point"
})
@NoArgsConstructor
@Proxy(lazy = false)
public final class Element extends AbstractEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "full_name")
    private String fullName;

    @NotNull(message = "Адрес не может быть пустым")
    @NonNull
    @NotEmpty
    @Column(name = "address", nullable = false)
    private String address;

    @JsonProperty("info")
    @Column(name = "info")
    private String info;

    @JsonProperty("point")
    @NotNull
    @NonNull
    @PrimaryKeyJoinColumn(name = "point_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH, optional = false)
    private GeoPoint point;


    @JsonProperty("polygon")
    @Setter(value = AccessLevel.PUBLIC)
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @OrderBy("id")
    private Set<GeoPoint> polygon;

    @JsonIgnore
    @NotNull
    @NonNull
    @PrimaryKeyJoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH, optional = false)
    private User user;

    @JsonProperty("end_points")
    @Setter(value = AccessLevel.PUBLIC)
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @OrderBy("id")
    private Set<EndPoint> endPoints;

    @JsonProperty("categories")
    @Setter(value = AccessLevel.PUBLIC)
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @OrderBy("id")
    private Set<ElementCategory> categories;

    @JsonProperty("accessibility_process")
    @Setter(value = AccessLevel.PUBLIC)
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @OrderBy("id")
    private Set<AccessibilityProcess> accessibilityProcesses;
}
