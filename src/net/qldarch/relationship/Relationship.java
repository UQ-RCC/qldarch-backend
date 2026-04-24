package net.qldarch.relationship;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name="relationship")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@EqualsAndHashCode(of={"id"})
public class Relationship {

  @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Long id;

  private Long subject;

  private Long object;

  @Enumerated(EnumType.STRING)
  private RelationshipType type;

  @Enumerated(EnumType.STRING)
  private RelationshipSource source;

  private String note;

  @Column(name="fromyear")
  private Integer from;

  @Column(name="untilyear")
  private Integer until;

  private Timestamp created;

  private Long owner;
}
