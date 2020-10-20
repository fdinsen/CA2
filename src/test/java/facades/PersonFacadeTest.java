package facades;

import entities.Person;
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
    public void getPersonByPhone(){
        
        String expected = p1.getFirstName();
        
        String actual = facade.getPersonByPhone(p1.getPhone()).getFirstName();
        
        assertEquals(expected, actual);
                
    }
    
    

}
