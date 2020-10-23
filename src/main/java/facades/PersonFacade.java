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

/**
 *
 * Rename Class to a relevant name Add add relevant facade methods
 */
public class PersonFacade {

    private static PersonFacade instance;
    private static EntityManagerFactory emf;

    //Private Constructor to ensure Singleton
    private PersonFacade() {
    }

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

    public PersonDTO getPersonById(int id) throws PersonNotFound {

        EntityManager em = emf.createEntityManager();

        TypedQuery<Person> query = em.createQuery("SELECT p FROM Person p WHERE p.id = :id", Person.class);

        query.setParameter("id", id);

        try {
            Person p = query.getSingleResult();
            return new PersonDTO(p);
        } catch (NoResultException ex) {
            throw new PersonNotFound("No person found by id " + id);
        }

    }

    public PersonDTO createPerson(PersonDTO personToCreate) throws MalformedRequest {
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

            personToCreate.setPid(person.getId());
            return personToCreate;
        } catch (Exception ex) {
            throw new MalformedRequest("Error, person must contain phone, email, first name, last name, street and zipcode");
        } finally {
            em.close();
        }
    }

    public PersonDTO addHobbyToPerson(int personID, String hobbyName) throws HobbyNotFound, PersonNotFound {
        EntityManager em = getEntityManager();
        try {
            Person person = em.find(Person.class, personID);
            Hobby hobby = em.find(Hobby.class, hobbyName);
            if (person == null) {
                throw new PersonNotFound("No person found by id " + personID);
            }
            if (hobby == null) {
                throw new HobbyNotFound("No hobby found by id " + hobbyName);
            }
            person.addHobby(hobby);

            em.getTransaction().begin();

            em.persist(person);

            em.getTransaction().commit();

            PersonDTO toReturn = new PersonDTO(person);
            return toReturn;
        } finally {
            em.close();
        }
    }

    public PersonDTO deletePerson(int phone) throws PersonNotFound {

        EntityManager em = getEntityManager();
        Person person;

        try {
            em.getTransaction().begin();

            person = em.find(Person.class, phone);

            em.remove(person);

            em.getTransaction().commit();
        } catch (Exception e) {
            throw new PersonNotFound("No person found by id " + phone);
        } finally {
            em.close();
        }

        return new PersonDTO(person);
    }


    public int getCountOfPeopleWithHobby(String hobbyId) throws HobbyNotFound {
        EntityManager em = null;

        int size = -1;
        try {
            em = getEntityManager();
            em.getTransaction().begin();

            Query query = em.createQuery("SELECT count(p) FROM Person p join p.hobbyList u where u.name = :name");
            
            query.setParameter("name", hobbyId);

            Long temp = (long) query.getSingleResult();

            size = temp.intValue();

        } catch (Exception e) {
            throw new HobbyNotFound("hobby not found" + hobbyId);
        } finally {
            em.close();
        }

        return size;
    }
    
    public void removeHobbyFromPerson(int personId, String hobbyName) throws HobbyNotFound, PersonNotFound {
        boolean removedSuccessfully;
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Person person = em.find(Person.class, personId);
            if(person == null) {
                throw new PersonNotFound("No person found with id: " + personId);
            }
            
            removedSuccessfully = person.removeHobby(hobbyName);
            if(!removedSuccessfully) {
                throw new HobbyNotFound("No hobby found by name " + hobbyName + " on person with id: " + personId);
            }
            em.getTransaction().commit();
            
        }finally {
            em.close();
        }

    }
}
