package com.jos.dem.springboot.h2.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DealerAndSwitchRecordCount {
    private String badgeId;
    private int switchRecordCount;
}
