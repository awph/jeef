/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.hearc.jeef.facade;

import ch.hearc.jeef.entities.Category;
import ch.hearc.jeef.entities.Topic;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Alexandre
 */
@Stateless
public class TopicFacade extends AbstractFacade<Topic> {
    @PersistenceContext(unitName = "jeefPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TopicFacade() {
        super(Topic.class);
    }

    public List<Topic> findRangeForCategory(int[] range, Category category) {
        Query query = em.createNamedQuery("Topic.findByCategory").setParameter("category", category);
        query.setMaxResults(range[1] - range[0] + 1);
        query.setFirstResult(range[0]);
        return query.getResultList();
    }

    public int countForCategory(Category category) {
        Query query = getEntityManager().createNamedQuery("Topic.countByCategory").setParameter("category", category);
        return ((Long) query.getSingleResult()).intValue();
    }
    
}
