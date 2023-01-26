package com.jos.dem.springboot.h2.controller;

import com.jos.dem.springboot.h2.model.*;
import com.jos.dem.springboot.h2.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.awt.print.Book;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class TestController {
    @Autowired
    private Environment env;

    @Autowired
    private FileUploadDownloadService fileUploadDownloadService;

    @Autowired
    private FilesStoreService filesStoreService;

    @Autowired
    private ScheduleService scheduleService;

//    private final BookRepository repository;
    @Autowired
    private SwitchRecordService switchRecordService;

    @Autowired
    private TestUtilityService testUtilityService;


    //todo: call other rest service from here ...

//    public BookController(BookRepository repository, PDFService pdfService) {
//        this.repository = repository;
//        this.pdfService = pdfService;
//    }

    @GetMapping("/hello")
    public String helloWorld() throws IOException {
        this.filesStoreService.sampleOfReadingFileFromResourcesPackage();
        return "hello world!...";
    }

    private void printFileContent(InputStream is) throws IOException {
        try (InputStreamReader isr = new InputStreamReader(is);
             BufferedReader br = new BufferedReader(isr);) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            is.close();
        }
    }

    //not working yet, try to have both sides use xml format... 2023-1-17
    @GetMapping(value="/pureTest", produces = "application/xml")
    public ResponseEntity puretestMethod(){
        System.out.println("Hello hello!!!");
        Shift shift = new Shift();
        shift.setBadgeId("2333");
        shift.setShiftDate(LocalDate.of(2020, 1, 8));
        shift.setPit("P7");
        return new ResponseEntity(shift, HttpStatus.OK);
    }

    @PostMapping("/testpost")
    public Shift testPostAngular(@RequestBody Object shift1){
        //@RequestBody Object shift1 will translate object from angular as map...interesting
        System.out.println("Hello test post!!! --- " + shift1.toString());
        Shift shift = new Shift();
        shift.setBadgeId("1584");
        shift.setShiftDate(LocalDate.of(2020, 1, 8));
        shift.setPit("P7");
        return shift;
    }
    @PostMapping("/testpost2")
    public Shift testPostAngular2(@RequestBody Person person){
        //@RequestBody both front end and here should use same class structure, in other words, both are Person
        System.out.println("Hello test post!!! --- " + person.getEmail());//output:Hello test post!!! --- abc@xyz.com
        Shift shift = new Shift();
        shift.setBadgeId("2222");
        shift.setShiftDate(LocalDate.of(2020, 1, 8));
        shift.setPit("P7");
        return shift;
    }

    @RequestMapping("/user")
    @ResponseBody
    private String getUser() {
        String uri = "https://jsonplaceholder.typicode.com/users/1";
        RestTemplate restTemplate = new RestTemplate();

        User user = restTemplate.getForObject(uri, User.class);
        System.out.println("User: " + user);

        return "User detail page.";
    }

    @GetMapping("/read")
    public List<SwitchRecord> readPDF() throws IOException {
        return this.switchRecordService.extractSwitchRecordsFromSavedPdfFile();
    }

    @GetMapping("/testOfTest")
    public ResponseEntity testOfTest(@RequestParam("name") String name) {
        String replyFromService = testUtilityService.getTestMessage(name);
        return new ResponseEntity(replyFromService, HttpStatus.OK);
    }

    @PostMapping(value = "/uploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = fileUploadDownloadService.uploadFile(file);

        return "Process is done!";
    }

//    @PostMapping("/confirmation")
//    public String uploadSwitchRecord(@RequestParam("files") MultipartFile[] files) {
    @PostMapping(value = "/testReadPdfFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadTestPdfFile(@RequestParam("file") MultipartFile[] file) throws IOException {
        filesStoreService.deleteAll();
        filesStoreService.init();
        filesStoreService.saveUploadedFileIntoTemporaryFolder(file, UploadedFileType.TEST);
        List<String> result = switchRecordService.extractTestPdfFile();
        result.stream().forEach(temp -> System.out.println(temp));
        filesStoreService.deleteAll();
        return "Process is done! " + result.size() + " lines read.";
    }

    @PostMapping("/upload")
    public String testFilesUpload(@RequestParam("file") MultipartFile[] files) {
        try {
            System.out.println("entering upload ....");
            filesStoreService.init();
            filesStoreService.saveUploadedFileIntoTemporaryFolder(files, UploadedFileType.SWITCH_CONFIRMATION);
            filesStoreService.deleteAll();
            return "finished upload...";
        } catch (Exception e) {
            e.printStackTrace();
            return "bad";
        }
    }
}
