/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

import entities.Cityinfo;

/**
 *
 * @author simon
 */
public class CityinfoDTO {
    
    private String zipcode; 
    private String city;

    public CityinfoDTO(Cityinfo city) {
        this.zipcode = city.getCity();
        this.city = city.getZipcode();
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
