package com.jos.dem.springboot.h2.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class WeekShift {
    private String badgeId;
    private String startSundayStr;
    private boolean verified;
    private Shift[] shift;

}
