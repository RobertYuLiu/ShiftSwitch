package com.jos.dem.springboot.h2.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jos.dem.springboot.h2.model.*;
import com.jos.dem.springboot.h2.service.FilesStoreService;
import com.jos.dem.springboot.h2.service.ScheduleService;
import com.jos.dem.springboot.h2.service.UtilityService;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class ScheduleController {

    public static final String SENIORITY_REGEX = "[0][0-9][0-9][0-9][0-9]";
    //    public static final String badgeNumberpatternRegex = "[0-9][0-9][0-9][0-9]";
    public static final String BADGE_BADGE_REGEX = "\\d{4,5}";

    private FilesStoreService filesStoreService;
    private ScheduleService scheduleService;
    private UtilityService utilityService;

    public ScheduleController(FilesStoreService filesStoreService, ScheduleService scheduleService, UtilityService utilityService) {
        this.filesStoreService = filesStoreService;
        this.scheduleService = scheduleService;
        this.utilityService = utilityService;
    }

    @PostMapping("/dealerInfo")
    public String uploadDealerInfo(@RequestBody String dealerInfoString) {
        ObjectMapper om = new ObjectMapper();
        List<Dealer> dealerList = new ArrayList<>();
        try {
            JsonNode result = om.readTree(dealerInfoString);
            JsonNode dealerInfoValue = result.findValue("textArea");
            //cleaning of the raw string
            String processedStr = dealerInfoValue.toString()
                    .replace("\\n", "")
                    .replace("\"", "");
            Pattern seniorityPattern = Pattern.compile(SENIORITY_REGEX);
            Matcher matcherOfDealerInfoStr = seniorityPattern.matcher(processedStr);
            int firstIndex = 0;
            while (matcherOfDealerInfoStr.find()) {
                int start = matcherOfDealerInfoStr.start();
                int end = matcherOfDealerInfoStr.end();
                dealerList.add(generateDealerInstance(processedStr.substring(firstIndex, start),
                        processedStr.substring(start, end)));
                firstIndex = end;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println(dealerList);
        utilityService.saveAllDealers(dealerList);

        return "dealer info received...";
    }

    private Dealer generateDealerInstance(String tempStr, String seniority) {
        Dealer dealer = new Dealer();
        Pattern badgePattern = Pattern.compile(BADGE_BADGE_REGEX);
        Matcher badgeMatcher = badgePattern.matcher(tempStr);
        if (badgeMatcher.find()) {
            int start = badgeMatcher.start();
            int end = badgeMatcher.end();
            String[] firstLastName = tempStr.substring(0, start).split(",");//ROBINSON,DONALD M
            dealer.setLastName(firstLastName[0].trim().replace("-", ""));
            dealer.setFirstName(firstLastName[1].trim().replace("-", ""));
            dealer.setBadgeNumber(tempStr.substring(start, end));
            //cleaning
            String[] otherItems = tempStr.substring(end).replaceAll("-", "").trim().split(" ");
            dealer.setStatus(otherItems[0]);
            if (otherItems.length > 1) {//difference between full timer and part timer
                dealer.setStartTimeCategory(otherItems[1]);
            }
        }
        //https://stackoverflow.com/questions/275711/add-leading-zeroes-to-number-in-java
        dealer.setSeniority(String.format("%05d", Integer.parseInt(seniority)));
        System.out.println("..." + dealer.getBadgeNumber() + "..." + dealer.getFirstName() + "..." + dealer.getLastName() + "..." + dealer.getStatus() + "..." + dealer.getSeniority());
        return dealer;
    }

    @PostMapping("/offArray")
    public String uploadOffString(@RequestBody String[] offArr){
        String startDateStr = offArr[1];
//        for(int i=0; i<offArr.length; i++){
//            System.out.println("offArr[" + i + "] = " + offArr[i]);
//        }
//        System.out.println("uploadSchedule....." + startDateStr);
        StringBuilder sb = new StringBuilder("File ");
        try {
            filesStoreService.init();//create the temporary file folder
            //Step:0 prepare raw string
//            sb.append(filesStoreService.saveUploadedFileIntoTemporaryFolder(files, UploadedFileType.SCHEDULE_ORIGIN));
            String rawString = offArr[0];
//            String rawString = filesStoreService.convertUploadedScheduleTextFromImageToString();
            String[] arrayIncludingDealerAndShift = rawString.split(SENIORITY_REGEX);
            System.out.println(arrayIncludingDealerAndShift);
            int numberOfDealers = arrayIncludingDealerAndShift.length - 1;
            //Step:1 construct schedule
            String[] schedule = getInitialScheduleArray(offArr);
//            String[] schedule = filesStoreService.fileRead_initializeScheduleArrayAndAddOffValue(numberOfDealers);
            scheduleService.updateScheduleWithScannedShifts(arrayIncludingDealerAndShift[numberOfDealers], schedule);
            //Step:2 construct seniority list
            String[] seniority = scheduleService.constructSeniorityArray(rawString, SENIORITY_REGEX, numberOfDealers);
            //Step:3 construct dealer list
            List<Dealer> listOfDealers = new LinkedList<>();
            Pattern badgePattern = Pattern.compile(BADGE_BADGE_REGEX);
            for (int i = 0; i < numberOfDealers; i++) {
                Dealer dealer = new Dealer();
                String tempStr = arrayIncludingDealerAndShift[i];//sample:'ROBINSON,DONALD M 1658 FT 10AM - '
                Matcher badgeMatcher = badgePattern.matcher(tempStr);
                if (badgeMatcher.find()) {
                    int start = badgeMatcher.start();
                    int end = badgeMatcher.end();
                    String[] firstLastName = tempStr.substring(0, start).split(",");//ROBINSON,DONALD M
                    dealer.setLastName(firstLastName[0].trim());
                    dealer.setFirstName(firstLastName[1].trim());
                    dealer.setBadgeNumber(tempStr.substring(start, end));
                    System.out.println("....,,,,..." +tempStr.substring(end) + "....,,,...");
                    String[] otherItems = tempStr.substring(end).replaceAll("-", "").trim().split(" ");
                    dealer.setStatus(otherItems[0]);
                    if(otherItems.length > 1) {
                        dealer.setStartTimeCategory(otherItems[1]);
                    }
                }
                //https://stackoverflow.com/questions/275711/add-leading-zeroes-to-number-in-java
                dealer.setSeniority(String.format("%05d", Integer.parseInt(seniority[i])));
                listOfDealers.add(dealer);
            }
            //Step:4 add schedule to dealer list
//            int totalRowsScanned = schedule.length / 7;//each row contains seven days shift so divided by 7
//            LocalDate tempWeekStartDate = utilityService.convertDateYYYYMMDD(startDateStr);
//            scheduleService.saveExtractedShift(schedule, listOfDealers, totalRowsScanned, tempWeekStartDate);
            scheduleService.saveDealers(listOfDealers);
            filesStoreService.deleteAll();
//            return ResponseEntity.status(HttpStatus.OK).body(listOfDealers);
            return sb.append(" have been successfully uploaded and processed.").toString();
        } catch (Exception e) {
            filesStoreService.deleteAll();
            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(e.toString(), "bad"));
            return "bad";
        }
        /////end
    }

    private String[] getInitialScheduleArray(String[] offArr) {
        String[] scheduleArray = new String[offArr.length - 2];
        int count = 0;
        for (int i = 2; i < offArr.length; i++) {
            scheduleArray[count] = offArr[i];
            count++;
        }
        return scheduleArray;
    }

    @PostMapping(value = "/schedule", produces = "application/json")
    public String uploadSchedule(@RequestParam("files") MultipartFile[] files, @RequestParam("startDate") String startDateStr) {
        System.out.println("uploadSchedule....." + startDateStr);
        StringBuilder sb = new StringBuilder("File ");
        try {
            filesStoreService.init();//create the temporary file folder
            //Step:0 prepare raw string
            sb.append(filesStoreService.saveUploadedFileIntoTemporaryFolder(files, UploadedFileType.SCHEDULE_ORIGIN));
            String rawString = filesStoreService.convertUploadedScheduleTextFromImageToString();
            String[] arrayIncludingDealerAndShift = rawString.split(SENIORITY_REGEX);
            int numberOfDealers = arrayIncludingDealerAndShift.length - 1;
            //Step:1 construct schedule
            String[] schedule = filesStoreService.fileRead_initializeScheduleArrayAndAddOffValue(numberOfDealers);
            scheduleService.updateScheduleWithScannedShifts(arrayIncludingDealerAndShift[numberOfDealers], schedule);
            //Step:2 construct seniority list
            String[] seniority = scheduleService.constructSeniorityArray(rawString, SENIORITY_REGEX, numberOfDealers);
            //Step:3 construct dealer list
            List<Dealer> listOfDealers = new LinkedList<>();
            Pattern badgePattern = Pattern.compile(BADGE_BADGE_REGEX);
            for (int i = 0; i < numberOfDealers; i++) {
                Dealer dealer = new Dealer();
                String tempStr = arrayIncludingDealerAndShift[i];//sample:'ROBINSON,DONALD M 1658 FT 10AM - '
                Matcher badgeMatcher = badgePattern.matcher(tempStr);
                if (badgeMatcher.find()) {
                    int start = badgeMatcher.start();
                    int end = badgeMatcher.end();
                    String[] firstLastName = tempStr.substring(0, start).split(",");//ROBINSON,DONALD M
                    dealer.setLastName(firstLastName[0].trim());
                    dealer.setFirstName(firstLastName[1].trim());
                    dealer.setBadgeNumber(tempStr.substring(start, end));
                    String[] otherItems = tempStr.substring(end).replaceAll("-", "").trim().split(" ");
                    dealer.setStatus(otherItems[0]);
                    dealer.setStartTimeCategory(otherItems[1]);
                }
                //https://stackoverflow.com/questions/275711/add-leading-zeroes-to-number-in-java
                dealer.setSeniority(String.format("%05d", Integer.parseInt(seniority[i])));
                listOfDealers.add(dealer);
            }
            //Step:4 add schedule to dealer list
            int totalRowsScanned = schedule.length / 7;//each row contains seven days shift so divided by 7
            LocalDate tempWeekStartDate = utilityService.convertDateYYYYMMDD(startDateStr);
            scheduleService.saveExtractedShift(schedule, listOfDealers, totalRowsScanned, tempWeekStartDate);
            scheduleService.saveDealers(listOfDealers);
            filesStoreService.deleteAll();
//            return ResponseEntity.status(HttpStatus.OK).body(listOfDealers);
            return sb.append(" have been successfully uploaded and processed.").toString();
        } catch (Exception e) {
            filesStoreService.deleteAll();
            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(e.toString(), "bad"));
            return "bad";
        }
    }

//    @GetMapping(value = "/schedule", produces = "application/json")
//    public FourWeekShift getShiftByBadgeId(@RequestParam("badgeId") String badgeId) {
//        System.out.println("getShiftByBadgeId....." + badgeId);
//        FourWeekShift result = scheduleService.getLast4WeekShiftsByBadgeId(badgeId);
//        return result;
//    }
    @PostMapping(value = "/schedule123", produces = "application/json")
    public FourWeekShift getShiftByBadgeId(@RequestBody Dealer dealer) {
        System.out.println("get Shift By dealer whole object....." + dealer);
        FourWeekShift result = scheduleService.getLast4WeekShiftsByBadgeId(dealer);
        return result;
    }

    @GetMapping("/schedules")
    public FourWeekShift getLast4WeekShiftByBadgeId(@Param("badgeId") String badgeId){
        FourWeekShift result = scheduleService.getLast4WeekShiftsByBadgeId_old(badgeId);
        return result;
    }

    @GetMapping("/weeklySchedules")
    public List<WeekShift> getWeeklyShiftBySundayDate(@RequestParam("sundayDate") String sundayDate){
        System.out.println("inside schedule controller - getWeeklyShiftBySundayDate().....");
        System.out.println("selected sundayDate is: " + sundayDate);
        List<WeekShift> resultList = scheduleService.getShiftsBySundayDate(sundayDate);
        System.out.println("number of shifts found: " + resultList.size());
        return resultList;
    }

    @PostMapping(value = "/weekShift", produces = "application/json")
    public Shift updateShifts(@RequestBody WeekShift shifts) {
        System.out.println("badgeId is: " + shifts.toString());
        List<Shift> shiftList = new ArrayList<>();
        Shift[] shiftsFromUserUpdate = shifts.getShift();
        for (int i = 0; i < 7; i++) {
            String badgeId = shiftsFromUserUpdate[i].getBadgeId();
            LocalDate shiftDate = shiftsFromUserUpdate[i].getShiftDate();
            Shift tempShift = scheduleService.getShiftsByBadgeIdAndShiftDate(badgeId, shiftDate);
            tempShift.setDescription(shiftsFromUserUpdate[i].getDescription());
            tempShift.setShiftCode(shiftsFromUserUpdate[i].getShiftCode());
            shiftList.add(tempShift);
            System.out.println("shift is: " + shiftsFromUserUpdate[i].getDescription());
        }
        this.scheduleService.updateShifts(shiftList);
        return new Shift();
    }

}
