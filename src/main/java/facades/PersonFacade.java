package facades;

import dto.PersonDTO;
import entities.Person;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

/**
 *
 * Rename Class to a relevant name Add add relevant facade methods
 */
public class PersonFacade {

    private static PersonFacade instance;
    private static EntityManagerFactory emf;
    
    //Private Constructor to ensure Singleton
    private PersonFacade() {}
    
    
    /**
     * 
     * @param _emf
     * @return an instance of this facade class.
     */
    public static PersonFacade getPersonFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PersonFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    public PersonDTO getPersonByPhone(int phone){
        
        EntityManager em = emf.createEntityManager();
        
        TypedQuery<Person> query = em.createQuery("SELECT p FROM Person p WHERE p.phone = :phone",Person.class);
        
        query.setParameter("phone", phone);
        
        Person p = query.getSingleResult();
        
        return new PersonDTO(p);
    }
    
}
