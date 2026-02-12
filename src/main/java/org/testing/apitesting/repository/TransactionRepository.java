package org.testing.apitesting.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.testing.apitesting.domain.Transaction;
import org.testing.apitesting.domain.User;
import org.testing.apitesting.domain.type.TransactionType;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

        Page<Transaction> findByUserIdOrderByCreatedAtDesc(Long userId,Pageable pageable);

        Page<Transaction> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, TransactionType type, Pageable pageable);

        Optional<Transaction> findByIdAndUserId(Long id, Long userId);

        boolean existsByIdAndUserId(Long id, Long userId);
    }
