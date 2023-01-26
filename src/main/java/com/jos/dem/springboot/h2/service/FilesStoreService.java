package com.jos.dem.springboot.h2.service;

import com.jos.dem.springboot.h2.model.UploadedFileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Service
public class FilesStoreService {

    private final Path root = Paths.get("uploads");
    private static final Logger logger = LoggerFactory.getLogger(FilesStoreService.class);

    private UtilityService utilityService;

    public void sampleOfReadingFileFromResourcesPackage() throws IOException {
        //Note: files must be put inside the properties package as gradle build will only copy that to the jar
        Resource resource = new ClassPathResource("data/off.txt");
        File file = resource.getFile();
        InputStream is = new FileInputStream(file);
        InputStreamReader reader = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(reader);

        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
        br.close();
        reader.close();
        is.close();
    }

    public FilesStoreService(UtilityService utilityService) {
        this.utilityService = utilityService;
    }

    public String testService(){
        logger.info("inside {} ...", logger.getName());
        return "hello from " + logger.getName();
    }

    public void init() {
        try {
            Files.createDirectory(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    public String saveUploadedFileIntoTemporaryFolder(MultipartFile[] files, UploadedFileType fileType) {
        StringBuilder messageSB = new StringBuilder();
        if (fileType.equals(UploadedFileType.SWITCH_CONFIRMATION)) {
            messageSB.append(files[0].getOriginalFilename() + " ");
            try {
                String fileName = files[0].getOriginalFilename();
                if (fileName.toLowerCase().contains("confirmation")) {
                    Files.copy(files[0].getInputStream(), this.root.resolve("confirmation.pdf"));
                } else {
                    System.out.println("Strange file name: " + fileName);
                }
            } catch (Exception e) {
                throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
            }
        } else if (fileType.equals(UploadedFileType.SCHEDULE_ORIGIN)) {
            for (MultipartFile file : files) {
                messageSB.append(file.getOriginalFilename() + " ");
                String fileName = file.getOriginalFilename();
                try {
                    if (fileName.contains("schedule")) {
                        Files.copy(file.getInputStream(), this.root.resolve("schedule.txt"));
                    } else if (fileName.contains("off")) {
                        Files.copy(file.getInputStream(), this.root.resolve("off.txt"));
                    } else {
                        System.out.println("Strange file name: " + fileName);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            messageSB.append(files[0].getOriginalFilename() + " ");
            try {
                String fileName = files[0].getOriginalFilename();
                Files.copy(files[0].getInputStream(), this.root.resolve("test.pdf"));
            } catch (Exception e) {
                throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
            }
            System.out.println("do nothing for now...");
        }
        return messageSB.toString();
    }

    public String convertUploadedScheduleTextFromImageToString() {
        StringBuilder sb = new StringBuilder();
        // The file is already text from the image using https://www.prepostseo.com/image-to-text
        try {
            File scheduleFile = new File("./uploads/schedule.txt");
            FileReader scheduleFileReader = new FileReader(scheduleFile);
            BufferedReader scheduleFileBufferedReader = new BufferedReader(scheduleFileReader);
            String contentOfLine;
            while ((contentOfLine = scheduleFileBufferedReader.readLine()) != null) {
                sb.append(contentOfLine);
            }
            scheduleFileBufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public Resource load(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    public void deleteAll() {
        System.out.println("what is root: " + root.toString());
        FileSystemUtils.deleteRecursively(root.toFile());
    }

    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!");
        }
    }

    public String[] fileRead_initializeScheduleArrayAndAddOffValue(int numberOfDealers) throws IOException {
        String[] totalSchedule = new String[numberOfDealers * 7];
        String offDaysMarkerFile = "uploads/off.txt";
//        File fileOff = new File(System.getProperty("uploads")+ File.separator + "off.txt");
        File fileOff = new File("./uploads/off.txt");
//        File fileOff = new File(offDaysMarkerFile);
        try {
            FileReader frOff = new FileReader(fileOff);
            BufferedReader bufferedReader = new BufferedReader(frOff);
            String stOff;
            int count = 0;
            while ((stOff = bufferedReader.readLine()) != null) {
                String lineSeparatedByComma = utilityService.generateLineSeparatedbyComma(stOff);
                String[] indexOfOff = lineSeparatedByComma.split(",");
                for (int i = 0; i < indexOfOff.length; i++) {
                    int index = Integer.parseInt(indexOfOff[i].trim()) - 1 + count * numberOfDealers;
                    totalSchedule[index] = "off";
                }
                count++;
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return totalSchedule;
    }



}