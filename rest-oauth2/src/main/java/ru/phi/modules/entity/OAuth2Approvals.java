package ru.phi.modules.entity;

import lombok.*;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "oauth_approvals")
@Data
@EqualsAndHashCode
@ToString()
@NoArgsConstructor
@Proxy(lazy = false)
public final class OAuth2Approvals {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @Setter(AccessLevel.PUBLIC)
    @Getter
    private Long id;

    @Column(name = "userId")
    private String userId;

    @Column(name = "clientId")
    private String clientId;

    @Column(name = "scope")
    private String scope;

    @Column(name = "status")
    private String status;

    @Column(name = "expiresAt")
    private LocalDateTime expiresAt;

    @Column(name = "lastModifiedAt")
    private LocalDateTime lastModifiedAt;
}