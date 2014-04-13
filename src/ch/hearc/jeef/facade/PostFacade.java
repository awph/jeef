/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hearc.jeef.facade;

import ch.hearc.jeef.entities.Post;
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
public class PostFacade extends AbstractFacade<Post> {

    @PersistenceContext(unitName = "jeefPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PostFacade() {
        super(Post.class);
    }

    public List<Post> findRangeForTopic(int[] range, Topic topic) {
        Query query = getEntityManager().createNamedQuery("Post.findByTopic").setParameter("topic", topic);
        query.setMaxResults(range[1] - range[0] + 1);
        query.setFirstResult(range[0]);
        return query.getResultList();
    }

    public int countForTopic(Topic topic) {
        Query query = getEntityManager().createNamedQuery("Post.countByTopic").setParameter("topic", topic);
        return ((Long) query.getSingleResult()).intValue();
    }

}
