/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

import entities.Person;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gamma
 */
public class PersonDTO {

    private int phone;
    private String email;
    private String firstName;
    private String lastName;
    private List<HobbyDTO> hobbies;
    private String street;
    private String zipcode;
    private String city;

    public PersonDTO(Person person) {
        this.phone = person.getPhone();
        this.email = person.getEmail();
        this.firstName = person.getFirstName();
        this.lastName = person.getLastName();
        if (person.getAddress() != null) {
            this.street = person.getAddress().getStreet();
            
            if (person.getAddress().getZipcode() != null) {
                this.zipcode = person.getAddress().getZipcode().getZipcode();
                this.city = person.getAddress().getZipcode().getCity();
            }

        }

        hobbies = new ArrayList();
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<HobbyDTO> getHobbies() {
        return hobbies;
    }

    public void addHobby(HobbyDTO hobby) {
        hobbies.add(hobby);
    }

    public void setHobbies(List<HobbyDTO> hobbies) {
        this.hobbies = hobbies;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

}
