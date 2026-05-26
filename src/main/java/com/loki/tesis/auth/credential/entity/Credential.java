package com.loki.tesis.auth.credential.entity;

import com.loki.tesis.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "credentials")
@NoArgsConstructor
@Getter
@Setter
public class Credential {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @OneToOne(fetch = FetchType.LAZY) // FetchType.Lazy hace que no se pudan estos datos automaticamente.
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}
