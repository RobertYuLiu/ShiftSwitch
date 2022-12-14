package com.jos.dem.springboot.h2.controller;

import com.jos.dem.springboot.h2.model.FileInfo;
import com.jos.dem.springboot.h2.model.Response;
import com.jos.dem.springboot.h2.service.FilesStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("http://localhost:4200")
public class FileUploadController {

    @Autowired
    private FilesStoreService storageService;

    @GetMapping(value="/test", produces="application/json")
    public String hello() {
        return "hello test....";
    }

    @PostMapping("/upload")
    public ResponseEntity<Response> uploadFile(@RequestParam("file") MultipartFile file) {
        String message = "";
        try {
            System.out.println("starting ...");
            storageService.save(file);
            System.out.println("ending ...");

            message = "File successfully uploaded: " + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.OK).body(new Response(message));
        } catch (Exception e) {
            //System.out.println(e);
            //System.out.println(e);
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(message));
        }
    }

    @GetMapping("/files")
    public ResponseEntity<List<FileInfo>> getListFiles() {
        List<FileInfo> fileInfos = storageService.loadAll().map(path -> {
            String filename = path.getFileName().toString();
            String url = MvcUriComponentsBuilder
                    .fromMethodName(FileUploadController.class, "getFile", path.getFileName().toString()).build().toString();

            return new FileInfo(filename, url);
        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
    }

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = storageService.load(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}
