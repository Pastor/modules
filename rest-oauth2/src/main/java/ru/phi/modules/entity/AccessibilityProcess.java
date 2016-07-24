package ru.phi.modules.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(name = "Accessibility_Process")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"elements", "endPoints"})
@ToString(exclude = {"elements", "endPoints"})
@NoArgsConstructor
@Proxy(lazy = false)
public final class AccessibilityProcess extends AbstractEntity {

    @NotNull
    //NOTICE: Bad boy
    //@NonNull
    @Column(name = "accessibility", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Accessibility accessibility;

    @NotNull
    //@NonNull
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccessibilityType type;

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("id")
    private Set<Element> elements;

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @OrderBy("id")
    private Set<EndPoint> endPoints;
}
