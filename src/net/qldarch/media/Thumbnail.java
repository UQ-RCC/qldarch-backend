package net.qldarch.media;

import java.io.ByteArrayInputStream;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import lombok.Data;

@Entity
@Table(name="thumbnail")
@Data
public class Thumbnail {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name="media")
  private Media media;

  private int width;

  private int height;

  private String path;

  private String hash;

  @Temporal(TemporalType.TIMESTAMP)
  private Date created;

  private String mimetype;

  private long filesize;

  private byte[] thumbnail;

  private boolean failed;

  private String failmsg;

  public ContentProvider getContentprovider() {
    return () -> new ByteArrayInputStream(thumbnail);
  }

}
