package ru.phi.modules.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Proxy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "oauth_access_token")
@Data
@EqualsAndHashCode
@ToString()
@NoArgsConstructor
@Proxy(lazy = false)
public final class OAuth2AccessToken {
    @Column(name = "token_id")
    private String tokenId;

    @Column(name = "token", length = Integer.MAX_VALUE)
    private byte[] token;

    @Id
    @Column(name = "authentication_id", unique = true)
    private String authenticationId;

    @Column(name = "user_name")
    private String username;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "authentication", length = Integer.MAX_VALUE)
    private byte[] authentication;

    @Column(name = "refresh_token")
    private String refreshToken;
}