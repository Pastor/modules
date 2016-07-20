package ru.phi.modules.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "Statistic")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"user"})
@ToString(exclude = {"user"})
@NoArgsConstructor
@Proxy(lazy = false)
public final class Statistic extends AbstractEntity {

    @JsonProperty("point")
    @NotNull
    @NonNull
    @PrimaryKeyJoinColumn(name = "point_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH, optional = false)
    private GeoPoint point;

    @JsonIgnore
    @NotNull
    @NonNull
    @PrimaryKeyJoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private User user;
}
