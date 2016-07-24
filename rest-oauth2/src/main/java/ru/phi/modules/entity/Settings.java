package ru.phi.modules.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "Settings")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"profile"})
@ToString(exclude = {"profile"})
@NoArgsConstructor
@Proxy(lazy = false)
public final class Settings extends AbstractEntity {

    @JsonProperty("filter")
    @Column(name = "filter")
    private String filter;

    @JsonProperty("quality")
    @PrimaryKeyJoinColumn(name = "quality_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private Quality quality;

    @JsonProperty("route_type")
    @Column(name = "route_type")
    @Enumerated(EnumType.STRING)
    private RouteType routeType = RouteType.best;

    @JsonProperty("start")
    @PrimaryKeyJoinColumn(name = "stop_point_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private GeoPoint start;

    @JsonProperty("stop")
    @PrimaryKeyJoinColumn(name = "stop_point_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private GeoPoint stop;

    @JsonIgnore
    @NotNull
    @NonNull
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH, optional = false)
    private Profile profile;
}
