package com.sdms.beneficiary.repository;

import com.sdms.beneficiary.entity.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {
    List<Beneficiary> findByRegion(String region);
    List<Beneficiary> findByIsActiveTrue();
    Optional<Beneficiary> findByAadhaarNumber(String aadharNumber);



}
