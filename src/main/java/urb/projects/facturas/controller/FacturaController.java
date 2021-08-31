package urb.projects.facturas.controller;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import urb.projects.facturas.domain.Factura;
import urb.projects.facturas.service.FacturaService;

@RestController
@RequestMapping("/factura")
public class FacturaController {

  private FacturaService facturaService;

  private FacturaController(FacturaService facturaService){
    this.facturaService = facturaService;
  }

  @GetMapping("/{reporteId}")
  public Page<Factura> getFacturas(@PathVariable UUID reporteId, Pageable pageable){
    return facturaService.getFacturasByReporeId(reporteId, pageable);
  }

}