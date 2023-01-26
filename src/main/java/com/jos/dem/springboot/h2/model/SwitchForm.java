package com.jos.dem.springboot.h2.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class SwitchForm {

    private boolean dealer;

    private boolean dayDaySwitch;
    private boolean giveAwayPickUp;
    private boolean timeSwitch;
    private boolean pitSwitch;

    private String firstLastNameA1;
    private int badgeIdA1;
    private boolean fullTimeA1;
    private boolean fullTimeUtilA1;
    private boolean partTimeA1;

    private String firstLastNameA2;
    private int badgeIdA2;
    private boolean fullTimeA2;
    private boolean fullTimeUtilA2;
    private boolean partTimeA2;

    private String dayOfWeekA;
    private String monthDateA;
    private String shiftStartEndTimeA;
    private boolean pokerRoomA;
    private boolean sixAmCrabsA;

    private String firstLastNameB1;
    private int badgeIdB1;
    private boolean fullTimeB1;
    private boolean fullTimeUtilB1;
    private boolean partTimeB1;

    private String firstLastNameB2;
    private int badgeIdB2;
    private boolean fullTimeB2;
    private boolean fullTimeUtilB2;
    private boolean partTimeB2;

    private String dayOfWeekB;
    private String monthDateB;
    private String shiftStartEndTimeB;
    private boolean pokerRoomB;
    private boolean sixAmCrabsB;

    private String phoneNumA1;
    private String phoneNumA2;
    private String phoneNumA3;

    private String phoneNumB1;
    private String phoneNumB2;
    private String phoneNumB3;
}
