package facades;

import dto.HobbyDTO;
import entities.Cityinfo;
import entities.Address;
import entities.Person;
import dto.PersonDTO;
import entities.Hobby;
import exceptions.HobbyNotFound;
import exceptions.PersonNotFound;
import java.util.List;
import utils.EMF_Creator;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//Uncomment the line below, to temporarily disable this test
//@Disabled
public class PersonFacadeTest {

    private static EntityManagerFactory emf;
    private static PersonFacade facade;

    Person p1;
    Person p2;
    Person p3;
    Person p4;

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
            
            if(hTest == null) {
                h1 = new Hobby("Dans", "https://en.wikipedia.org/wiki/Dance", "Generel", "Indendørs");
                h2 = new Hobby("Skuespil", "https://en.wikipedia.org/wiki/Acting", "Generel", "Indendørs");
                h3 = new Hobby("Brætspil", "https://en.wikipedia.org/wiki/Board_game", "Generel", "Indendørs");
                h4 = new Hobby("Spil", "https://en.wikipedia.org/wiki/Games", "Generel", "Indendørs");
                
                em.persist(h1);
                em.persist(h2);
                em.persist(h3);
                em.persist(h4);
            }    
        } finally {
            em.getTransaction().commit();
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
            
            TypedQuery<Person> q = em.createQuery("SELECT p FROM Person p" ,Person.class);
            List<Person> list = q.getResultList();
            System.out.println("SIZE OF PERSON LIST: " + list.size());
            for(Person p : list) {
                System.out.println(p.getFirstName());
            }
        } finally {
            em.close();
        }
    }

    @AfterEach
    public void tearDown() {
//        Remove any data after each test was run
    }

    @Test
    public void getPersonByPhone() {

        String expected = p1.getFirstName();
        String exCity = p1.getAddress().getZipcode().getCity();
        String exStreet = p1.getAddress().getStreet();

        PersonDTO p = facade.getPersonByPhone(p1.getPhone());

        String actual = p.getFirstName();
        String actualCity = p.getCity();
        String acStreet = p.getStreet();

        assertEquals(expected, actual);
        assertEquals(exCity, actualCity);
        assertEquals(exStreet, acStreet);

    }

    @Test
    public void testCreatePerson() {
        //Arrange
        EntityManager em = emf.createEntityManager();
        int phone = 98127634;
        Person personToCreate = new Person(
                phone, "email@email.com",
                "Peter", "Petersen");
        Address addressToCreate = new Address(
                "Espegaardsvej 20");
        Cityinfo city = em.find(Cityinfo.class, "3360");
        if(city == null) {
            city = new Cityinfo("3360", "Liseleje");
        }
        addressToCreate.setZipcode(city);
        personToCreate.setAddress(addressToCreate);
        PersonDTO dto = new PersonDTO(personToCreate);

        //Act
        facade.createPerson(dto);

        //PersonDTO actual = facade.getPersonByPhone(phone);
        Person actual = em.find(Person.class, phone);

        //Assert
        assertEquals(dto.getEmail(), actual.getEmail());
    }

    @Test
    public void getPersonByPhoneError() {

        NoResultException assertThrows;

        assertThrows = Assertions.assertThrows(NoResultException.class, () -> {
            facade.getPersonByPhone(0);
        });
    }
    
    @Test
    public void testAddHobbyToPersonNonExistentHobby() {
        //Arrange
        int personId = p1.getPhone();
        String hobbyName = h1.getName();
        HobbyNotFound assertThrows;
        
        //Act
        assertThrows = Assertions.assertThrows(HobbyNotFound.class, () -> {
            facade.addHobbyToPerson(personId, "fake hobby");
        });
    }
    
    @Test
    public void testAddHobbyToPersonNonExistentPerson() {
        //Arrange
        int personId = p1.getPhone();
        String hobbyName = h1.getName();
        PersonNotFound assertThrows;
        
        //Act
        assertThrows = Assertions.assertThrows(PersonNotFound.class, () -> {
            facade.addHobbyToPerson(1, hobbyName);
        });
    }
    
    @Test
    public void testAddHobbyToPersonOnReturn() throws HobbyNotFound, PersonNotFound {
        //Arrange
        int personId = p1.getPhone();
        String hobbyName = h1.getName();
        
        //Act
        PersonDTO actual = facade.addHobbyToPerson(personId, hobbyName);
        
        assertEquals(h1.getType(), actual.getHobbies().get(0).getType());
        assertEquals(h1.getName(), actual.getHobbies().get(0).getName());
        assertEquals(h1.getCategory(), actual.getHobbies().get(0).getCategory());
        assertEquals(h1.getWikilink(), actual.getHobbies().get(0).getWikilink());
    }
    
    @Test
    public void testAddHobbyToPersonOnDB() throws HobbyNotFound, PersonNotFound {
        //Arrange
        EntityManager em = emf.createEntityManager();
        int personId = p2.getPhone();
        String hobbyName = h2.getName();
        int expectedHobbyAmount = 1;
        
        //Act
        facade.addHobbyToPerson(personId, hobbyName);
        
        Person actual = em.find(Person.class, personId);
        
        assertEquals(expectedHobbyAmount, actual.getHobbyList().size());
        assertEquals(h2.getName(), actual.getHobbyList().get(0).getName());
        assertEquals(h2.getCategory(), actual.getHobbyList().get(0).getCategory());
    }
    
    @Test
    public void testAddHobbyToPersonOnDBMultipleHobbies() throws HobbyNotFound, PersonNotFound {
        //Arrange
        EntityManager em = emf.createEntityManager();
        int personId = p2.getPhone();
        String hobbyName = h2.getName();
        String hobbyName2 = h3.getName();
        int expectedHobbyAmount = 2;
        
        //Act
        facade.addHobbyToPerson(personId, hobbyName);
        facade.addHobbyToPerson(personId, hobbyName2);
        
        Person actual = em.find(Person.class, personId);
        
        assertEquals(expectedHobbyAmount, actual.getHobbyList().size());
        assertEquals(h2.getName(), actual.getHobbyList().get(0).getName());
        assertEquals(h2.getCategory(), actual.getHobbyList().get(0).getCategory());
        
        assertEquals(h3.getName(), actual.getHobbyList().get(1).getName());
        assertEquals(h3.getCategory(), actual.getHobbyList().get(1).getCategory());
    }
}
