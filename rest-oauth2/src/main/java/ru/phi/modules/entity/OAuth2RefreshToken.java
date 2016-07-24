package ru.phi.modules.entity;

import lombok.*;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;

@Entity
@Table(name = "oauth_refresh_token")
@Data
@EqualsAndHashCode
@ToString()
@NoArgsConstructor
@Proxy(lazy = false)
public final class OAuth2RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @Setter(AccessLevel.PUBLIC)
    @Getter
    private Long id;

    @Column(name = "token_id")
    private String tokenId;

    @Column(name = "token", length = Integer.MAX_VALUE)
    private byte[] token;

    @Column(name = "authentication", length = Integer.MAX_VALUE)
    private byte[] authentication;
}