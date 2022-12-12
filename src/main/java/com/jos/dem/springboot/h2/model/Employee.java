package com.jos.dem.springboot.h2.model;


public class Employee {
    private int employeeId;
    private String firstname;
    private String lastName;

    public Employee() {
    }

    public Employee(int employeeId, String firstname, String lastName) {
        this.employeeId = employeeId;
        this.firstname = firstname;
        this.lastName = lastName;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
