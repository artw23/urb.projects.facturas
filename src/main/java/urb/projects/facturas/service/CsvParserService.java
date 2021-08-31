package urb.projects.facturas.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface CsvParserService {

  <T> List<T> parseCsv(InputStream file, Class<T> clazz) throws IOException;

  <T> List<T> parseCsv(String filename, Class<T> clazz) throws IOException;

    <T> void writeCsv(List<T> objects, String filename, Class<T> clazz) throws IOException;

  <T> String listToCsvString(List<T> objects, Class<T> clazz) throws IOException;
}