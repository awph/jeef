/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.hearc.jeef.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Alexandre
 */
@Entity
@Table(name = "post")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Post.findAll", query = "SELECT p FROM Post p"),
    @NamedQuery(name = "Post.findById", query = "SELECT p FROM Post p WHERE p.id = :id"),
    @NamedQuery(name = "Post.findByTopic", query = "SELECT p FROM Post p WHERE p.topic = :topic"),
    @NamedQuery(name = "Post.findByCreatedDate", query = "SELECT p FROM Post p WHERE p.createdDate = :createdDate"),
    @NamedQuery(name = "Post.findByEditedDate", query = "SELECT p FROM Post p WHERE p.editedDate = :editedDate"),
    @NamedQuery(name = "Post.countByTopic", query = "SELECT COUNT(p) FROM Post p WHERE p.topic = :topic")})
public class Post implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "content")
    private String content;
    @Basic(optional = false)
    @NotNull
    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "edited_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date editedDate;
    @JoinColumn(name = "topic_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Topic topic;
    @JoinColumn(name = "last_editor_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private User lastEditor;
    @JoinColumn(name = "creator_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private User creator;

    public Post() {
    }

    public Post(Integer id) {
        this.id = id;
    }

    public Post(Integer id, String content, Date createdDate, Date editedDate) {
        this.id = id;
        this.content = content;
        this.createdDate = createdDate;
        this.editedDate = editedDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getEditedDate() {
        return editedDate;
    }

    public void setEditedDate(Date editedDate) {
        this.editedDate = editedDate;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public User getLastEditor() {
        return lastEditor;
    }

    public void setLastEditor(User lastEditor) {
        this.lastEditor = lastEditor;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }
    
    public Boolean getEdited() {
        return !createdDate.equals(editedDate);
    }
    
    public Boolean getCanDelete(Topic topic) {
        return !this.equals(topic.getPostCollection().iterator().next());
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
        if (!(object instanceof Post)) {
            return false;
        }
        Post other = (Post) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ch.hearc.jeef.entities.Post[ id=" + id + " content=" + content + " createdDate=" + createdDate + " editedDate=" + editedDate + " topic=" + topic + " creator=" + creator + " ]";
    }
    
}
