/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import dto.CityinfoDTO;
import entities.Address;
import entities.Cityinfo;
import entities.Person;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

/**
 *
 * @author simon
 */
public class ZipcodeFacadeTest {

    private static EntityManagerFactory emf;
    private static ZipcodeFacade facade;

    Person p1;
    Person p2;
    Person p3;
    Person p4;

    Address a1;

    public static Cityinfo test;

    Cityinfo c;
    Cityinfo c1;
    Cityinfo c2;
    Cityinfo c3;

    public ZipcodeFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = ZipcodeFacade.getZipcodeFacade(emf);

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            test = em.find(Cityinfo.class, "3400");

            if (test == null) {

                Cityinfo c = new Cityinfo("3360", "Liseleje");
                Cityinfo c1 = new Cityinfo("3370", "Melby");
                Cityinfo c2 = new Cityinfo("3390", "Hundested");
                Cityinfo c3 = new Cityinfo("3400", "Hillerød");

                em.persist(c);
                em.persist(c1);
                em.persist(c2);
                em.persist(c3);

                em.getTransaction().commit();
            } else {
                em.getTransaction().commit();
            }

        } finally {

            em.close();
        }
    }

    @AfterAll
    public static void tearDownClass() {
//        Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
    }

    // Setup the DataBase in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the code below to use YOUR OWN entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();

        p1 = new Person(12345678, "mail", "navn", "andetNavn");
        p2 = new Person(23456789, "mail1", "navn1", "andetNavn1");
        p3 = new Person(34567890, "mail2", "navn2", "andetNavn2");
        p4 = new Person(45678901, "mail3", "navn3", "andetNavn3");

        Address a1 = new Address(1, "vej vej");
        a1.setAdditionalInfo("");
        a1.setZipcode(new Cityinfo("3400", "Hillerod"));
        p1.setAddress(a1);

        try {
            em.getTransaction().begin();
            em.createNamedQuery("Person.deleteFrom").executeUpdate();

            p1 = new Person(12345678, "mail", "navn", "andetNavn");
            p2 = new Person(23456789, "mail1", "navn1", "andetNavn1");
            p3 = new Person(34567890, "mail2", "navn2", "andetNavn2");
            p4 = new Person(45678901, "mail3", "navn3", "andetNavn3");

            Cityinfo c4 = em.find(Cityinfo.class, "3400");

            a1 = new Address(1, "vej vej");
            a1.setAdditionalInfo("Ingen ting her");
            a1.setZipcode(c4);

            p1.setAddress(a1);

            em.persist(p1);
            em.persist(p2);
            em.persist(p3);
            em.persist(p4);

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterEach
    public void tearDown() {
//        Remove any data after each test was run
    }

    @Test
    public void getAllzipcodes() {

        int exSize = 4;

        int actualSize = facade.getAllZipcodes().size();

        assertEquals(exSize, actualSize);
    }

    @Test
    public void zipNotSize() {
        int notexSize = 3;
        
        int actualSize = facade.getAllZipcodes().size();
        

        assertNotEquals(notexSize,actualSize);
    }
}
