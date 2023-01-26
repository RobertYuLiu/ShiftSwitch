package com.jos.dem.springboot.h2.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table
public class SwitchRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String employee1;
    private String employee2;
    private String typeOfSwitch;
    private LocalDate switchDate1;
    private LocalDate switchDate2;
    private String confirmationId;
    private String reasonOfDeny;
    private String switchRecordFrom;

}
