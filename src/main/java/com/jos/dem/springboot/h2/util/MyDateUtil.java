package com.jos.dem.springboot.h2.util;

import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class MyDateUtil {
    public MyDateUtil() {
    }

    public FourWeekStartEndDate generateScheduleStartEndDatesFor4Weeks() {
        FourWeekStartEndDate fourWeekStartEndDate = new FourWeekStartEndDate();
        LocalDate today = LocalDate.now();
        // Go backward to get Sunday
        LocalDate sunday = getSundayOfCurrentWeek();
        fourWeekStartEndDate.setWeekTwoSunday(sunday);
        fourWeekStartEndDate.setWeekOneSunday(sunday.minusWeeks(1));
        fourWeekStartEndDate.setWeekThreeSunday(sunday.plusWeeks(1));
        fourWeekStartEndDate.setWeekFourSunday(sunday.plusWeeks(2));
        // Go forward to get Saturday
        LocalDate saturday = getSaturdayOfCurrentWeek();
        fourWeekStartEndDate.setWeekTwoSaturday(saturday);
        fourWeekStartEndDate.setWeekOneSaturday(saturday.minusWeeks(1));
        fourWeekStartEndDate.setWeekThreeSaturday(saturday.plusWeeks(1));
        fourWeekStartEndDate.setWeekFourSaturday(saturday.plusWeeks(2));
        return fourWeekStartEndDate;
    }

    private LocalDate getSaturdayOfCurrentWeek() {
        LocalDate saturday = LocalDate.now();
        while (saturday.getDayOfWeek() != DayOfWeek.SATURDAY) {
            saturday = saturday.plusDays(1);
        }
        return saturday;
    }

    public LocalDate getSundayOfCurrentWeek() {
        LocalDate sunday = LocalDate.now();
        while (sunday.getDayOfWeek() != DayOfWeek.SUNDAY) {
            sunday = sunday.minusDays(1);
        }
        return sunday;
    }

    public String convertLocalDateToString(LocalDate date) {
        //sample code
//        DateTimeFormatter DATEFORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        LocalDate ld = LocalDate.parse("2017-03-13", DATEFORMATTER);
//        LocalDateTime ldt = LocalDateTime.of(ld, LocalDateTime.now().toLocalTime());

        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String text = date.format(formatters);
        return text;
    }

    public LocalDate convertStringToDate(String dateString) {
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyy-MM-d");
        LocalDate localDate = LocalDate.parse(dateString, formatters);
        return localDate;
    }

    public LocalDate getDayPlus(String baseDay, int dayPlus) {
        LocalDate date = LocalDate.parse(baseDay);
        System.out.println("LocalDate before" + " adding days: " + date);
        LocalDate nextSunday = date.plusDays(dayPlus);
        return nextSunday;
    }

}
