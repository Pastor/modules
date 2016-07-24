package ru.phi.modules.entity;

import lombok.*;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;

@Entity
@Table(name = "oauth_code")
@Data
@EqualsAndHashCode
@ToString()
@NoArgsConstructor
@Proxy(lazy = false)
public final class OAuth2Code {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @Setter(AccessLevel.PUBLIC)
    @Getter
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "authentication", length = Integer.MAX_VALUE)
    private byte[] authentication;
}