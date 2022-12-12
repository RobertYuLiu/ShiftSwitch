package com.jos.dem.springboot.h2.service;

import com.jos.dem.springboot.h2.model.Employee;
import com.jos.dem.springboot.h2.model.SwitchRecord;
import com.jos.dem.springboot.h2.model.TypeOfSwitch;
import com.jos.dem.springboot.h2.repository.SwitchRecordRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PDFService {

    @Autowired
    private FilesStoreService storageService;

    @Autowired
    private SwitchRecordRepository switchRecordRepository;

    public List<SwitchRecord> readPdfFile() throws IOException {
//            InputStream jarPdf = getClass().getClassLoader().getResourceAsStream("data/confirmation1.pdf");
        List<SwitchRecord> switchRecordList = new ArrayList<>();
//        try (PDDocument document = PDDocument.load(new File("data/CONFIRMATION1.pdf"))) {
        try (PDDocument document = PDDocument.load(new ClassPathResource("data/CONFIRMATION1.pdf").getFile())) {//working
//        try (PDDocument document = PDDocument.load(new File("../uploads/fileToProcess.pdf"))) {
//        try (PDDocument document = PDDocument.load(new File("imageAsText.txt"))) {

            document.getClass();
            if (!document.isEncrypted()) {

                PDFTextStripperByArea stripper = new PDFTextStripperByArea();
                stripper.setSortByPosition(true);

                PDFTextStripper tStripper = new PDFTextStripper();

                String pdfFileInText = tStripper.getText(document);

                // split by whitespace
                String lines[] = pdfFileInText.split("\\n");
//                String lines[] = pdfFileInText.split("\\r?\\n");
                System.out.println("start.....");
                int count = 0;
                int count1 = 0;
                int count2 = 0;
                for (String line : lines) {
                    count++;
                    //this is a working filter condition, only good switch can be selected
                    if(line.matches(".*\\d{4,5}.*\\d{4,5}.*\\d{7}.*")) {
                        ///////
                        String patternStr = "[0-9]{4,5}";
                        Pattern pattern = Pattern.compile(patternStr);
                        Matcher matcher = pattern.matcher(line);
                        int badgeId1 = 0;
                        int badgeId2 = 0;
                        if(matcher.find()){
                            int index1 = matcher.start();
                            int index2 = matcher.end();
                            badgeId1 = Integer.parseInt(line.substring(index1,index2));
                            Employee employee1 = constructEmployee(badgeId1, line.substring(0, index1));
                            String newLine = line.substring(index2);
                            matcher = pattern.matcher(newLine);
                            if(matcher.find()){
                                int index3 = matcher.start();
                                int index4 = matcher.end();
                                badgeId2 = Integer.parseInt(newLine.substring(index3,index4));
                                Employee employee2 = constructEmployee(badgeId2, newLine.substring(0, index3));
                                String remainLine = newLine.substring(index4);
                                Pattern pattern3 = Pattern.compile("[0-9]{7}");
                                Matcher matcher3 = pattern3.matcher(remainLine);
                                if (matcher3.find()) {
                                    int index5 = matcher3.start();
                                    int index6 = matcher3.end();
                                    int confirmationId = Integer.parseInt(remainLine.substring(index5,index6));
                                    String[] remainItems = remainLine.substring(0,index5).trim().split(" ");
//                                    System.out.println(badgeId1 + "....." + badgeId2 + "..." + remainItems[0] + "..." + remainItems[1] + "..." + confirmationId);//this will give you index
                                    SwitchRecord switchRecord = new SwitchRecord();
                                    switchRecord.setEmployee1(badgeId1);
                                    switchRecord.setEmployee2(badgeId2);
                                    switchRecord.setConfirmationId(confirmationId);
                                    final TypeOfSwitch typeOfSwitch = TypeOfSwitch.valueOf(convertType(remainItems[0]));
                                    switchRecord.setTypeOfSwitch(typeOfSwitch);
                                    LocalDate date1 = convertDate(remainItems[1]);
                                    switchRecord.setSwitchDate1(convertDate(remainItems[1]));
                                    if(typeOfSwitch.equals(TypeOfSwitch.D4D)){
                                        switchRecord.setSwitchDate2(convertDate(remainItems[2]));
                                    }
                                    switchRecordList.add(switchRecord);
                                }

                            }
//                            System.out.println(line);
                        }
                    }
                }
            }

        }
        storageService.deleteAll();
        storageService.init();
        switchRecordRepository.saveAll(switchRecordList);
        //TODO: update the schedule table ...
        return switchRecordList;
    }

    private LocalDate convertDate(String remainItem) {
        LocalDate localDate = null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MMM-yy").withLocale(Locale.ENGLISH);
            localDate = LocalDate.parse(remainItem, formatter);
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return localDate;
    }

    private String convertType(String remainItem) {
        if(remainItem.contains("-")){
            return remainItem.replace('-', '_');
        }else if(remainItem.contains("/")){
            return remainItem.replace("/", "_");
        }else{
            return remainItem;
        }
    }

    private Employee constructEmployee(int badgeId1, String substring) {
        Employee employee = new Employee();
        employee.setEmployeeId(badgeId1);
        String[] names = substring.split(" ");
        int length = names.length;
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< length-1;i++){
            sb.append(names[i]);
        }
        employee.setFirstname(sb.toString());
        employee.setLastName(names[length-1]);
        return employee;
    }

    private boolean isNumber(String temp) {
        try{
            Integer.parseInt(temp);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    private InputStream getFileAsIOStream(final String fileName) {
        InputStream ioStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(fileName);

        if (ioStream == null) {
            throw new IllegalArgumentException(fileName + " is not found");
        }
        return ioStream;
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
}
