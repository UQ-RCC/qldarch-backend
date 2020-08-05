package net.qldarch.text;

import static net.qldarch.util.UpdateUtils.hasChanged;

import java.sql.Date;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.qldarch.archobj.ArchObj;
import net.qldarch.util.DateUtil;
import net.qldarch.util.ObjUtils;
import net.qldarch.util.UpdateUtils;
import net.qldarch.guice.Guice;
import net.qldarch.relationship.ArticleRelationship;
import net.qldarch.db.Db;
import net.qldarch.db.Rsc;
import net.qldarch.db.Sql;
import net.qldarch.util.M;

@Entity
@Table(name = "text")
@Data
@EqualsAndHashCode(callSuper=true)
public class Article extends ArchObj {

  private static final String PERIODICAL = "periodical";
  private static final String VOLUME = "volume";
  private static final String ISSUE = "issue";
  private static final String PUBLISHED = "published";
  private static final String PAGES = "pages";
  private static final String AUTHORS = "authors";

  private String periodical;

  private String volume;

  private String issue;
  
  private Date published;

  private String pages;

  private String authors;

  @Transient
  private List<ArticleRelationship> annotations;

  @Override
  public Map<String, Object> asMap() {
    Map<String, Object> m = super.asMap();
    m.put(PERIODICAL, periodical);
    m.put(VOLUME, volume);
    m.put(ISSUE, issue);
    m.put(PUBLISHED, published);
    m.put(PAGES, pages);
    m.put(AUTHORS, authors);
    return m;
  }

  @Override
  public boolean updateFrom(Map<String, Object> m) {
    boolean changed = super.updateFrom(m);
    if(UpdateUtils.hasChanged(m, PERIODICAL, periodical)) {
      changed = true;
      periodical = ObjUtils.asString(m.get(PERIODICAL));
    }
    if(UpdateUtils.hasChanged(m, VOLUME, volume)) {
      changed = true;
      volume = ObjUtils.asString(m.get(VOLUME));
    }
    if(UpdateUtils.hasChanged(m, ISSUE, issue)) {
      changed = true;
      issue = ObjUtils.asString(m.get(ISSUE));
    }
    if(hasChanged(m, PUBLISHED, o-> DateUtil.toSqlDate(
        ObjUtils.asDate(o, DateUtil.YYYY_MM_DD)), published)) {
      changed = true;
      published = DateUtil.toSqlDate(ObjUtils.asDate(m.get(PUBLISHED), "yyyy-MM-dd"));
    }
    if(UpdateUtils.hasChanged(m, PAGES, pages)) {
      changed = true;
      pages = ObjUtils.asString(m.get(PAGES));
    }
    if(UpdateUtils.hasChanged(m, AUTHORS, authors)) {
      changed = true;
      authors = ObjUtils.asString(m.get(AUTHORS));
    }
    return changed;
  }

  @Override
  public void copyFrom(Map<String, Object> m) {
    super.copyFrom(m);
    this.periodical = ObjUtils.asString(m.get(PERIODICAL));
    this.volume = ObjUtils.asString(m.get(VOLUME));
    this.issue = ObjUtils.asString(m.get(ISSUE));
    this.published = DateUtil.toSqlDate(ObjUtils.asDate(m.get(PUBLISHED), DateUtil.YYYY_MM_DD));
    this.pages = ObjUtils.asString(m.get(PAGES));
    this.authors = ObjUtils.asString(m.get(AUTHORS));
  }

  private ArticleRelationship createAR(Map<String, Object> m) {
    final ArticleRelationship r = new ArticleRelationship();
    r.setId(M.getLongNotNull(m, "relationshipId"));
    r.setRelationship(M.getStringNotNull(m, "relationship"));
    r.setNote(M.getString(m, "note"));
    r.setFrom(M.getInt(m, "fromyear"));
    r.setUntil(M.getInt(m, "untilyear"));
    r.setSubject(M.getLongNotNull(m, "subject"));
    r.setSubjectlabel(M.getStringNotNull(m, "subjectlabel"));
    r.setSubjecttype(M.getStringNotNull(m, "subjecttype"));
    r.setSubjectarchitect(M.getBoolean(m, "subjectarchitect"));
    r.setObject(M.getLongNotNull(m, "object"));
    r.setObjectlabel(M.getStringNotNull(m, "objectlabel"));
    r.setObjecttype(M.getStringNotNull(m, "objecttype"));
    r.setObjectarchitect(M.getBoolean(m, "objectarchitect"));
    r.setArticle(M.getLongNotNull(m, "article"));
    r.setPage(M.getString(m, "page"));
    r.setCreated(M.getTimestampNotNull(m, "created"));
    return r;
  }


  @Override
  protected void setup() {
    // Create the article annotation
    final Db db = Guice.injector().getInstance(Db.class);
    annotations = new ArrayList<>();
    try {
      db.executeQuery(new Sql("net/qldarch/relationship/ArticleRelationship.sql").prepare(),
          M.of("article", this.getId()), Rsc::fetchAll)
      .stream().map(this::createAR).forEach(annotations::add);
    } catch(Exception e) {
      throw new RuntimeException("failed to retrieve annotations for article with id "+this.getId(), e);
    }

  }

}
