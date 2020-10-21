package facades;

import dto.HobbyDTO;
import dto.PersonDTO;
import entities.Person;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import dto.PersonDTO;
import entities.Person;
import entities.Address;
import entities.Cityinfo;
import entities.Hobby;
import exceptions.HobbyNotFound;
import exceptions.PersonNotFound;

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
   
   
    
    public PersonDTO createPerson(PersonDTO personToCreate) {
        EntityManager em = getEntityManager();
        try {
            Person person = new Person(
                personToCreate.getPhone(),
                personToCreate.getEmail(),
                personToCreate.getFirstName(),
                personToCreate.getLastName());
            Address address = new Address(
                personToCreate.getStreet());
            Cityinfo city = em.find(Cityinfo.class, personToCreate.getZipcode());
            
            address.setZipcode(city);
            person.setAddress(address);
            
            em.getTransaction().begin();
            em.persist(person);
            em.getTransaction().commit();
            
            return personToCreate;
        }finally {
            em.close();
        }
    }
    
    public HobbyDTO addHobbyToPerson(int personID, String hobbyName) throws HobbyNotFound, PersonNotFound {
        EntityManager em = getEntityManager();
        try {
            Person person = em.find(Person.class, personID);
            Hobby hobby = em.find(Hobby.class, hobbyName);
            if(person == null) {
                throw new PersonNotFound("No person found by id " + personID);
            }
            if(hobby == null){
                throw new HobbyNotFound("No hobby found by id " + hobbyName);
            }
            person.addHobby(hobby);
            
            em.getTransaction().begin();
            
            em.persist(person);
            
            em.getTransaction().commit();
            return new HobbyDTO(hobby);
        }finally {
            em.close();
        }
    }
}
