package org.testing.apitesting.domain;

import jakarta.persistence.*;
import lombok.*;
import org.testing.apitesting.domain.type.VerificationStatus;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true, unique = true)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus;
}
