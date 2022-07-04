package urb.projects.facturas.domain;

import java.util.UUID;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReporteRepository extends PagingAndSortingRepository<Report, UUID> {

}