package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.PersonDTO;
import utils.EMF_Creator;
import facades.PersonFacade;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

//Todo Remove or change relevant parts before ACTUAL use
@Path("person")
public class PersonResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();

    private static final PersonFacade FACADE = PersonFacade.getPersonFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @GET
    public String demo() {
        return "message: \"Hello, World\"";
    }
    
    @Path("/{phone}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getById(@PathParam("phone") int phone) {
        System.out.println(phone);
        PersonDTO personDTO = FACADE.getPersonByPhone(phone);

        return new Gson().toJson(personDTO);
    }
}
