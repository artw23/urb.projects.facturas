package urb.projects.facturas.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import lombok.Data;

@Data
@Entity
public class File extends BaseEntity {

  @Column
  private String nombre;

  @Column
  private String extension;

  @Lob
  private byte[] content;

}
