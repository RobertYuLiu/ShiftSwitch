package com.jos.dem.springboot.h2.repository;

import com.jos.dem.springboot.h2.model.Dealer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DealerRepository extends JpaRepository<Dealer,Integer> {
    List<Dealer> findByBadgeNumber(String badgeNumber);
    List<Dealer> findByStatus(String status);
}
