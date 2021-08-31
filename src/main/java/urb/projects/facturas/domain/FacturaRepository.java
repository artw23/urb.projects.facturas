package urb.projects.facturas.domain;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacturaRepository extends PagingAndSortingRepository<Factura, UUID> {

  Page<Factura> findByReporteId(UUID reporteId, Pageable pageable);

  List<Factura> findByReporteId(UUID reporteId);
}
