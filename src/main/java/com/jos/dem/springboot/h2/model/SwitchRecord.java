package com.jos.dem.springboot.h2.model;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
public class SwitchRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private int employee1;
    private int employee2;
    private TypeOfSwitch typeOfSwitch;
    private LocalDate switchDate1;
    private LocalDate switchDate2;
    private int confirmationId;
    private String reasonOfDeny;

    public SwitchRecord() {
    }

    public SwitchRecord(int employee1, int employee2, TypeOfSwitch typeOfSwitch, LocalDate switchDate1, LocalDate switchDate2, int confirmationId, String reasonOfDeny) {
        this.employee1 = employee1;
        this.employee2 = employee2;
        this.typeOfSwitch = typeOfSwitch;
        this.switchDate1 = switchDate1;
        this.switchDate2 = switchDate2;
        this.confirmationId = confirmationId;
        this.reasonOfDeny = reasonOfDeny;
    }

    public int getEmployee1() {
        return employee1;
    }

    public void setEmployee1(int employee1) {
        this.employee1 = employee1;
    }

    public int getEmployee2() {
        return employee2;
    }

    public void setEmployee2(int employee2) {
        this.employee2 = employee2;
    }

    public TypeOfSwitch getTypeOfSwitch() {
        return typeOfSwitch;
    }

    public void setTypeOfSwitch(TypeOfSwitch typeOfSwitch) {
        this.typeOfSwitch = typeOfSwitch;
    }

    public LocalDate getSwitchDate1() {
        return switchDate1;
    }

    public void setSwitchDate1(LocalDate switchDate1) {
        this.switchDate1 = switchDate1;
    }

    public LocalDate getSwitchDate2() {
        return switchDate2;
    }

    public void setSwitchDate2(LocalDate switchDate2) {
        this.switchDate2 = switchDate2;
    }

    public int getConfirmationId() {
        return confirmationId;
    }

    public void setConfirmationId(int confirmationId) {
        this.confirmationId = confirmationId;
    }

    public String getReasonOfDeny() {
        return reasonOfDeny;
    }

    public void setReasonOfDeny(String reasonOfDeny) {
        this.reasonOfDeny = reasonOfDeny;
    }
}
