package com.jos.dem.springboot.h2.service;

import com.jos.dem.springboot.h2.model.Dealer;
import com.jos.dem.springboot.h2.repository.DealerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class UtilityService {

    private DealerRepository dealerRepository;

    public UtilityService(DealerRepository dealerRepository){
        this.dealerRepository = dealerRepository;
    }

    public String generateLineSeparatedbyComma(String stOff) {
        StringBuilder sb = new StringBuilder();
        String[] middleResult = stOff.split(",");
        for (int i = 0; i < middleResult.length; i++) {
            String temp = middleResult[i];
            if (temp.contains("-")) {
                String sss = generateString(temp);
                sb.append(sss);
            } else {
                sb.append(temp + ",");
            }
        }
        return sb.toString();
    }

    private String generateString(String temp) {
        StringBuilder sb = new StringBuilder();
        String[] numbers = temp.split("-");
        int firstNum = Integer.parseInt(numbers[0].trim());
        int secondNum = Integer.parseInt(numbers[1].trim());
        for (int i = firstNum; i <= secondNum; i++) {
            sb.append(i).append(",");
        }
        return sb.toString();
    }

    public LocalDate convertDate(String remainItem) {
        if (remainItem == null || remainItem.trim().equals("")) {
            return null;
        } else {
            LocalDate localDate = null;
            try {
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d").withLocale(Locale.ENGLISH);
                //below is used by switch confirmation file process
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MMM-yy").withLocale(Locale.ENGLISH);
                localDate = LocalDate.parse(remainItem, formatter);
            } catch (Exception e) {
                System.out.println(e);
            }
            return localDate;
        }
    }

    public List<Dealer> getAllDealers(String status) {
        List<Dealer> result = new ArrayList<>();
        switch (status) {
            case "FT":
            case "FT/UT":
            case "PT":
                result = dealerRepository.findByStatus(status);
                break;
            default:
                result = dealerRepository.findAll();
        }
        return result;
    }

    public List<Dealer> saveAllDealers(List<Dealer> list) {
        List<Dealer> newDealers = new ArrayList<>();
        for (Dealer dealer : list) {
            List<Dealer> tempDealer = dealerRepository.findByBadgeNumber(dealer.getBadgeNumber());
            if (tempDealer.size() == 0) {
                newDealers.add(dealer);
            }else{
                Dealer oldDealer = tempDealer.get(0);
                oldDealer.setBadgeNumber(dealer.getBadgeNumber());
                oldDealer.setFirstName(dealer.getFirstName());
                oldDealer.setOffDay(dealer.getOffDay());

                dealerRepository.save(oldDealer);
            }
        }
        return dealerRepository.saveAll(newDealers);
//        return dealerRepository.saveAll(list);
    }

    public LocalDate convertDateYYYYMMDD(String startDateStr) {
        DateTimeFormatter DateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-d");//sample:"2022-12-6" or "2022-1-12"
        return LocalDate.parse(startDateStr, DateFormatter);
    }

    public String getShiftStartEndTimeByCode(String shiftCode) {
        if (shiftCode == null) return "0AM - 0PM";
        String description = "";
        switch (shiftCode) {
            case "4AM":
                description = "4AM - 12PM";
                break;
            case "10AM":
                description = "10AM - 6PM";
                break;
            case "2PM/4PM":
                description = "2PM - 10PM";
                break;
            case "6PM":
                description = "6PM - 2AM";
                break;
            case "8PM":
                description = "8PM - 4AM";
                break;
            case "12PM":
                description = "12PM - 8PM";
                break;
            default:
                description = "0AM - 0PM";
        }
        return description;
    }
}
