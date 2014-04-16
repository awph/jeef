package ch.hearc.jeef.facade;

import ch.hearc.jeef.entities.Category;
import ch.hearc.jeef.entities.Topic;
import java.util.Collections;
import java.util.Iterator;
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

    public List<Topic> findRangeForKeywords(int[] range, List<String> keywords) {
        Query query = getEntityManager().createNamedQuery("Topic.findForKeywords").setParameter("keywords", keywordsQuery(keywords));
        query.setMaxResults(range[1] - range[0] + 1);
        query.setFirstResult(range[0]);
        return query.getResultList();
    }

    public int countForKeywords(List<String> keywords) {
        Query query = getEntityManager().createNamedQuery("Topic.countForKeywords").setParameter("keywords", keywordsQuery(keywords));
        return ((Long) query.getSingleResult()).intValue();
    }

    private String keywordsQuery(List<String> keywords) {
        if (keywords == null) {
            return null;
        }
        StringBuilder keywordsQuery = new StringBuilder();
        Iterator<String> iterator = keywords.iterator();
        while (iterator.hasNext()) {
            keywordsQuery.append("%");
            keywordsQuery.append(iterator.next());
            keywordsQuery.append("%");
            if (iterator.hasNext()) {
                keywordsQuery.append(" AND ");
            }
        }
        return keywordsQuery.toString();
    }

    public List<Topic> findRangeAdvanced(int[] range, List<String> keywords, String username, Category category, String sortby, Boolean desc) {
        Query query;
        if (category != null) {
            query = getEntityManager().createNamedQuery("Topic.findAdvancedWithCategory");
        } else {
            query = getEntityManager().createNamedQuery("Topic.findAdvanced");

        }
        query.setParameter("keywords", keywordsQuery(keywords));
        query.setParameter("username", username);
        if (category != null) {
            query.setParameter("category", category);
        }
        query.setMaxResults(range[1] - range[0] + 1); //TODO check
        query.setFirstResult(range[0]);
        List<Topic> results = query.getResultList();
        if (desc) {
            Collections.reverse(results);
        }
        return results;
    }

    public int countAdvanced(List<String> keywords, String username, Category category) {
        Query query;
        if (category != null) {
            query = getEntityManager().createNamedQuery("Topic.countAdvancedWithCategory");
        } else {
            query = getEntityManager().createNamedQuery("Topic.countAdvanced");

        }
        query.setParameter("keywords", keywordsQuery(keywords));
        query.setParameter("username", username);
        if (category != null) {
            query.setParameter("category", category);
        }
        return ((Long) query.getSingleResult()).intValue();
    }
}
