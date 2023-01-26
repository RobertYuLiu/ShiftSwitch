package com.jos.dem.springboot.h2.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Data
@Getter
@Setter
@NoArgsConstructor
public class FourWeekShift {
    private String badgeId;
    private boolean verified;
    private Shift[] weekOne;
    private Shift[] weekTwo;
    private Shift[] weekThree;
    private Shift[] weekFour;
}
