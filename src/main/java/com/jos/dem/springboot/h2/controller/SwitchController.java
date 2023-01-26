package com.jos.dem.springboot.h2.controller;

import com.jos.dem.springboot.h2.model.*;
import com.jos.dem.springboot.h2.service.FilesStoreService;
import com.jos.dem.springboot.h2.service.SwitchRecordService;
import com.jos.dem.springboot.h2.service.UtilityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@CrossOrigin("http://localhost:4200")
public class SwitchController {

    private static final Logger logger = LoggerFactory.getLogger(SwitchController.class);
    private FilesStoreService filesStoreService;
    private SwitchRecordService switchRecordService;
    private UtilityService utilityService;

    public SwitchController(FilesStoreService filesStoreService, SwitchRecordService switchRecordService, UtilityService utilityService) {
        this.filesStoreService = filesStoreService;
        this.switchRecordService = switchRecordService;
        this.utilityService = utilityService;
    }

    @GetMapping(value = "/test", produces = "application/json")
    public String helloControllerAndInjectedServices() {
        logger.info("inside {}", logger.getName());
        this.switchRecordService.test();
        return this.filesStoreService.testService();
    }

    @GetMapping(value = "/switchRecordCount", produces = "application/json")
    public Map<String, Integer> findTop10Switchers(@RequestParam("choice") String choice) {
        System.out.println("The choice is:" + choice);
        Map<String, Integer> dealerSwitchRecords = new HashMap<>();
        switch(choice){
            case "badgeId":
                dealerSwitchRecords = this.switchRecordService.findSwitchRecordsOfTop10Dealers();
                break;
//            case "gapu":
//                dealerSwitchRecords = this.switchRecordService.findSwitchRecordsOfPickup();

        }
        return dealerSwitchRecords;
    }

    @GetMapping(value = "/pickUp", produces = "application/json")
    public List<Object[]> pickup(@RequestParam("choice1") String choice) {
        System.out.println("The pickup choice is:" + choice);
        List<Dealer> list = new ArrayList<>();
        List<Object[]> dealerSwitchRecords = this.switchRecordService.findSwitchRecordsOfPickup();
        return dealerSwitchRecords;
    }

    @PostMapping("/confirmation")
    public String uploadSwitchRecord(@RequestParam("files") MultipartFile[] files) {
        Response response = new Response();
        logger.info("inside uploadSwitchRecord method ...");
        filesStoreService.deleteAll();
        filesStoreService.init();
        List<SwitchRecord> result = new ArrayList<>();
        HttpStatus httpStatus = HttpStatus.OK;
        try {
            filesStoreService.saveUploadedFileIntoTemporaryFolder(files, UploadedFileType.SWITCH_CONFIRMATION);
            result = switchRecordService.extractSwitchRecordsFromSavedPdfFile();
            response.setMessage("File successfully uploaded: " + files[0].getOriginalFilename());
            switchRecordService.saveSwitchRecordAndUpdateSchedule(result);
            //TODO: update switched shift on schedule table

            response.setNote(result.toString());
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            response.setMessage("Could not upload the file: " + files[0].getOriginalFilename() + "!");
            response.setNote(e.toString());
//            filesStoreService.deleteAll();
            httpStatus = HttpStatus.EXPECTATION_FAILED;
//            result = null;
//            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
//            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(result);
        }finally {
            filesStoreService.deleteAll();
            return "Upload successful - " + result.size() + " switch records have been saved.";
        }
    }

    @GetMapping("/switchRecords")
    public List<SwitchRecord> getSwitchRecords(@RequestParam("date1") String date1, @RequestParam("date2") String date2) {
        System.out.println("....date1....." + date1 + "......date2....." + date2+".....");
//        return null;
        return switchRecordService.fetchSwitchRecords(utilityService.convertDate(date1), utilityService.convertDate(date2));
    }

    @GetMapping("/switchRecordsByBadgeId")
    public List<SwitchRecord> getSwitchRecordsById(@Param("badgeId") String badgeId) {
        return switchRecordService.getSwitchRecordsByBadgeId(badgeId);
    }

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = filesStoreService.load(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }
}
