package com.jos.dem.springboot.h2.controller;

import com.jos.dem.springboot.h2.model.Dealer;
import com.jos.dem.springboot.h2.service.DealerService;
import com.jos.dem.springboot.h2.service.UtilityService;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class DealerController {

    private UtilityService utilityService;

    private DealerService dealerService;

    public DealerController(UtilityService utilityService, DealerService dealerService) {
        this.utilityService = utilityService;
        this.dealerService = dealerService;
    }

    @GetMapping("/dealers")
    public ResponseEntity getDealers(@Param("status") String status) {
        System.out.println("status...." + status);
//        int error = 1/0; // here already added the 'RestExceptionHandler.java' to GLOBALLY handle the exception
        List<Dealer> result = utilityService.getAllDealers(status);
        return new ResponseEntity("Successfully updated " + result.size() + " records.", HttpStatus.OK);
    }

    @PostMapping("/dealersEdited")
    public List<Dealer> updateEditedDealers(@RequestBody List<Dealer> dealerList) throws Exception {

        //ideally after the update, empty list will be sent back
        List<Dealer> newList = this.dealerService.updateEditedDealers(dealerList);
        //in case there is duplicated dealer, send the list of those dealers back for further process.
        newList.stream().forEach(temp->{
            System.out.println("....." + temp.getStartTimeCategory());
        });
        return newList;
    }
}
