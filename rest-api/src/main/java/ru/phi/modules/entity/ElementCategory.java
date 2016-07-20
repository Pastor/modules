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
@Table(name = "Element_Category")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"elements", "user"})
@ToString(exclude = {"elements", "user"})
@NoArgsConstructor
@Proxy(lazy = false)
public final class ElementCategory extends AbstractEntity {

    @JsonProperty("name")
    @NotNull
    @NonNull
    @NotEmpty
    @Column(name = "name", nullable = false)
    private String name;

    @JsonProperty("icon")
    @Column(name = "icon", nullable = true)
    private String icon;

    @JsonIgnore
    @NotNull
    @NonNull
    @PrimaryKeyJoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH, optional = false)
    private User user;

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.DETACH, mappedBy = "categories")
    @OrderBy("id")
    private Set<Element> elements;

    /**
     * HOSPITAL, SCHOOL
     */
}
