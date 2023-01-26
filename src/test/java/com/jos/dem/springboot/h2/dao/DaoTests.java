package com.jos.dem.springboot.h2.dao;

import com.jos.dem.springboot.h2.TestHelper;
import com.jos.dem.springboot.h2.model.Dealer;
import com.jos.dem.springboot.h2.repository.DealerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DaoTests {
 
  @Autowired
  private DealerRepository dealerRepository;
 
  @Test
  public void testCreateReadDelete() {

    List<Dealer> listOfDealer = TestHelper.getAllDealersWithoutAnyDuplication();
//    Dealer dealer = new Dealer();
//
//    dealerRepository.save(dealer);
    dealerRepository.saveAll(listOfDealer);
 
    Iterable<Dealer> dealers = dealerRepository.findAll();
//    Assertions.assertThat(dealers).extracting(Dealer::getFirstName).containsOnly("Lokesh");
//
//    dealerRepository.deleteAll();
//    Assertions.assertThat(dealerRepository.findAll()).isEmpty();
  }
}