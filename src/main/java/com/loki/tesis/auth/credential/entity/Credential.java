package com.loki.tesis.auth.credential.entity;

import com.loki.tesis.auth.credential.enums.RoleType;
import com.loki.tesis.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;

import static com.loki.tesis.user.enums.AccountStatus.ACTIVE;

@Entity
@Table(name = "credentials")
@NoArgsConstructor
@Getter
@Setter
public class Credential implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "credentials_id_seq")
    @SequenceGenerator(name = "credentials_id_seq", sequenceName = "credentials_id_seq", allocationSize = 1)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, name = "email_verified")
    private boolean emailVerified = false;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true, updatable = false)
    private User user;

    @Column(nullable = false, name = "login_attempts")
    private Integer loginAttempts;

    @Column(name = "locked_until")
    private Instant lockedUntil;

    @Column(name = "last_lock_notification_at")
    private Instant lastLockNotificationAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "credential_roles")
    private RoleType roleType;

    @Version
    @Column(nullable = false)
    private Long version;

    @PrePersist
    public void prePersist() {
        this.roleType = RoleType.ROLE_USER;
        this.loginAttempts = 0;
        this.version = 0L;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.<GrantedAuthority>singleton(new SimpleGrantedAuthority(roleType.name()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }
    @Override
    public boolean isAccountNonLocked() {
        return this.lockedUntil == null || this.lockedUntil.isBefore(Instant.now());
    }

    @Override
    public boolean isEnabled() {
        return this.user != null && this.getUser().getStatus() == ACTIVE;
    }
}
