package urb.projects.facturas.domain;

import javax.persistence.*;

import lombok.Data;
import org.hibernate.annotations.Type;

@Data
@Table(name = "file")
@Entity
public class File extends BaseEntity {

  @Column
  private String nombre;

  @Lob
  @Type(type = "org.hibernate.type.ImageType")
  private byte[] content;

}
