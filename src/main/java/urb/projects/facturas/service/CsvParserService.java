package urb.projects.facturas.service;

import java.util.List;

public interface CsvParserService {
    
    <T> List<T> parseCsv(String filename, Class<T> clazz);

}