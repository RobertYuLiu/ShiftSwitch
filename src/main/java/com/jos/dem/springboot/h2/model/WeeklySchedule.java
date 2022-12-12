package com.jos.dem.springboot.h2.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
public class WeeklySchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String scheduleId;

    public WeeklySchedule(int badgeId, String weekStartDate, String sundayShift, String mondayShift, String tuesdayShift, String wednesdayShift, String thursdayShift, String fridayShift, String saturdayShift) {
        this.badgeId = badgeId;
        this.weekStartDate = weekStartDate;
        this.sundayShift = sundayShift;
        this.mondayShift = mondayShift;
        this.tuesdayShift = tuesdayShift;
        this.wednesdayShift = wednesdayShift;
        this.thursdayShift = thursdayShift;
        this.fridayShift = fridayShift;
        this.saturdayShift = saturdayShift;
    }

    private int badgeId;
    private String weekStartDate;
    private String sundayShift;
    private String mondayShift;
    private String tuesdayShift;
    private String wednesdayShift;
    private String thursdayShift;
    private String fridayShift;
    private String saturdayShift;

}
