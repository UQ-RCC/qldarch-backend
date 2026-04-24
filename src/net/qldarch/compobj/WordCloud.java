package net.qldarch.compobj;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.qldarch.gson.JsonExclude;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@Entity
@Table(name="wordcloud")
@Data
@EqualsAndHashCode(of={"id"})
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class WordCloud {

  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Long id;

  @JsonExclude
  private Long compobj;

  @Column(name="label")
  private String title;

  private String text;

  @JsonCreator
  public WordCloud(@JsonProperty("title") String label, @JsonProperty("text") String txt) {
    title = label;
    text = txt;
  }

}
