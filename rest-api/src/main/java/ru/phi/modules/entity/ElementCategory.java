package ru.phi.modules.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Proxy;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(name = "Element_Category")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor
@Proxy(lazy = false)
public final class ElementCategory extends AbstractEntity {

    @NotNull
    @NonNull
    @NotEmpty
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "icon", nullable = true)
    private String icon;

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("id")
    private Set<Element> elements;

    /**
     * HOSPITAL, SCHOOL
     */
}
