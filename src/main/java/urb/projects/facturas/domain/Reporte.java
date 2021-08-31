package urb.projects.facturas.domain;

    import java.time.LocalDate;
    import java.util.UUID;
    import javax.persistence.Column;
    import javax.persistence.Entity;
    import javax.persistence.Lob;
    import lombok.Data;

@Data
@Entity
public class Reporte extends BaseEntity {

  @Column
  private ReportStatus status;

  @Column(name = "fecha_de_pago")
  private LocalDate fechaDePago;

  @Column(name = "in_file_id")
  private UUID inFileId;

  @Column(name = "out_file_id")
  private UUID outFileId;

}