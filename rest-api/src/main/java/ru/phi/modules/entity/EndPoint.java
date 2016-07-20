package ru.phi.modules.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(name = "End_Point")
@Data
@EqualsAndHashCode(callSuper = true, of = {"point", "type"})
@ToString(exclude = {"user", "elements"})
@NoArgsConstructor
@Proxy(lazy = false)
public final class EndPoint extends AbstractEntity {

    @JsonProperty(value = "point", required = true)
    @NotNull
    @NonNull
    @PrimaryKeyJoinColumn(name = "point_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private GeoPoint point;

    @JsonProperty(value = "type", required = true)
    @NotNull
    @NonNull
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private EndPointType type = EndPointType.BOTH;

    @JsonIgnore
    @NotNull
    @NonNull
    @PrimaryKeyJoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private User user;

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("id")
    private Set<Element> elements;
}
