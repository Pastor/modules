package ru.phi.modules.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "Settings")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"token"})
@ToString(exclude = {"token"})
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
    private RouteType routeType;

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
    @PrimaryKeyJoinColumn(name = "token_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Token token;
}
