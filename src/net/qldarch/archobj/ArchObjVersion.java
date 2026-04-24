package net.qldarch.archobj;

import java.sql.Timestamp;
import java.util.Map;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name="archobjversion")
@Data
@EqualsAndHashCode(of={"id"})
public class ArchObjVersion {

  @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Long id;

  private Long oid;

  private Long owner;

  private Timestamp created;

  private String comment;

  private String document;

  private Long parent;

  public Map<String, Object> getDocumentAsMap() {
    return new Gson().fromJson(document, new TypeToken<Map<String, Object>>(){}.getType());
  }

}
