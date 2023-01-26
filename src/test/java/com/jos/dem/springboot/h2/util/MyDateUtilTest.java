package com.jos.dem.springboot.h2.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class MyDateUtilTest {
    //reference
//https://github.com/kriscfoster/spring-boot-testing-pyramid/blob/master/src/test/java/com/kriscfoster/controllertesting/controller/WelcomeControllerUnitTest.java
    private MyDateUtil classUnderTest;

//    WelcomeController welcomeController;

    @BeforeEach
    void setup() {
//        WelcomeService welcomeService = Mockito.mock(WelcomeService.class);
//        when(welcomeService.getWelcomeMessage("Stranger")).thenReturn("Welcome Stranger!");
//        when(welcomeService.getWelcomeMessage("John")).thenReturn("Welcome John!");
        this.classUnderTest = new MyDateUtil();
    }


    @Test
    public void generateScheduleStartEndDatesFor4Weeks() {
    }

    @Test
    public void getSundayOfCurrentWeek() {
    }

    @Test
    public void convertLocalDateToString_singleDigit() {
        LocalDate dateForTest1 = LocalDate.of(2023, 1, 22);
        LocalDate dateForTest2 = LocalDate.of(2023, 11, 2);
        String result1 = classUnderTest.convertLocalDateToString(dateForTest1);
        String result2 = classUnderTest.convertLocalDateToString(dateForTest2);
        Assert.assertEquals("2023-01-22", result1);
        Assert.assertEquals("2023-11-02", result2);
    }

    @Test
    public void convertLocalDateToString_others() {
        LocalDate dateForTest = LocalDate.of(2023, 12, 22);
        String result = classUnderTest.convertLocalDateToString(dateForTest);
        Assert.assertEquals("2023-1-22", result);
    }

    @Test
    public void convertStringToDate() {
    }

    @Test
    public void getDayPlus() {
    }
}