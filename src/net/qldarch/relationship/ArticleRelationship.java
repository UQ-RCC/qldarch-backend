package net.qldarch.relationship;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name="articlerelationship")
@Data
@EqualsAndHashCode(callSuper=true)
public class ArticleRelationship extends Relationship implements Comparable<ArticleRelationship> {

  private Long article;

  private String page;

  @Transient
  private String relationship;

  @Transient
  private String subjectlabel;

  @Transient
  private String subjecttype;
  
  @Transient
  private boolean subjectarchitect;
  
  @Transient
  private String objectlabel;
  
  @Transient
  private String objecttype;
  
  @Transient
  private boolean objectarchitect;
    
  @Override
  public int compareTo(ArticleRelationship r) {
    if((this.getCreated() != null) && (r.getCreated() != null)) {
      return this.getCreated().compareTo(r.getCreated());
    } else if((this.getCreated() == null) && (r.getCreated() == null)) {
      return 0;
    } else if((this.getCreated() == null)) {
      return -1;
    } else {
      return 1;
    }
  }
  
}
