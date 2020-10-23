package facades;

import dto.HobbyDTO;
import entities.Cityinfo;
import entities.Address;
import entities.Person;
import dto.PersonDTO;
import entities.Hobby;
import exceptions.HobbyNotFound;
import exceptions.MalformedRequest;
import exceptions.PersonNotFound;
import java.util.List;
import utils.EMF_Creator;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//Uncomment the line below, to temporarily disable this test
//@Disabled
public class HobbyFacadeTest {

    private static EntityManagerFactory emf;
    private static HobbyFacade facade;

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

    public HobbyFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = HobbyFacade.getHobbyFacade(emf);

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
            }else {
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
    }

    @AfterEach
    public void tearDown() {
//        Remove any data after each test was run
    }

    @Test
    public void testGetAllHobbiesOnSize() {
        //arrange
        int expSize = 4;
        
        //Act
        List<HobbyDTO> actual = facade.getAllHobbies();
        
        
        //Assert
        assertEquals(expSize, actual.size());
    }
}
