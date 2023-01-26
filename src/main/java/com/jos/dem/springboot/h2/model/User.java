package com.jos.dem.springboot.h2.model;

public class User {
 private float id;
 private String name;
 private String username;
 private String email;
 Address AddressObject;


 // Getter Methods 

 public float getId() {
  return id;
 }

 public String getName() {
  return name;
 }

 public String getUsername() {
  return username;
 }

 public String getEmail() {
  return email;
 }

 public Address getAddress() {
  return AddressObject;
 }

 // Setter Methods 

 public void setId(float id) {
  this.id = id;
 }

 public void setName(String name) {
  this.name = name;
 }

 public void setUsername(String username) {
  this.username = username;
 }

 public void setEmail(String email) {
  this.email = email;
 }

 public void setAddress(Address addressObject) {
  this.AddressObject = addressObject;
 }
}
class Address {
 private String street;
 private String suite;
 private String city;
 private String zipcode;
 Geo GeoObject;


 // Getter Methods 

 public String getStreet() {
  return street;
 }

 public String getSuite() {
  return suite;
 }

 public String getCity() {
  return city;
 }

 public String getZipcode() {
  return zipcode;
 }

 public Geo getGeo() {
  return GeoObject;
 }

 // Setter Methods 

 public void setStreet(String street) {
  this.street = street;
 }

 public void setSuite(String suite) {
  this.suite = suite;
 }

 public void setCity(String city) {
  this.city = city;
 }

 public void setZipcode(String zipcode) {
  this.zipcode = zipcode;
 }

 public void setGeo(Geo geoObject) {
  this.GeoObject = geoObject;
 }
}
class Geo {
 private String lat;
 private String lng;


 // Getter Methods 

 public String getLat() {
  return lat;
 }

 public String getLng() {
  return lng;
 }

 // Setter Methods 

 public void setLat(String lat) {
  this.lat = lat;
 }

 public void setLng(String lng) {
  this.lng = lng;
 }
}