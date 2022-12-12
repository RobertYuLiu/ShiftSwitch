package com.jos.dem.springboot.h2.controller;

import com.jos.dem.springboot.h2.model.SwitchRecord;
import com.jos.dem.springboot.h2.service.FileUploadDownloadService;
import com.jos.dem.springboot.h2.service.PDFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.print.Book;
import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class BookController {
    @Autowired
    private Environment env;

    @Autowired
    private FileUploadDownloadService fileUploadDownloadService;

//    private final BookRepository repository;
    @Autowired
    private PDFService pdfService;


//    public BookController(BookRepository repository, PDFService pdfService) {
//        this.repository = repository;
//        this.pdfService = pdfService;
//    }

//    @GetMapping("/aaa")
//    public Iterable<Book> findAll() {
//        System.out.println("get active profiles ..." + env.getActiveProfiles().toString());
//        System.out.println("get default profiles ..." + env.getDefaultProfiles().toString());
//        return repository.findAll();
//    }

    @GetMapping("/read")
    public List<SwitchRecord> readPDF() throws IOException {
        return this.pdfService.readPdfFile();
    }

    @PostMapping(value = "/uploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = fileUploadDownloadService.uploadFile(file);

        return "Process is done!";
    }
}
