package facades;

import entities.Cityinfo;
import entities.Address;
import entities.Person;
import dto.PersonDTO;
import entities.Hobby;
import exceptions.HobbyNotFound;
import exceptions.MalformedRequest;
import exceptions.PersonNotFound;
import exceptions.ZipcodeNotFound;
import utils.EMF_Creator;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

//Uncomment the line below, to temporarily disable this test
////@Disabled
public class PersonFacadeTest {

    private static EntityManagerFactory emf;
    private static PersonFacade facade;

    Person p1;
    Person p2;
    Person p3;
    Person p4;
    Person p5;

    Address a1;

    public static Cityinfo test;
    public static Hobby hTest;

    Cityinfo c;
    Cityinfo c1;
    Cityinfo c2;
    Cityinfo c3;

    public static Hobby h1, h2, h3, h4;

    public PersonFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = PersonFacade.getPersonFacade(emf);

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            test = em.find(Cityinfo.class, "3400");
            hTest = em.find(Hobby.class, "Spil");

            if (test == null) {

                Cityinfo c = new Cityinfo("3360", "Liseleje");
                Cityinfo c1 = new Cityinfo("3370", "Melby");
                Cityinfo c2 = new Cityinfo("3390", "Hundested");
                Cityinfo c3 = new Cityinfo("3400", "Hillerød");

                em.persist(c);
                em.persist(c1);
                em.persist(c2);
                em.persist(c3);
            }

            if (hTest == null) {
                h1 = new Hobby("Dans", "https://en.wikipedia.org/wiki/Dance", "Generel", "Indendørs");
                h2 = new Hobby("Skuespil", "https://en.wikipedia.org/wiki/Acting", "Generel", "Indendørs");
                h3 = new Hobby("Brætspil", "https://en.wikipedia.org/wiki/Board_game", "Generel", "Indendørs");
                h4 = new Hobby("Spil", "https://en.wikipedia.org/wiki/Games", "Generel", "Indendørs");

                em.persist(h1);
                em.persist(h2);
                em.persist(h3);
                em.persist(h4);
            } else {
                h1 = em.find(Hobby.class, "Dans");
                h2 = em.find(Hobby.class, "Skuespil");
                h3 = em.find(Hobby.class, "Brætspil");
                h4 = em.find(Hobby.class, "Spil");
            }
            em.getTransaction().commit();
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

        try {
            em.getTransaction().begin();
            em.createNamedQuery("Person.deleteFrom").executeUpdate();

            p1 = new Person(12345678, "mail", "navn", "andetNavn");
            p2 = new Person(23456789, "mail1", "navn1", "andetNavn1");
            p3 = new Person(34567890, "mail2", "navn2", "andetNavn2");
            p4 = new Person(45678901, "mail3", "navn3", "andetNavn3");
            p5 = new Person(45678901, "mail4", "navn4", "andetNavn4");

            h1 = em.find(Hobby.class, "Dans");
            h2 = em.find(Hobby.class, "Skuespil");
            h3 = em.find(Hobby.class, "Brætspil");
            h4 = em.find(Hobby.class, "Spil");

            p1.addHobby(h1);
            p2.addHobby(h1);
            p3.addHobby(h1);
            p4.addHobby(h1);

            p1.addHobby(h2);
            p2.addHobby(h2);
            p3.addHobby(h2);

            Cityinfo c4 = em.find(Cityinfo.class, "3400");

            a1 = new Address(1, "vej vej");
            a1.setAdditionalInfo("Ingen ting her");
            a1.setZipcode(c4);

            p1.setAddress(a1);
            p2.setAddress(a1);

            em.persist(p1);
            em.persist(p2);
            em.persist(p3);
            em.persist(p4);
            em.persist(p5);

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterEach
    public void tearDown() {
//        Remove any data after each test was run
    }

    //@Disabled
    @Test
    public void getPersonById() throws PersonNotFound {

        String expected = p1.getFirstName();
        String exCity = p1.getAddress().getZipcode().getCity();
        String exStreet = p1.getAddress().getStreet();

        PersonDTO p = facade.getPersonById(p1.getId());

        String actual = p.getFirstName();
        String actualCity = p.getCity();
        String acStreet = p.getStreet();

        assertEquals(expected, actual);
        assertEquals(exCity, actualCity);
        assertEquals(exStreet, acStreet);

    }

    //@Disabled
    @Test
    public void testCreatePerson() throws MalformedRequest {
        //Arrange
        EntityManager em = emf.createEntityManager();
        int phone = 98127634;
        Person personToCreate = new Person(
                phone, "email@email.com",
                "Peter", "Petersen");
        Address addressToCreate = new Address(
                "Espegaardsvej 20");
        Cityinfo city = em.find(Cityinfo.class, "3360");
        if (city == null) {
            city = new Cityinfo("3360", "Liseleje");
        }
        addressToCreate.setZipcode(city);
        personToCreate.setAddress(addressToCreate);
        PersonDTO dto = new PersonDTO(personToCreate);

        //Act
        dto = facade.createPerson(dto);

        //PersonDTO actual = facade.getPersonByPhone(phone);
        Person actual = em.find(Person.class, dto.getPid());

        //Assert
        assertEquals(dto.getEmail(), actual.getEmail());
    }

    //@Disabled
    @Test
    public void getPersonByIdError() {

        PersonNotFound assertThrows;

        assertThrows = Assertions.assertThrows(PersonNotFound.class, () -> {
            facade.getPersonById(0);
        });
        Assertions.assertNotNull(assertThrows);
    }

    //@Disabled
    @Test
    public void testAddHobbyToPersonNonExistentHobby() {
        //Arrange
        int personId = p1.getId();
        String hobbyName = h1.getName();
        HobbyNotFound assertThrows;

        //Act
        assertThrows = Assertions.assertThrows(HobbyNotFound.class, () -> {
            facade.addHobbyToPerson(personId, "fake hobby");
        });
        Assertions.assertNotNull(assertThrows);
    }

    //@Disabled
    @Test
    public void testAddHobbyToPersonNonExistentPerson() {
        //Arrange
        int personId = p1.getId();
        String hobbyName = h1.getName();
        PersonNotFound assertThrows;

        //Act
        assertThrows = Assertions.assertThrows(PersonNotFound.class, () -> {
            facade.addHobbyToPerson(1, hobbyName);
        });
        Assertions.assertNotNull(assertThrows);
    }

    //@Disabled
    @Test
    public void testAddHobbyToPersonOnReturn() throws HobbyNotFound, PersonNotFound {
        //Arrange
        int personId = p5.getId();
        String hobbyName = h4.getName();

        //Act
        PersonDTO actual = facade.addHobbyToPerson(personId, hobbyName);

        assertEquals(h4.getType(), actual.getHobbies().get(0).getType());
        assertEquals(h4.getName(), actual.getHobbies().get(0).getName());
        assertEquals(h4.getCategory(), actual.getHobbies().get(0).getCategory());
        assertEquals(h4.getWikilink(), actual.getHobbies().get(0).getWikilink());
    }

    //@Disabled
    @Test
    public void testAddHobbyToPersonOnDB() throws HobbyNotFound, PersonNotFound {
        //Arrange
        EntityManager em = emf.createEntityManager();
        int personId = p5.getId();
        String hobbyName = h4.getName();
        int expectedHobbyAmount = 1;

        //Act
        facade.addHobbyToPerson(personId, hobbyName);

        Person actual = em.find(Person.class, personId);

        assertEquals(expectedHobbyAmount, actual.getHobbyList().size());
        assertEquals(h4.getName(), actual.getHobbyList().get(0).getName());
        assertEquals(h4.getCategory(), actual.getHobbyList().get(0).getCategory());
    }

    //@Disabled
    @Test
    public void testAddHobbyToPersonOnDBMultipleHobbies() throws HobbyNotFound, PersonNotFound {
        //Arrange
        EntityManager em = emf.createEntityManager();
        int personId = p5.getId();
        String hobbyName = h4.getName();
        String hobbyName2 = h3.getName();
        int expectedHobbyAmount = 2;

        //Act
        facade.addHobbyToPerson(personId, hobbyName);
        facade.addHobbyToPerson(personId, hobbyName2);

        Person actual = em.find(Person.class, personId);

        assertEquals(expectedHobbyAmount, actual.getHobbyList().size());
        assertEquals(h4.getName(), actual.getHobbyList().get(0).getName());
        assertEquals(h4.getCategory(), actual.getHobbyList().get(0).getCategory());

        assertEquals(h3.getName(), actual.getHobbyList().get(1).getName());
        assertEquals(h3.getCategory(), actual.getHobbyList().get(1).getCategory());
    }

    //@Disabled
    @Test
    public void testDeletePerson() throws NoResultException, PersonNotFound {

        PersonNotFound assertThrows;

        PersonDTO pdto = facade.deletePerson(p1.getId());

        assertThrows = Assertions.assertThrows(PersonNotFound.class, () -> {
            facade.getPersonById(p1.getId());
        });

        Assertions.assertNotNull(assertThrows);
        Assertions.assertNotNull(pdto);
    }

    //@Disabled
    @Test
    public void testDeletePersonError() throws PersonNotFound {

        PersonNotFound assertThrows;

        assertThrows = Assertions.assertThrows(PersonNotFound.class, () -> {
            facade.getPersonById(0);
        });
        Assertions.assertNotNull(assertThrows);
    }

    @Test
    public void TestgetCountOfHobby() throws HobbyNotFound {

        int exSize = 4;

        int acSize = facade.getCountOfPeopleWithHobby(h1.getName());

        assertEquals(exSize, acSize);
        
    }
    
    @Test
    public void testDeleteSingleHobbyFromPerson() throws HobbyNotFound, PersonNotFound {
        //Arrange
        int personId = p4.getId();
        String hobbyName = h3.getName();
        EntityManager em = emf.createEntityManager();
        facade.addHobbyToPerson(personId, hobbyName);
        int expSize = 1;
        
        //Act
        facade.removeHobbyFromPerson(personId, hobbyName);
        Person personAfter = em.find(Person.class, p4.getId());
        

        assertEquals(expSize, personAfter.getHobbyList().size());
    }
    
    @Test
    public void testDeleteNonExistentHobbyFromPerson() throws HobbyNotFound, PersonNotFound {
        //Arrange
        int personId = p4.getId();
        String hobbyName = "Pastamaking";
        
        HobbyNotFound assertThrows;
        assertThrows = Assertions.assertThrows(HobbyNotFound.class, () -> {
            facade.removeHobbyFromPerson(personId, hobbyName);
        });
        assertNotNull(assertThrows);
    }
    
        @Test
    public void testDeleteHobbyFromNonExistentPerson() throws HobbyNotFound, PersonNotFound {
        //Arrange
        int personId = 1456;
        String hobbyName = h3.getName();
        
        PersonNotFound assertThrows;
        assertThrows = Assertions.assertThrows(PersonNotFound.class, () -> {
            facade.removeHobbyFromPerson(personId, hobbyName);
        });
        assertNotNull(assertThrows);
    }

    @Test
    public void testGetCountOfHobbyZero() throws HobbyNotFound {

        int exSize = 0;

        int acSize = facade.getCountOfPeopleWithHobby("4kjfdkjfkjb");

        assertEquals(exSize, acSize);
    }

    @Test
    public void testGetPeopleWithSameZipcode() throws PersonNotFound, ZipcodeNotFound {
        int expectedSize = 2;

        List<PersonDTO> persons = facade.getPeopleWithSameZipcode("3400");
        int actualSize = persons.size();

        assertEquals(expectedSize, actualSize);
    }

    @Test
    public void testGetPeopleWithSameZipcodeWrongZipcode() throws ZipcodeNotFound {
        ZipcodeNotFound assertThrows = Assertions.assertThrows(ZipcodeNotFound.class, () -> {
            facade.getPeopleWithSameZipcode("2");
        });

        assertNotNull(assertThrows);
    }

    @Test
    public void testGetPeopleWithSameZipcodeCorrectZipCodeNoPeople() throws PersonNotFound {
        PersonNotFound assertThrows = Assertions.assertThrows(PersonNotFound.class, () -> {
            facade.getPeopleWithSameZipcode("3360");
        });

        assertNotNull(assertThrows);
    }

    @Test
    public void testGetPeopleWithSameHobby() throws PersonNotFound, HobbyNotFound {
        int expectedSize = 3;

        List<PersonDTO> persons = facade.getPeopleWithSameHobby(h2.getName());
        int actualSize = persons.size();

        assertEquals(expectedSize, actualSize);
    }

    @Test
    public void testGetPeopleWithSameHobbyWrongHobby() throws HobbyNotFound {
        HobbyNotFound assertThrows = Assertions.assertThrows(HobbyNotFound.class, () -> {
            facade.getPeopleWithSameHobby("NotFound");
        });

        assertNotNull(assertThrows);
    }

    @Test
    public void testGetPeopleWithSameHobbyZipCodeNoPeople() throws PersonNotFound {
        PersonNotFound assertThrows = Assertions.assertThrows(PersonNotFound.class, () -> {
            facade.getPeopleWithSameHobby(h3.getName());
        });

        assertNotNull(assertThrows);
    }

    @Test
    public void testUpdatePerson() throws ZipcodeNotFound, PersonNotFound, MalformedRequest {
        String expectedEmail = "newEmail@gmail.com";
        System.out.println(p1.getAddress().getStreet());
        System.out.println(p1.getAddress().getZipcode().getZipcode());

        PersonDTO personDTO = new PersonDTO(p1.getId(),p1.getPhone(),expectedEmail,p1.getFirstName(),p1.getLastName(),p1.getAddress().getStreet(),p1.getAddress().getZipcode().getZipcode());
       PersonDTO updatedPerson = facade.updatePerson(personDTO);

        assertEquals(expectedEmail, updatedPerson.getEmail());
    }

    @Test
    public void testUpdatePersonEmptyEmail() throws ZipcodeNotFound, PersonNotFound, MalformedRequest {
        String expectedEmail = "";
        PersonDTO personDTO = new PersonDTO(p1.getId(),p1.getPhone(),expectedEmail,p1.getFirstName(),p1.getLastName(),p1.getAddress().getStreet(),p1.getAddress().getZipcode().getZipcode());

        MalformedRequest assertThrows = Assertions.assertThrows(MalformedRequest.class, () -> {
            facade.updatePerson(personDTO);
        });

        assertNotNull(assertThrows);
    }

    @Test
    public void testUpdatePersonWrongZipcode() throws ZipcodeNotFound, PersonNotFound, MalformedRequest {
        PersonDTO personDTO = new PersonDTO(p1.getId(),p1.getPhone(),p1.getEmail(),p1.getFirstName(),p1.getLastName(),p1.getAddress().getStreet(),"12344321");

        ZipcodeNotFound assertThrows = Assertions.assertThrows(ZipcodeNotFound.class, () -> {
            facade.updatePerson(personDTO);
        });

        assertNotNull(assertThrows);
    }


    //Er ikke et instanceof WebApplicationException ???
    @Disabled
    @Test
    public void testUpdatePersonWrongPID() throws ZipcodeNotFound, PersonNotFound, MalformedRequest {
        PersonDTO personDTO = new PersonDTO(-1,p1.getPhone(),p1.getEmail(),p1.getFirstName(),p1.getLastName(),p1.getAddress().getStreet(),p1.getAddress().getZipcode().getZipcode());

        PersonNotFound assertThrows = Assertions.assertThrows(PersonNotFound.class, () -> {
            facade.updatePerson(personDTO);
        });

        assertNotNull(assertThrows);
    }
}
