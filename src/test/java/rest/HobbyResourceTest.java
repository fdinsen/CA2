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
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
//Uncomment the line below, to temporarily disable this test
//@Disabled

public class HobbyResourceTest {

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
        
    }

    @Test
    public void testServerIsUp() {
        System.out.println("Testing is server UP");
        given().when().get("hobby").then().statusCode(200);
    }

    @Test
    public void testGetAllHobbiesOnSize() {
        given().contentType("application/json").get("hobby").then().assertThat().body("size()", is(4));
    }
    
    @Test
    public void testGetAllHobbiesOnContent() {
        given().contentType("application/json").get("hobby").then().assertThat()
                .body("name", hasItem(h1.getName())).and()
                .body("name", hasItem(h2.getName())).and()
                .body("name", hasItem(h3.getName())).and()
                .body("name", hasItem(h4.getName()));
    }
    
    @Test
    public void testGetAllHobbiesOnContentWikilink() {
        given().contentType("application/json").get("hobby").then().assertThat()
                .body("wikilink", hasItem(h1.getWikilink())).and()
                .body("wikilink", hasItem(h2.getWikilink())).and()
                .body("wikilink", hasItem(h3.getWikilink())).and()
                .body("wikilink", hasItem(h4.getWikilink()));
    }
    
    @Test
    public void testGetAllHobbiesOnContentCategory() {
        given().contentType("application/json").get("hobby").then().assertThat()
                .body("category", hasItem(h1.getCategory())).and()
                .body("category", hasItem(h2.getCategory())).and()
                .body("category", hasItem(h3.getCategory())).and()
                .body("category", hasItem(h4.getCategory()));
    }
    
    @Test
    public void testGetAllHobbiesOnContentType() {
        given().contentType("application/json").get("hobby").then().assertThat()
                .body("type", hasItem(h1.getType())).and()
                .body("type", hasItem(h2.getType())).and()
                .body("type", hasItem(h3.getType())).and()
                .body("type", hasItem(h4.getType()));
    }
}
