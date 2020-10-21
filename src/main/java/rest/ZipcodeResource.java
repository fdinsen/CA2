/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.PersonDTO;
import dto.ZipcodesDTO;
import facades.PersonFacade;
import facades.ZipcodeFacade;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import utils.EMF_Creator;

/**
 *
 * @author simon
 */
@Path("zipcodes")
public class ZipcodeResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();

    private static final ZipcodeFacade FACADE = ZipcodeFacade.getZipcodeFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @GET
    public String demo() {
        return "message: \"Hello, World\"";
    }
    
    @Path("/all")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getAllZipcodes() {

        ZipcodesDTO zcDTO = FACADE.getAllZipcodes();

        return new Gson().toJson(zcDTO);
    }
    
}
