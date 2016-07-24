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
@Table(name = "oauth_client_token")
@Data
@EqualsAndHashCode
@ToString()
@NoArgsConstructor
@Proxy(lazy = false)
public final class OAuth2ClientToken {
    @Column(name = "token_id")
    private String tokenId;

    @Column(name = "token", length = Integer.MAX_VALUE)
    private byte[] token;

    @Id
    @Column(name = "authentication_id")
    private String authenticationId;

    @Column(name = "user_name")
    private String username;

    @Column(name = "client_id")
    private String clientId;
}