package ru.phi.modules.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Column(name = "longitude")
    private double longitude;

    @Column(name = "latitude")
    private double latitude;

    @JsonIgnore
    @NotNull
    @NonNull
    @PrimaryKeyJoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private User user;
}
