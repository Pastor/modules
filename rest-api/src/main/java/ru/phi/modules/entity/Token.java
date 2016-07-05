package ru.phi.modules.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "Token")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"scopes"})
@NoArgsConstructor
@Proxy(lazy = false)
@ToString(exclude = {"scopes"})
public final class Token extends AbstractEntity {
    @NotNull
    @NonNull
    @Column(name = "expected_at", nullable = false)
    @JsonProperty("expected_at")
    private LocalDateTime expiredAt;

    @NotNull
    @NonNull
    @Column(name = "key", nullable = false)
    @JsonProperty("key")
    private String key;

    @JsonIgnore
    @NotNull
    @NonNull
    @PrimaryKeyJoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private User user;

    @Setter(value = AccessLevel.PUBLIC)
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @JoinTable(name = "Scope", joinColumns = @JoinColumn(name = "id"),
//            inverseJoinColumns = @JoinColumn(name = "id"))
    @OrderBy("id")
    private Set<Scope> scopes;
}
