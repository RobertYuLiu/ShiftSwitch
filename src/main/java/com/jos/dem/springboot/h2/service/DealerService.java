package com.jos.dem.springboot.h2.service;

import com.jos.dem.springboot.h2.model.Dealer;
import com.jos.dem.springboot.h2.repository.DealerRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DealerService {

    private DealerRepository dealerRepository;

    public DealerService(DealerRepository dealerRepository) {
        this.dealerRepository = dealerRepository;
    }

    public List<Dealer> updateEditedDealers(List<Dealer> dealerList) throws Exception {
        List<Dealer> updatedList = new ArrayList<>();
        for (Dealer dealer : dealerList) {
            //step 1: find dealer by badge number
            List<Dealer> tempList = this.dealerRepository.findByBadgeNumber(dealer.getBadgeNumber());
            if (tempList == null || tempList.size() == 0) {
                throw new Exception("no dealer found!");
            }
            //step 2: see if there are multiple dealers sharing same badge number
            if (tempList.size() > 1) {
                throw new Exception("multi dealer records found:" + tempList.get(0).getBadgeNumber());
            }
            Dealer tempDealer = tempList.get(0);
            tempDealer.setStartTimeCategory(dealer.getStartTimeCategory());
            tempDealer.setOffDay(dealer.getOffDay());
            this.dealerRepository.save(tempDealer);
            updatedList.add(tempDealer);
        }
        ;
        return updatedList;
    }
}
