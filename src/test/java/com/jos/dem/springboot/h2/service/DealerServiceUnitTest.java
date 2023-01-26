package com.jos.dem.springboot.h2.service;

import com.jos.dem.springboot.h2.TestHelper;
import com.jos.dem.springboot.h2.model.Dealer;
import com.jos.dem.springboot.h2.repository.DealerRepository;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class DealerServiceUnitTest {
    //reference
    //https://github.com/kriscfoster/spring-boot-testing-pyramid/blob/master/src/test/java/com/kriscfoster/controllertesting/controller/WelcomeControllerUnitTest.java
    private DealerService classUnderTest;
    private DealerRepository dealerRepository;

    @Test
    public void updateEditedDealers() throws Exception {
        this.dealerRepository = Mockito.mock(DealerRepository.class);
        this.classUnderTest = new DealerService(this.dealerRepository);
        List<Dealer> allDealers = TestHelper.getAllDealersWithoutAnyDuplication();
        when(this.dealerRepository.findAll()).thenReturn(allDealers);
        Dealer dealerForTest1 = TestHelper.getOneSampleDealer();
        when(this.dealerRepository.findByBadgeNumber(Mockito.anyString())).thenReturn(Arrays.asList(dealerForTest1));
        when(this.dealerRepository.save(Mockito.any(Dealer.class))).thenReturn(new Dealer());
        List<Dealer> result = this.classUnderTest.updateEditedDealers(allDealers);
        assertEquals(4, result.size());
    }


}