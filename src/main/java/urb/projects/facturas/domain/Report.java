package urb.projects.facturas.domain;

import java.time.LocalDate;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Report extends BaseEntity {

  @Column
  private ReportStatus status;

  @Column
  private InvoiceType invoiceType;

  @Column
  private LocalDate paymentDate;

  @Column
  private UUID inputFileId;

  @Column
  private UUID outputFileId;

}