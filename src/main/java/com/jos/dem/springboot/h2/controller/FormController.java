package com.jos.dem.springboot.h2.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.DocumentException;
import com.jos.dem.springboot.h2.model.SwitchForm;
import com.jos.dem.springboot.h2.model.SwitchRecord;
import com.jos.dem.springboot.h2.service.FilesStoreService;
import com.jos.dem.springboot.h2.service.SwitchFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.jos.dem.springboot.h2.util.SwitchFormFields.DEST;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class FormController {

    private final String seniorityNumberpatternRegex = "[0][0-9][0-9][0-9][0-9]";
    private final String badgeNumberpatternRegex = "[0-9][0-9][0-9][0-9]";

    private SwitchFormService switchFormService;
    private FilesStoreService filesStoreService;

    public FormController(SwitchFormService switchFormService, FilesStoreService filesStoreService) {
        this.switchFormService = switchFormService;
        this.filesStoreService = filesStoreService;
    }

    @PostMapping("/switchForm")
    public ResponseEntity<?> generateSwitchForm(@RequestBody SwitchForm switchForm) throws FileNotFoundException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        // Java object to JSON string
        String jsonString = mapper.writeValueAsString(new SwitchForm());
        System.out.println("switch form received: " + switchForm.getShiftStartEndTimeB());

        SwitchRecord newSwitchRecordForTracking = null;
        try {
            newSwitchRecordForTracking = switchFormService.generateShiftSwitchFormWithInputData(switchForm);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }

        if (newSwitchRecordForTracking != null) {
            switchFormService.saveNewRecordForTracking(newSwitchRecordForTracking);
            File newFile = new File(DEST);
            InputStreamResource resource = new InputStreamResource(new FileInputStream(newFile));
            System.out.println("sending response file ...");
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentLength(newFile.length())
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("file can not be processed.");
        }
    }

    @PostMapping("/fillable1")
    public String saveUser(@RequestBody String person) {
        System.out.println(person);
        return "something is tested ...";
    }
}
