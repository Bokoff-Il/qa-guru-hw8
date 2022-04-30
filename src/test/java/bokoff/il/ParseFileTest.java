package bokoff.il;

import static org.hamcrest.MatcherAssert.assertThat;
import com.codeborne.pdftest.PDF;
import com.codeborne.pdftest.matchers.ContainsExactText;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParseFileTest {

  ClassLoader cl = ParseFileTest.class.getClassLoader();

  @Test
  void zipTest() throws Exception {
    ZipFile zf = new ZipFile(new File("src/test/resources/zip_file.zip"));
    ZipInputStream is = new ZipInputStream(cl.getResourceAsStream("zip_file.zip"));
    ZipEntry entry;
    while ((entry = is.getNextEntry()) != null) {

      if (entry.getName().equals("csv_file.csv")) {
        try (InputStream inputStream = zf.getInputStream(entry)) {
          CSVReader csvReader = new CSVReader(
              new InputStreamReader(inputStream, StandardCharsets.UTF_8));

          List<String[]> content = csvReader.readAll();

          org.assertj.core.api.Assertions.assertThat(content).contains(
              new String[]{"Name", "Surname", "Age"},
              new String[]{"Petr", "Ivanov", "12"},
              new String[]{"Vasya", "Pupkin", "98"});
        }
      }

      if (entry.getName().equals("pdf_file.pdf")) {
        try (InputStream inputStream = zf.getInputStream(entry)) {
          PDF pdf = new PDF(inputStream);

          Assertions.assertEquals(166, pdf.numberOfPages);
          assertThat(pdf,new ContainsExactText("123"));
        }
      }

      if (entry.getName().equals("xls_file.xls")) {
        try (InputStream inputStream = zf.getInputStream(entry)) {
          XLS xls = new XLS(inputStream);

          String value = xls.excel.getSheetAt(0).getRow(0).getCell(1).getStringCellValue();
          org.assertj.core.api.Assertions.assertThat(value).contains("A");
        }
      }
    }
  }
}
