package com.sdms.scheme.repository;

import com.sdms.scheme.entity.Scheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface SchemeRepository extends JpaRepository<Scheme, Long> {
    List<Scheme> findByRegion(String region);
    List<Scheme> findByIsActiveTrue();
}
