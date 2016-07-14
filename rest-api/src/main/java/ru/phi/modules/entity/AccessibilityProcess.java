package ru.phi.modules.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(name = "Accessibility_Process")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"elements"})
@ToString(exclude = {"elements"})
@NoArgsConstructor
@Proxy(lazy = false)
public final class AccessibilityProcess extends AbstractEntity {

    @NotNull
    @NonNull
    @Column(name = "accessibility", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Accessibility accessibility;

    @Min(0)
    @Max(100)
    @Column(name = "process", nullable = false)
    private int process;

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("id")
    private Set<Element> elements;
}
