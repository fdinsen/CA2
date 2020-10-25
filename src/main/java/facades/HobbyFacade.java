package facades;

import dto.HobbyDTO;
import dto.PersonDTO;
import entities.Person;
import java.util.List;
import javax.persistence.*;

import dto.PersonDTO;
import entities.Person;
import entities.Address;
import entities.Cityinfo;
import entities.Hobby;
import exceptions.HobbyNotFound;
import exceptions.MalformedRequest;
import exceptions.PersonNotFound;
import java.util.ArrayList;

/**
 *
 * Rename Class to a relevant name Add add relevant facade methods
 */
public class HobbyFacade {

    private static HobbyFacade instance;
    private static EntityManagerFactory emf;
    
    //Private Constructor to ensure Singleton
    private HobbyFacade() {}
    
    
    /**
     * 
     * @param _emf
     * @return an instance of this facade class.
     */
    public static HobbyFacade getHobbyFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new HobbyFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    public List<HobbyDTO> getAllHobbies() {
        EntityManager em = getEntityManager();
        TypedQuery<Hobby> q = em.createNamedQuery("Hobby.findAll", Hobby.class);
        List<HobbyDTO> hobbyList = new ArrayList();
        q.getResultList().stream().forEach(hobby -> {
            hobbyList.add(new HobbyDTO(hobby));
        });
        return hobbyList;
    }
}
