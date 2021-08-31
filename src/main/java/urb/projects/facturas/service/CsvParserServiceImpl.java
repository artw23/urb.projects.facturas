package urb.projects.facturas.service;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.io.InputStream;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CsvParserServiceImpl implements CsvParserService {

  @Override
  public <T> List<T> parseCsv(InputStream file, Class<T> clazz) throws IOException {
    CsvMapper csvMapper = new CsvMapper();
    csvMapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);

    CsvSchema schema = csvMapper.typedSchemaFor(clazz).withHeader();

    MappingIterator<T> csvObjectIter = new CsvMapper()
        .readerFor(clazz)
        .with(schema)
        .readValues(file);

    List<T> objectList = csvObjectIter.readAll();
    return objectList;
  }


  @Override
  public <T> List<T> parseCsv(String csvFilePath, Class<T> clazz) throws IOException {

    return parseCsv(new FileInputStream(new File(csvFilePath)), clazz);
  }

  @Override
  public <T> void writeCsv(List<T> objects, String filename, Class<T> clazz) throws IOException {
    CsvMapper csvMapper = new CsvMapper();
    csvMapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
    CsvSchema schema = csvMapper.typedSchemaFor(clazz).withHeader();
    ObjectWriter writer = csvMapper.writerFor(clazz).with(schema);
    writer.writeValues(new File(filename)).writeAll(objects);
  }

  @Override
  public <T> String listToCsvString(List<T> objects, Class<T> clazz) throws IOException {
    CsvMapper csvMapper = new CsvMapper();
    csvMapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
    CsvSchema schema = csvMapper.typedSchemaFor(clazz).withHeader();
    ObjectWriter writer = csvMapper.writerFor(clazz).with(schema);
    return writer.writeValueAsString(objects);
  }

  private void verifyDirectory(String path){
    File directory = new File(path);
    if (! directory.exists()){
      directory.mkdirs();
    }
  }



}
