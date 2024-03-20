package net.qldarch.interview;

import static net.qldarch.util.UpdateUtils.hasChanged;

import javax.inject.Inject;

import java.sql.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.qldarch.archobj.ArchObj;
import net.qldarch.db.Column;
import net.qldarch.person.Person;
import net.qldarch.util.DateUtil;
import net.qldarch.util.ObjUtils;
import net.qldarch.relationship.TranscriptRelationshipSetup;
import net.qldarch.guice.Guice;


@Entity
@Table(name="interview")
@Data
@EqualsAndHashCode(callSuper=true, exclude={"interviewee", "interviewer", "transcript"})
public class Interview extends ArchObj {

  private static final String LOCATION = "location";
  private static final String INTERVIEWDATE = "interviewdate";
  private static final String INTERVIEWEE = "interviewee";
  private static final String INTERVIEWER = "interviewer";

  private String location;
  
  @Column(name="interviewdate")
  private Date interviewdate;
  

  @ManyToMany
  @JoinTable(
      name="interviewee",
      joinColumns=@JoinColumn(name="interview"),
      inverseJoinColumns=@JoinColumn(name="interviewee"))
  private Set<Person> interviewee;

  @ManyToMany
  @JoinTable(
      name="interviewer",
      joinColumns=@JoinColumn(name="interview"),
      inverseJoinColumns=@JoinColumn(name="interviewer"))
  private Set<Person> interviewer;

  @OneToMany(mappedBy="interview")
  @OrderBy("time, id")
  private SortedSet<Utterance> transcript;

  @Override
  protected void setup() {
    // Create the transcript annotation
    final TranscriptRelationshipSetup transcriptSetup = Guice.injector().getInstance(TranscriptRelationshipSetup.class);
    transcriptSetup.setup(this);
  }

  @Override
  public Map<String, Object> asMap() {
    Map<String, Object> m = super.asMap();
    if(StringUtils.isNotBlank(location)) {
      m.put(LOCATION, location);
    }
    if(interviewdate != null) {
      m.put(INTERVIEWDATE, interviewdate);
    }
    return m;
  }

  @Override
  public boolean updateFrom(Map<String, Object> m) {
    boolean changed = super.updateFrom(m);
    if(hasChanged(m, LOCATION, location)) {
      changed = true;
      location = ObjUtils.asString(m.get(LOCATION));
    }
    /* if(hasChanged(m, INTERVIEWDATE, interviewdate)) {
      changed = true;
      interviewdate = ObjUtils.asString(m.get(INTERVIEWDATE));
    } */
    if(hasChanged(m, INTERVIEWDATE, o-> DateUtil.toSqlDate(
        ObjUtils.asDate(o, DateUtil.YYYY_MM_DD)), interviewdate)) {
      changed = true;
      interviewdate = DateUtil.toSqlDate(ObjUtils.asDate(m.get(INTERVIEWDATE), "yyyy-MM-dd"));
    }
    if(hasChanged(m, INTERVIEWEE, interviewee)) {
      changed = true;
      Set<Long> intvwees = ObjUtils.asLongSet(m.get(INTERVIEWEE));
      interviewee = new HashSet<>();
      if(intvwees != null) {
        for(Long e : intvwees) {
          Person p = new Person();
          p.setId(e);
          interviewee.add(p);
        }
      }
    }
    if(hasChanged(m, INTERVIEWER, interviewer)) {
      changed = true;
      Set<Long> intvwers = ObjUtils.asLongSet(m.get(INTERVIEWER));
      interviewer = new HashSet<>();
      if(intvwers != null) {
        for(Long r : intvwers) {
          Person p = new Person();
          p.setId(r);
          interviewer.add(p);
        }
      }
    }
    return changed;
  }

  @Override
  public void copyFrom(Map<String, Object> m) {
    super.copyFrom(m);
    location = ObjUtils.asString(m.get(LOCATION));
    //interviewdate = ObjUtils.asString(m.get(INTERVIEWDATE));
    interviewdate = DateUtil.toSqlDate(ObjUtils.asDate(m.get(INTERVIEWDATE), DateUtil.YYYY_MM_DD));
    Set<Long> intvwees = ObjUtils.asLongSet(m.get(INTERVIEWEE));
    Set<Long> intvwers = ObjUtils.asLongSet(m.get(INTERVIEWER));
    if(intvwees != null && interviewee == null) {
      interviewee = new HashSet<>();
    }
    if(intvwers != null && interviewer == null) {
      interviewer = new HashSet<>();
    }
    if(intvwees != null) {
      for(Long e : intvwees) {
        Person p = new Person();
        p.setId(e);
        interviewee.add(p);
      }
    }
    if(intvwers != null) {
      for(Long r : intvwers) {
        Person p = new Person();
        p.setId(r);
        interviewer.add(p);
      }
    }
  }

}
