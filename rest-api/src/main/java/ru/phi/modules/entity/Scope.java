package ru.phi.modules.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(name = "Scope")
@Data
@EqualsAndHashCode(of = {"name"}, callSuper = false)
@NoArgsConstructor
@ToString(exclude = {"tokens"})
public final class Scope extends AbstractEntity {
    @NotNull
    @NonNull
    @Column(name = "name", nullable = false)
    @JsonProperty("name")
    private String name;

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("id")
    private Set<Token> tokens;
}
