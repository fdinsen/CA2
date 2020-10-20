/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author simon
 */
@Entity
@Table(name = "cityinfo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Cityinfo.findAll", query = "SELECT c FROM Cityinfo c"),
    @NamedQuery(name = "Cityinfo.findByZipcode", query = "SELECT c FROM Cityinfo c WHERE c.zipcode = :zipcode"),
    @NamedQuery(name = "Cityinfo.findByCity", query = "SELECT c FROM Cityinfo c WHERE c.city = :city")})
public class Cityinfo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 4)
    @Column(name = "zipcode")
    private String zipcode;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 35)
    @Column(name = "city")
    private String city;
    @OneToMany(cascade = CascadeType.DETACH, mappedBy = "zipcode")
    private List<Address> addressList;

    public Cityinfo() {
    }

    public Cityinfo(String zipcode) {
        this.zipcode = zipcode;
    }

    public Cityinfo(String zipcode, String city) {
        this.zipcode = zipcode;
        this.city = city;
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

    @XmlTransient
    public List<Address> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (zipcode != null ? zipcode.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Cityinfo)) {
            return false;
        }
        Cityinfo other = (Cityinfo) object;
        if ((this.zipcode == null && other.zipcode != null) || (this.zipcode != null && !this.zipcode.equals(other.zipcode))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "zipcode=" + zipcode + " city: " + city;
    }
    
}
