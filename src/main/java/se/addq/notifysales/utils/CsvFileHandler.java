package se.addq.notifysales.utils;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvFileHandler {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    final private ResourceLoader resourceLoader;

    @Autowired
    public CsvFileHandler(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public List<CSVRecord> getListOfCSVRecords(String resourceFilePath, Class<? extends Enum<?>> headerEnumClass) {
        List<CSVRecord> csvRecords = new ArrayList<>();
        Resource resource = resourceLoader.getResource("classpath:" + resourceFilePath);
        try (Reader reader = new InputStreamReader(resource.getInputStream())) {
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                    .withIgnoreHeaderCase()
                    .withHeader(headerEnumClass)
                    .withFirstRecordAsHeader()
                    .withTrim());
            csvRecords = csvParser.getRecords();
        } catch (IOException e) {
            log.error("Failed to read file {}", resourceFilePath, e);
        }
        return csvRecords;
    }


    public byte[] getListOfCSVRecordsAsByteArray(List<String[]> csvRecordList, Class<? extends Enum<?>> headerEnumClass) {
        byte[] bytes = new byte[0];
        if (csvRecordList == null) {
            log.error("Not allowed to set null as input for list");
            return bytes;
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
             CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT
                     .withHeader(headerEnumClass))
        ) {
            for (String[] strings : csvRecordList) {
                printer.printRecord((Object[]) strings);
            }
            printer.flush();
            return out.toByteArray();

        } catch (IOException e) {
            log.error("Failed to get list as byte array", e);
        }
        return bytes;

    }

}



