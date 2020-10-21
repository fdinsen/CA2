/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

import entities.Cityinfo;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author simon
 */
public class ZipcodesDTO {
    private List<CityinfoDTO> all = new ArrayList();;
    
    public ZipcodesDTO(List<Cityinfo> cityList) {

        cityList.forEach(city -> {
            all.add(new CityinfoDTO(city));
        });
    }

    public List<CityinfoDTO> getAll() {
        return all;
    }
    public int size() {
        return all.size();
    }
    
    public CityinfoDTO get(int index) {
        return all.get(index);
    }
}
