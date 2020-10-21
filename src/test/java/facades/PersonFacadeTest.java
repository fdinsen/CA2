package facades;

import entities.Cityinfo;
import entities.Address;
import entities.Person;
import dto.PersonDTO;
import utils.EMF_Creator;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
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

    public PersonFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = PersonFacade.getPersonFacade(emf);
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
    public void getPersonByPhone() {

        String expected = p1.getFirstName();

        PersonDTO p = facade.getPersonByPhone(p1.getPhone());

        String actual = p.getFirstName();

        assertEquals(expected, actual);

    }

    public void testCreatePerson() {
        //Arrange
        EntityManager em = emf.createEntityManager();
        int phone = 12345678;
        Person personToCreate = new Person(
                phone, "email@email.com",
                "Peter", "Petersen");
        Address addressToCreate = new Address(
                "Espegaardsvej 20");
        Cityinfo city = em.find(Cityinfo.class, "2860");
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
}
