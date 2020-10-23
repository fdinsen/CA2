package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import dto.PersonDTO;
import exceptions.HobbyNotFound;
import exceptions.MalformedRequest;
import exceptions.PersonNotFound;
import exceptions.ZipcodeNotFound;
import utils.EMF_Creator;
import facades.PersonFacade;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

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

    @Path("/{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getById(@PathParam("id") int id) throws PersonNotFound {

        PersonDTO personDTO = FACADE.getPersonById(id);

        return new Gson().toJson(personDTO);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPerson(String person) throws MalformedRequest {
        try {
            PersonDTO personToCreate = GSON.fromJson(person, PersonDTO.class);
            PersonDTO createdPerson = FACADE.createPerson(personToCreate);
            return Response.ok().entity(GSON.toJson(createdPerson)).build();
        }catch (JsonSyntaxException ex) {
            throw new MalformedRequest("Error, person must contain phone, email, first name, last name, street and zipcode");
        }
        

    }
    
    @Path("{pid}/hobby/{hname}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addHobbyToPerson(
            @PathParam("pid") int pid,
            @PathParam("hname") String hobbyName) throws HobbyNotFound, PersonNotFound {
        PersonDTO hobby = FACADE.addHobbyToPerson(pid, hobbyName);
        return Response.ok().entity(GSON.toJson(hobby)).build();
    }
    
    @Path("/{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public String deletePerson(@PathParam("id") int id) throws PersonNotFound {
        
        PersonDTO pDTO = FACADE.deletePerson(id);
        
        return "{\"status\" :\"200\", \"msg\": \"person deleted\"}" ;

    }
    
    @Path("{pid}/hobby/{hname}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public String removeHobbyFromPerson(
            @PathParam("pid") int personId, 
            @PathParam("hname") String hobbyName) throws HobbyNotFound, PersonNotFound {
        FACADE.removeHobbyFromPerson(personId, hobbyName);
        return "{msg: \"hobby deleted from person\"}";
    }
  
    @Path("/hobby/{hid}/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String getHobbyCount(@PathParam("hid") String hobbyId) throws HobbyNotFound{
        System.out.println(hobbyId);
        int count = FACADE.getCountOfPeopleWithHobby(hobbyId);
        
        return "{\"count\":"+count+"}";
    }

    @Path("/city/{zipcode}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String getPersonsWithSameZipcode(@PathParam("zipcode") String zipcode) throws PersonNotFound, ZipcodeNotFound {
        List<PersonDTO> persons = FACADE.getPeopleWithSameZipcode(zipcode);

        return new Gson().toJson(persons);
    }

    @Path("/hobby/{hobby}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String getPersonsWithSameHobby(@PathParam("hobby") String hobby) throws PersonNotFound, HobbyNotFound {
        List<PersonDTO> persons = FACADE.getPeopleWithSameHobby(hobby);

        return new Gson().toJson(persons);
    }
}
