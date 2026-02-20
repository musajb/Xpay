package org.testing.apitesting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.testing.apitesting.domain.Wallet;
import org.testing.apitesting.domain.User;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUser(User user);
    Optional<Wallet> findByVaAccountNumber(String vaAccountNumber);
}