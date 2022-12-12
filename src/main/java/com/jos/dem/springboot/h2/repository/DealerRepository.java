package com.jos.dem.springboot.h2.repository;

import com.jos.dem.springboot.h2.model.Dealer;
import com.jos.dem.springboot.h2.model.SwitchRecord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DealerRepository extends CrudRepository<Dealer,Integer> {
}
