/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

import entities.Hobby;

/**
 *
 * @author gamma
 */
public class HobbyDTO {
    private String name;
    private String wikilink;
    private String category;
    private String type;
    
    public HobbyDTO(Hobby hobby) {
        this.name = hobby.getName();
        this.wikilink = hobby.getWikilink();
        this.category = hobby.getCategory();
        this.type = hobby.getType();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWikilink() {
        return wikilink;
    }

    public void setWikilink(String wikilink) {
        this.wikilink = wikilink;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    
}
