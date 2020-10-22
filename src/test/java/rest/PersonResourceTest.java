package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entities.Address;
import entities.Cityinfo;
import entities.Person;
import dto.PersonDTO;
import entities.Hobby;
import static facades.PersonFacadeTest.h1;
import static facades.PersonFacadeTest.h2;
import static facades.PersonFacadeTest.h3;
import static facades.PersonFacadeTest.h4;
import static facades.PersonFacadeTest.hTest;
import static facades.PersonFacadeTest.test;
import utils.EMF_Creator;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.parsing.Parser;
import java.net.URI;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
//Uncomment the line below, to temporarily disable this test
//@Disabled

public class PersonResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    //private static RenameMe r1, r2;

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    Person p1;
    Person p2;
    Person p3;
    Person p4;

    Address a1;

    Cityinfo c;
    Cityinfo c1;
    Cityinfo c2;
    Cityinfo c3;

    public static Hobby h1, h2, h3, h4;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();

        EntityManager em = emf.createEntityManager();

        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;

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
    public static void closeTestServer() {
        //System.in.read();

        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }

    // Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the EntityClass used below to use YOUR OWN (renamed) Entity class
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

    @Test
    public void testServerIsUp() {
        System.out.println("Testing is server UP");
        given().when().get("person").then().statusCode(200);
    }

    @Test
    public void testGetPersonByPerson() {
        given()
                .get("/person/" + p1.getPhone())
                .then()
                .assertThat()
                .body("firstName", equalTo(p1.getFirstName()))
                .body("lastName", equalTo(p1.getLastName()))
                .body("email", equalTo(p1.getEmail()));
    }

    @Test
    public void testGetPersonError() {
        given().when().get("person/0").then().statusCode(500);
    }

    @Test
    public void testCreatePerson() {
        int phone = 77553399;
        String email = "frederik@dinsen.net";
        String firstName = "Frederik";
        String lastName = "Dinsen";
        String street = "Buddingevej 206";
        String zipcode = "3400";

        PersonDTO personToCreate = new PersonDTO(
                phone, email, firstName,
                lastName, street, zipcode);

        given()
                .contentType("application/json")
                .body(GSON.toJson(personToCreate))
                .post("person/")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("phone", equalTo(phone)).and()
                .body("email", equalTo(email)).and()
                .body("firstName", equalTo(firstName)).and()
                .body("street", equalTo(street)).and()
                .body("zipcode", equalTo(zipcode));

    }

    @Test
    public void testAddHobbyToPerson() {
        String hobbyName = "Dans";
        int personId = p1.getPhone();

        given()
                .contentType("application/json")
                .post("person/" + personId + "/hobby/" + hobbyName)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("hobbies[0].name", equalTo(hobbyName));
    }

    @Test
    public void testAddHobbyToPersonNonExistentPerson() {
        String hobbyName = "Dans";
        int personId = 1;

        given()
                .contentType("application/json")
                .post("person/" + personId + "/hobby/" + hobbyName)
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode());
    }

    @Test
    public void testAddHobbyToPersonNonExistentHobby() {
        String hobbyName = "Pastamaking";
        int personId = p1.getPhone();

        given()
                .contentType("application/json")
                .post("person/" + personId + "/hobby/" + hobbyName)
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode());
    }
}
