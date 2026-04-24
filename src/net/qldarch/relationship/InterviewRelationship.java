package net.qldarch.relationship;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name="interviewrelationship")
@Data
@EqualsAndHashCode(callSuper=true)
public class InterviewRelationship extends Relationship {

  private Long interview;

  private Long utterance;

}
