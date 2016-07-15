package ru.phi.modules.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Column(name = "filter")
    private String filter;

    @PrimaryKeyJoinColumn(name = "quality_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Quality quality;

    @Column(name = "route_type")
    @Enumerated(EnumType.STRING)
    private RouteType routeType = RouteType.BEST;

    @Column(name = "start_longitude")
    private Double startLongitude;

    @Column(name = "start_latitude")
    private Double startLatitude;

    @Column(name = "end_longitude")
    private Double endLongitude;

    @Column(name = "end_latitude")
    private Double endLatitude;

    @JsonIgnore
    @NotNull
    @NonNull
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    private Profile profile;
}
