package net.qldarch.interview;

import java.util.SortedSet;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.qldarch.gson.JsonExclude;
import net.qldarch.person.Person;
import net.qldarch.relationship.TranscriptRelationship;

@Entity
@Table(name="utterance")
@Data
@EqualsAndHashCode(of={"id"})
public class Utterance implements Comparable<Utterance> {

  @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "interview")
  @JsonExclude
  private Interview interview;

  @ManyToOne
  @JoinColumn(name = "speaker")
  private Person speaker;

  private int time;

  private String transcript;

  @Transient
  private SortedSet<TranscriptRelationship> relationships;

  @Override
  public int compareTo(Utterance u1) {
    return this.time - u1.time;
  }

}
