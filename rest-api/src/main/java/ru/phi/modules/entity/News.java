package ru.phi.modules.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.hibernate.annotations.Proxy;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "News")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"user"})
@ToString(exclude = {"user"})
@NoArgsConstructor
@Proxy(lazy = false)
public final class News extends AbstractEntity {

    @NotNull
    @NonNull
    @NotEmpty
    @Column(name = "title", nullable = false)
    private String title;

    @NotNull
    @NonNull
    @NotEmpty
    @Column(name = "bref", nullable = false)
    private String bref;

    //FIXME: CLOB
    @Column(name = "content", nullable = true)
    private String content;

    @NotNull
    @NonNull
    @Column(name = "visible", nullable = false)
    private boolean visible = false;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonProperty("published_at")
    @JsonSerialize(using = JsonLocalDateTimeSerializer.class)
    @LastModifiedDate
    @Column(name = "published_at", nullable = true)
    @Setter(AccessLevel.PUBLIC)
    @Getter
    private LocalDateTime publishedAt;

    @JsonIgnore
    @NotNull
    @NonNull
    @PrimaryKeyJoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private User user;
}
