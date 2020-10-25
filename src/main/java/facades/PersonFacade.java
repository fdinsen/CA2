package facades;

import dto.PersonDTO;
import entities.Person;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

import entities.Address;
import entities.Cityinfo;
import entities.Hobby;
import exceptions.HobbyNotFound;
import exceptions.MalformedRequest;
import exceptions.PersonNotFound;
import exceptions.ZipcodeNotFound;

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

    public PersonDTO addHobbyToPerson(int personID, String hobbyName) throws HobbyNotFound, PersonNotFound, Exception {
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
            
            for (Hobby hobbyelement : person.getHobbyList()) {
                if(hobbyelement.getName().equals(hobby.getName())){
                    throw new Exception("This hobby: "+ hobbyName +" does allready excists on this person");
                }
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

    public PersonDTO deletePerson(int id) throws PersonNotFound {

        EntityManager em = getEntityManager();
        Person person;

        try {
            em.getTransaction().begin();

            person = em.find(Person.class, id);

            em.remove(person);

            em.getTransaction().commit();
        } catch (Exception e) {
            throw new PersonNotFound("No person found by id " + id);
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

    public List<PersonDTO> getPeopleWithSameZipcode(String zipcode) throws PersonNotFound, ZipcodeNotFound {
        EntityManager em = null;

        Cityinfo cityinfo = null;
        try {
            em = getEntityManager();
            cityinfo = em.find(Cityinfo.class, zipcode);
            if(cityinfo == null)  throw new ZipcodeNotFound("No zipcode/city found with zipcode: " + zipcode);
        } catch (Exception e) {
            throw new ZipcodeNotFound("No zipcode/city found with zipcode: " + zipcode);
        } finally {
            em.close();
        }


        List<PersonDTO> resPersons = new ArrayList<>();
        try {
            em = getEntityManager();

            em.getTransaction().begin();
            Query query = em.createQuery("SELECT p FROM Person p join p.address a where a.zipcode = :zipcode");

            query.setParameter("zipcode", cityinfo);

            List<Person> persons = query.getResultList();

            if(cityinfo == null || persons.size() == 0)  throw new PersonNotFound("No persons found with zipcode: " + zipcode);

            for (Person person: persons){
                PersonDTO DTOtoReturn = new PersonDTO(person);
                resPersons.add(DTOtoReturn);
            }
        } catch (Exception e) {
            throw new PersonNotFound("No persons found with zipcode: " + zipcode);
        } finally {
            em.close();
        }

        return resPersons;
    }

    public List<PersonDTO> getPeopleWithSameHobby(String hobbyName) throws PersonNotFound, HobbyNotFound {
        EntityManager em = null;

        Hobby hobby = null;
        try {
            em = getEntityManager();
            hobby = em.find(Hobby.class, hobbyName);

            if(hobby == null)  throw new HobbyNotFound("Hoppy not found with name: " + hobbyName);

        } catch (Exception e) {
            throw new HobbyNotFound("Hoppy not found with name: " + hobbyName);
        } finally {
            em.close();
        }


        List<PersonDTO> resPersons = new ArrayList<>();
        try {
            em = getEntityManager();

            em.getTransaction().begin();
            Query query = em.createQuery("SELECT p FROM Person p join p.hobbyList h where h = :hobby");

            query.setParameter("hobby", hobby);

            List<Person> persons = query.getResultList();

            if(persons == null || persons.size() == 0)  throw new PersonNotFound("No persons found with hobby: " + hobbyName);

            for (Person person: persons){
                PersonDTO DTOtoReturn = new PersonDTO(person);
                resPersons.add(DTOtoReturn);
            }
        } catch (Exception e) {
            throw new PersonNotFound("No persons found with hobby: " + hobbyName);
        } finally {
            em.close();
        }

        return resPersons;
    }


    public PersonDTO updatePerson(PersonDTO updatedPerson) throws MalformedRequest, PersonNotFound, ZipcodeNotFound {
        try {
            if(updatedPerson.getPid() < 1
                    || isNullOrEmpty(updatedPerson.getEmail())
                    || isNullOrEmpty(updatedPerson.getFirstName())
                    || isNullOrEmpty(updatedPerson.getLastName())
                    || isNullOrEmpty(updatedPerson.getStreet())
                    || isNullOrEmpty(updatedPerson.getZipcode())){
                throw new MalformedRequest("Error, person must contain pid, phone, email, first name, last name, street and zipcode");
            };
        } catch (Exception e) {
            throw new MalformedRequest("Error, person must contain pid, phone, email, first name, last name, street and zipcode");
        }


        EntityManager em = null;
        Person person = null;
        try {
            em = getEntityManager();
            person = em.find(Person.class, updatedPerson.getPid());
            if(person == null)  throw new PersonNotFound("No person found with id: " + updatedPerson.getPid());
        } catch (Exception e) {
            throw new PersonNotFound("No person found with id: " + updatedPerson.getPid());
        } finally {
            em.close();
        }

        Cityinfo cityinfo = null;
        try {
            em = getEntityManager();
            cityinfo = em.find(Cityinfo.class, updatedPerson.getZipcode());
            if(cityinfo == null)  throw new ZipcodeNotFound("No zipcode/city found with zipcode: " + updatedPerson.getZipcode());
        } catch (Exception e) {
            throw new ZipcodeNotFound("No zipcode/city found with zipcode: " + updatedPerson.getZipcode());
        } finally {
            em.close();
        }

        Address address = new Address(updatedPerson.getStreet());
        address.setZipcode(cityinfo);


        try {
            em = getEntityManager();

            person.setFirstName(updatedPerson.getFirstName());
            person.setLastName(updatedPerson.getLastName());
            person.setEmail(updatedPerson.getEmail());
            person.setAddress(address);
            person.setPhone(updatedPerson.getPhone());

            em.getTransaction().begin();
            em.merge(person);
            em.getTransaction().commit();

            return new PersonDTO(person);
        } catch (Exception ex) {
            throw new MalformedRequest("Something went wrong while trying to update person");
        } finally {
            em.close();
        }
    }


    public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.isEmpty())
            return false;
        return true;
    }
}
