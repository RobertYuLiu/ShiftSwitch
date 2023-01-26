package com.jos.dem.springboot.h2;

import com.jos.dem.springboot.h2.model.Dealer;

import java.util.ArrayList;
import java.util.List;

public class TestHelper {

    public static Dealer getOneSampleDealer(){
        Dealer dealer = new Dealer();
        dealer.setOffDay("Thu Fri");
        dealer.setFirstName("Joe");
        dealer.setLastName("pee");
        dealer.setBadgeNumber("12345");
        return dealer;
    }

    public static List<Dealer> getAllDealersWithoutAnyDuplication() {
        List<Dealer> list = new ArrayList<>();
        list.add(new Dealer("11111", "aa", "a1a1"));
        list.add(new Dealer("22222", "bb", "b2b2"));
        list.add(new Dealer("33333", "cc", "c3c3"));
        list.add(new Dealer("44444", "dd", "d4d4"));
        return list;
    }
}
