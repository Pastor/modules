package ru.phi.modules.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "Geo_Point")
@Getter
@Setter
@ToString(exclude = {"user", "elements", "endPoints", "polyElements"})
@NoArgsConstructor
@Proxy(lazy = false)
public final class GeoPoint extends AbstractEntity {

    @Getter
    @Setter
    @NotNull
    @Column(name = "longitude", nullable = false)
    private double longitude;

    @Getter
    @Setter
    @NotNull
    @Column(name = "latitude", nullable = false)
    private double latitude;

    @JsonIgnore
    @NotNull
    @NonNull
    @PrimaryKeyJoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, optional = false)
    private User user;

    @JsonIgnore
    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.NONE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "point", cascade = CascadeType.DETACH)
    @OrderBy("id")
    private Set<Element> elements;

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "polygon")
    @OrderBy("id")
    private Set<Element> polyElements;

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "point", cascade = CascadeType.ALL)
    @OrderBy("id")
    private Set<EndPoint> endPoints;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeoPoint geoPoint = (GeoPoint) o;
        return Double.compare(geoPoint.longitude, longitude) == 0 &&
                Double.compare(geoPoint.latitude, latitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(longitude, latitude);
    }
}
