package com.jos.dem.springboot.h2.model;

import lombok.Data;

@Data
public class SwitchForm {

    private boolean isDealer;

    private boolean isDayDaySwitch;
    private boolean isGiveAwayPickUp;
    private boolean isTimeSwitch;
    private boolean isPitSwitch;

    private String firstLastNameA1;
    private int badgeIdA1;
    private boolean isFullTimeA1;
    private boolean isFullTimeUtilA1;
    private boolean isPartTimeA1;

    private String firstLastNameA2;
    private int badgeIdA2;
    private boolean isFullTimeA2;
    private boolean isFullTimeUtilA2;
    private boolean isPartTimeA2;

    private String dayOfWeekA;
    private String monthDateA;
    private String shiftStartEndTimeA;
    private boolean isPokerRoomA;
    private boolean is6AmCrabsA;

    private String firstLastNameB1;
    private int badgeIdB1;
    private boolean isFullTimeB1;
    private boolean isFullTimeUtilB1;
    private boolean isPartTimeB1;

    private String firstLastNameB2;
    private int badgeIdB2;
    private boolean isFullTimeB2;
    private boolean isFullTimeUtilB2;
    private boolean isPartTimeB2;

    private String dayOfWeekB;
    private String monthDateB;
    private String shiftStartEndTimeB;
    private boolean isPokerRoomB;
    private boolean is6AmCrabsB;

    private String phoneNumA1;
    private String phoneNumA2;
    private String phoneNumA3;

    private String phoneNumB1;
    private String phoneNumB2;
    private String phoneNumB3;
}
