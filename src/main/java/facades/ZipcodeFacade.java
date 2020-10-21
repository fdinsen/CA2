/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import dto.ZipcodesDTO;
import entities.Cityinfo;
import entities.Person;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

/**
 *
 * @author simon
 */
public class ZipcodeFacade {

    private static ZipcodeFacade instance;
    private static EntityManagerFactory emf;

    //Private Constructor to ensure Singleton
    private ZipcodeFacade() {
    }

    /**
     *
     * @param _emf
     * @return an instance of this facade class.
     */
    public static ZipcodeFacade getPersonFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new ZipcodeFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public ZipcodesDTO getAllZipcodes() {
        EntityManager em = getEntityManager();

        TypedQuery<Cityinfo> q = em.createQuery("SELECT c FROM Cityinfo c", Cityinfo.class);
        
        ZipcodesDTO zcdto = new ZipcodesDTO(q.getResultList());

        return zcdto;
    }

}
