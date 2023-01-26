package com.jos.dem.springboot.h2.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dealer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String badgeNumber;
    private String lastName;
    private String firstName;
    private String seniority;
    private String status;
    private String startTimeCategory;
    private boolean pkRoomDealer;
    private String offDay;

    public Dealer(String badgeNumber, String firstName, String lastName) {
        this.badgeNumber = badgeNumber;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
