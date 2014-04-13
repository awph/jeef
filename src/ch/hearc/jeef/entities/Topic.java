/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.hearc.jeef.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Alexandre
 */
@Entity
@Table(name = "topic")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Topic.findAll", query = "SELECT t FROM Topic t"),
    @NamedQuery(name = "Topic.findById", query = "SELECT t FROM Topic t WHERE t.id = :id"),
    @NamedQuery(name = "Topic.findByTitle", query = "SELECT t FROM Topic t WHERE t.title = :title"),
    @NamedQuery(name = "Topic.findByDate", query = "SELECT t FROM Topic t WHERE t.date = :date"),
    @NamedQuery(name = "Topic.findByLocked", query = "SELECT t FROM Topic t WHERE t.locked = :locked"),
    @NamedQuery(name = "Topic.findByPinned", query = "SELECT t FROM Topic t WHERE t.pinned = :pinned"),
    @NamedQuery(name = "Topic.findByCategory", query = "SELECT t FROM Topic t WHERE t.category = :category"),
    @NamedQuery(name = "Topic.countByCategory", query = "SELECT COUNT(t) FROM Topic t WHERE t.category = :category")})
public class Topic implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "title")
    private String title;
    @Basic(optional = false)
    @NotNull
    @Column(name = "date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    @Basic(optional = false)
    @NotNull
    @Column(name = "locked")
    private boolean locked;
    @Basic(optional = false)
    @NotNull
    @Column(name = "pinned")
    private boolean pinned;
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Category category;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "topic")
    private Collection<Post> postCollection;

    public Topic() {
    }

    public Topic(Integer id) {
        this.id = id;
    }

    public Topic(Integer id, String title, Date date, boolean locked, boolean pinned) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.locked = locked;
        this.pinned = pinned;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean getLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean getPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
    
    @XmlTransient
    public Collection<Post> getPostCollection() {
        return postCollection;
    }

    public void setPostCollection(Collection<Post> postCollection) {
        this.postCollection = postCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Topic)) {
            return false;
        }
        Topic other = (Topic) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ch.hearc.jeef.entities.Topic[ id=" + id + " title=" + title + " date=" + date + " locked=" + locked + " pinned=" + pinned + " category=" + category.getName() + " ]";
    }
    
}
