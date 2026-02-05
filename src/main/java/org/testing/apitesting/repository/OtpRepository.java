package org.testing.apitesting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.testing.apitesting.domain.Otp;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {

    Optional<Otp> findByPhoneNumber(String phoneNumber);

    Optional<Otp> findByPhoneNumberAndCodeAndUsedFalse(String phoneNumber, String code);

    void deleteByPhoneNumber(String phoneNumber);

}
