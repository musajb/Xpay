package org.testing.apitesting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.testing.apitesting.domain.Transaction;
import org.testing.apitesting.domain.User;
import org.testing.apitesting.domain.Wallet;
import org.testing.apitesting.domain.dto.WalletResponse;
import org.testing.apitesting.domain.type.TransactionStatus;
import org.testing.apitesting.domain.type.TransactionType;
import org.testing.apitesting.exception.UserNotFoundException;
import org.testing.apitesting.repository.TransactionRepository;
import org.testing.apitesting.repository.UserRepository;
import org.testing.apitesting.repository.WalletRepository;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final MonnifyService monnifyService;


    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Transactional
    public Wallet getOrCreateCurrentUserWallet() {
        User user = getAuthenticatedUser();
        return walletRepository.findByUser(user)
                .orElseGet(() -> walletRepository.save(
                        Wallet.builder().user(user).balance(0.0).build()));
    }

    @Transactional
    public WalletResponse generateVirtualAccount() {
        Wallet wallet = getOrCreateCurrentUserWallet();
        if ( wallet.getVaAccountNumber() != null && !wallet.getVaAccountNumber().isEmpty() ) {
            return toResponse(wallet);
        }
        String reference = "VA-" + UUID.randomUUID();
        MonnifyService.ReservedAccountResult result = monnifyService.createReservedAccount(
                reference,
                wallet.getUser().getFullName(),
                wallet.getUser().getEmail()
        );
        wallet.setVaAccountNumber(result.getAccountNumber());
        wallet.setVaBankName(result.getBankName());
        wallet.setVaReference(result.getAccountReference());
        walletRepository.save(wallet);
        return toResponse(wallet);
    }

    @Transactional
    public void debitForAirtime(User user, Double amount, String recipient, String reference, boolean success) {
        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Wallet not found for user"));

        if ( wallet.getBalance().compareTo(amount) < 0 ) {
            throw new IllegalStateException("Insufficient wallet balance");
        }
        // wallet.setBalance(wallet.getBalance() - amount);

        if ( success ) {
            wallet.setBalance(wallet.getBalance() - amount);
            walletRepository.save(wallet);
        }
        Transaction trx = Transaction.builder()
                .user(user)
                .amount(amount)
                .type(TransactionType.AIRTIME)
                .status(success ? TransactionStatus.SUCCESS : TransactionStatus.FAILED)
                .reference(reference)
                .recipient(recipient)
                .description((success ? "Airtime purchase to " : "Failed Airtime purchase to ") + recipient)
                .createdAt(LocalDateTime.now())
                .build();
        transactionRepository.save(trx);
    }

    private WalletResponse toResponse(Wallet wallet) {
        return WalletResponse.builder()
                .balance(wallet.getBalance())
                .vaAccountNumber(wallet.getVaAccountNumber())
                .vaBankName(wallet.getVaBankName())
                .vaReference(wallet.getVaReference())
                .build();
    }


}
