package com.jos.dem.springboot.h2.util;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Data
@Getter
@Setter
public class FourWeekStartEndDate {
    private LocalDate weekOneSunday;
    private LocalDate weekOneSaturday;
    private LocalDate weekTwoSunday;
    private LocalDate weekTwoSaturday;
    private LocalDate weekThreeSunday;
    private LocalDate weekThreeSaturday;
    private LocalDate weekFourSunday;
    private LocalDate weekFourSaturday;
}