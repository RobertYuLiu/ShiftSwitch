package com.jos.dem.springboot.h2.service;

import com.jos.dem.springboot.h2.model.*;
import com.jos.dem.springboot.h2.repository.SwitchRecordRepository;
import com.jos.dem.springboot.h2.repository.ShiftRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SwitchRecordService {

    public static final String UPLOADS_CONFIRMATION_PDF = "./uploads/confirmation.pdf";
    public static final String TEST_PDF = "./uploads/test.pdf";
    public static final String BADGE_BADGE_CONFIRMATION_REGEX = ".*\\d{4,5}.*\\d{4,5}.*\\d{7}.*";
    public static final String BADGE_BADGE_DENIED_REGEX = ".*\\d{4,5}.*\\d{4,5}.*[Dd][Ee][Nn][Ii][Ee][Dd].*";
    public static final String BADGE_BADGE_REGEX = ".*\\d{4,5}.*\\d{4,5}.*";
    public static final String BADGE_CONFIRMATION_REGEX = ".*\\d{4,5}.*\\d{7}.*";
    public static final String BADGE_ID_REGEX = "[0-9]{4,5}";// 4 or 5 digit
    public static final String CONFIRMATION_ID_REGEX = "[0-9]{7}";// 7 digit
    public static final String LINE_ENDING_REGEX = "\\n";
    public static final String SWITCH_DATE_REGEX = "[0-9]{1,2}-[a-zA-Z]{3}-[0-9]{1,2}";
    private static final String CONFIRMATION = "Confirmation post";

    private Logger logger = LoggerFactory.getLogger(SwitchRecordService.class);

    private FilesStoreService storageService;
    private UtilityService utilityService;
    private SwitchRecordRepository switchRecordRepository;
    private ShiftRepository shiftRepository;

    public SwitchRecordService(FilesStoreService storageService, UtilityService utilityService, SwitchRecordRepository switchRecordRepository, ShiftRepository shiftRepository) {
        this.storageService = storageService;
        this.utilityService = utilityService;
        this.switchRecordRepository = switchRecordRepository;
        this.shiftRepository = shiftRepository;
    }

    public List<SwitchRecord> extractSwitchRecordsFromSavedPdfFile() throws IOException {
        logger.info("entering extractSwitchRecordsFromPdfFile() method ...");
        List<SwitchRecord> switchRecordList = new ArrayList<>();
        try (PDDocument document = PDDocument.load(new File(UPLOADS_CONFIRMATION_PDF))) {
            document.getClass();
            if (!document.isEncrypted()) {
                PDFTextStripperByArea stripper = new PDFTextStripperByArea();///?? remove?
                stripper.setSortByPosition(true);//??? remove?
                PDFTextStripper tStripper = new PDFTextStripper();
                String pdfFileInText = tStripper.getText(document);
                String lines[] = pdfFileInText.split(LINE_ENDING_REGEX);
                for (int i = 0; i < lines.length; i++) {
                    // good switch record - why? because most of the cases will be like this
                    String line = lines[i];
                    SwitchRecord switchRecord = new SwitchRecord();
                    switchRecord.setSwitchRecordFrom(CONFIRMATION);
                    if (line.matches(BADGE_BADGE_CONFIRMATION_REGEX)) {
                        int endOf2ndBadgeId = extractBadgeId1AndBadgeId2FromLine(line, switchRecord);
                        Matcher confirmationIdMatcher = Pattern.compile(CONFIRMATION_ID_REGEX).matcher(line);
                        boolean confirmationIdFind = confirmationIdMatcher.find(); // step 3: find the confirmation number
                        String confirmationId = line.substring(confirmationIdMatcher.start(), confirmationIdMatcher.end());
                        List<SwitchRecord> recordsSavedAlready = this.switchRecordRepository.findByConfirmationId(confirmationId);
                        if (recordsSavedAlready == null || recordsSavedAlready.size() == 0) {
                            switchRecord.setConfirmationId(confirmationId);
                            int firstDateStart = extractDatesFromLine(line, switchRecord);
                            switchRecord.setTypeOfSwitch(line.substring(endOf2ndBadgeId, firstDateStart));
                            //apply switch on shift table
                            logger.info(applySwitchOnShiftTable(switchRecord));
                            switchRecordList.add(switchRecord);
                        } else {
//                            logger.info("same confirmation id found. Record saved already...");
                        }
                    } else if (line.matches(BADGE_BADGE_DENIED_REGEX)) {// switch record missing one badge number
                        int endOf2ndBadgeId = extractBadgeId1AndBadgeId2FromLine(line, switchRecord);
                        List<SwitchRecord> recordsSavedAlready = this.switchRecordRepository
                                .findByConfirmationIdAndEmployee1AndEmployee2("Denied", switchRecord.getEmployee1(), switchRecord.getEmployee2());
                        if (recordsSavedAlready == null || recordsSavedAlready.size() == 0) {
                            switchRecord.setConfirmationId("Denied");
                            switchRecord.setReasonOfDeny(getDeniedCaseNote(lines, i));
                            int firstDateStart = extractDatesFromLine(line, switchRecord);
                            switchRecord.setTypeOfSwitch(line.substring(endOf2ndBadgeId, firstDateStart));
                            switchRecordList.add(switchRecord);
                        } else {
//                            logger.info("record of same denied found. Record saved already...");
                        }
                    } else if (line.matches(BADGE_CONFIRMATION_REGEX)) {// switch record missing one badge number
//                        SwitchRecord switchRecord = new SwitchRecord();
//                        switchRecordList.add(switchRecord);
                        System.out.println("....badge id missing....." + line);
                    } else {
                    }
                }
            }
        }
        storageService.deleteAll();
        storageService.init();
        logger.info("leaving extractSwitchRecordsFromPdfFile() method ...");
        return switchRecordList;
    }

    private String applySwitchOnShiftTable(SwitchRecord switchRecord) {
        String employee1BadgeId = switchRecord.getEmployee1();
        LocalDate switchDate1 = switchRecord.getSwitchDate1();
        List<Shift> shift1List = this.shiftRepository.findByBadgeIdAndShiftDateWithQuery(employee1BadgeId, switchDate1);
        String employee2BadgeId = switchRecord.getEmployee2();
        LocalDate switchDate2 = switchRecord.getSwitchDate2();
//        if(switchRecord.getTypeOfSwitch().trim().toUpperCase(Locale.ROOT).contains("X-SWITCH")){
        if(switchDate1 != null && switchDate2 == null){//looks like this condition is good enough
            switchDate2 = switchDate1;
        }
        List<Shift> shift2List = this.shiftRepository.findByBadgeIdAndShiftDateWithQuery(employee2BadgeId, switchDate2);
        boolean condition1 = shift1List == null || shift1List.size() != 1;
        boolean condition2 = shift2List == null || shift2List.size() != 1;
        if (condition1 || condition2) {
            logger.error("shift either not exist or multiple, which is not correct...");
        } else {
            Shift shift1 = shift1List.get(0);
            Shift shift2 = shift2List.get(0);
            //swap badge id
            String tempBadgeId = shift1.getBadgeId();
            shift1.setBadgeId(shift2.getBadgeId());
            shift2.setBadgeId(tempBadgeId);
            //swap shift date
            LocalDate tempDate = shift1.getShiftDate();
            shift1.setShiftDate(shift2.getShiftDate());
            shift2.setShiftDate(tempDate);
            this.shiftRepository.save(shift1);
            this.shiftRepository.save(shift2);
        }
        return "message of apply switch record to table...";
    }

    private String getDeniedCaseNote(String[] lines, int i) {
        int count = 1;
        StringBuilder sb = new StringBuilder();
        while (lines[i + count].length() < 30) {
            sb.append(lines[i + count] + " ");
            count++;
        }
        return sb.toString();
    }

    private int extractBadgeId1AndBadgeId2FromLine(String line, SwitchRecord switchRecord) {
        Matcher badgeIdMatcher = Pattern.compile(BADGE_ID_REGEX).matcher(line);
        boolean firstBadgeIdFind = badgeIdMatcher.find();//find the 1st badge id
        switchRecord.setEmployee1(line.substring(badgeIdMatcher.start(), badgeIdMatcher.end()));
        boolean secondBadgeIdFind = badgeIdMatcher.find();//find the 2nd badge id
        int endOf2ndBadgeId = badgeIdMatcher.end();
        switchRecord.setEmployee2(line.substring(badgeIdMatcher.start(), endOf2ndBadgeId));
        return endOf2ndBadgeId;
    }

    private int extractDatesFromLine(String line, SwitchRecord switchRecord){
        Matcher switchDateMatcher = Pattern.compile(SWITCH_DATE_REGEX).matcher(line);
        boolean firstDateFind = switchDateMatcher.find();
        int firstDateStart = switchDateMatcher.start();
        switchRecord.setSwitchDate1(utilityService.convertDate(line.substring(firstDateStart, switchDateMatcher.end())));
        if (switchDateMatcher.find()) {
            switchRecord.setSwitchDate2(utilityService.convertDate(line.substring(switchDateMatcher.start(), switchDateMatcher.end())));
        } else {
            switchRecord.setSwitchDate2(null);
        }
        return firstDateStart;
    }


    public int saveSwitchRecordAndUpdateSchedule(List<SwitchRecord> records){
        logger.info("entering saveSwitchRecordAndUpdateSchedule ...");
        this.switchRecordRepository.saveAll(records);
        records.stream().forEach(record->{
            String dealer1 = record.getEmployee1();
            String dealer2 = record.getEmployee2();

            LocalDate date1 = record.getSwitchDate1();
            LocalDate startDate1 = date1.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
            String day1 = date1.getDayOfWeek().toString();

            LocalDate date2 = record.getSwitchDate2();
            LocalDate startDate2 = date2.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
            String day2 = date2.getDayOfWeek().toString();

//            WeeklySchedule weeklySchedule1 = getWeeklySchedule(dealer1, startDate1);
//            WeeklySchedule weeklySchedule2 = getWeeklySchedule(dealer2, startDate2);
//
//            if(switchShift(weeklySchedule1, day1,weeklySchedule2,day2)){
//                this.shiftRepository.save(weeklySchedule1);
//                this.shiftRepository.save(weeklySchedule2);
//            }

        });
        return 1;
    }

//    private WeeklySchedule getWeeklySchedule(String dealer, LocalDate startDate) {
//        List<WeeklySchedule> scheduleList = shiftRepository.findRecentShiftsByBadgeId(dealer);
//        if (scheduleList != null && scheduleList.size() > 0) {
//            return scheduleList.get(0);
//        }
//        return null;
//    }

    private boolean switchShift(WeeklySchedule weeklySchedule1, String day1, WeeklySchedule weeklySchedule2, String day2) {
        if (weeklySchedule1 == null || weeklySchedule2 == null) {
            return false;
        }
        String shift1 = getShift(weeklySchedule1, day1);
        String shift2 = getShift(weeklySchedule2, day2);
        updateShift(weeklySchedule1, day1, shift2);
        updateShift(weeklySchedule2, day2, shift1);
        return true;
    }

    private String getShift(WeeklySchedule weeklySchedule, String day) {
        switch (day) {
            case "SUNDAY":
                return weeklySchedule.getSunday();
            case "MONDAY":
                return weeklySchedule.getMonday();
            case "TUESDAY":
                return weeklySchedule.getTuesday();
            case "WEDNESDAY":
                return weeklySchedule.getWednesday();
            case "THURSDAY":
                return weeklySchedule.getThursday();
            case "FRIDAY":
                return weeklySchedule.getFriday();
            case "SATURDAY":
                return weeklySchedule.getSaturday();
            default:
                return null;
        }
    }

    private void updateShift(WeeklySchedule weeklySchedule, String day, String newValue) {
        switch (day) {
            case "SUNDAY":
                weeklySchedule.setSunday(newValue);
            case "MONDAY":
                weeklySchedule.setMonday(newValue);
            case "TUESDAY":
                weeklySchedule.setTuesday(newValue);
            case "WEDNESDAY":
                weeklySchedule.setWednesday(newValue);
            case "THURSDAY":
                weeklySchedule.setThursday(newValue);
            case "FRIDAY":
                weeklySchedule.setFriday(newValue);
            case "SATURDAY":
                weeklySchedule.setSaturday(newValue);
            default:
                System.out.println("error: no day of week matched when doing shift update!");
        }
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

    public List<SwitchRecord> fetchSwitchRecords(LocalDate startDate, LocalDate endDate) {
//        return switchRecordRepository.findByDate(startDate);
        return switchRecordRepository.findByDate(startDate, endDate);
    }

    public List<SwitchRecord> getSwitchRecordsByBadgeId2(String badgeId) {
        List<SwitchRecord> list = new ArrayList<>();
        Optional<SwitchRecord> result = switchRecordRepository.findById(Integer.parseInt(badgeId));
        result.ifPresent(switchRecord -> {
            list.add(switchRecord);
        });
        return list;
    }

    public List<SwitchRecord> getSwitchRecordsByBadgeId(String badgeId) {
        return switchRecordRepository.getSwitchRecords(Integer.parseInt(badgeId));
    }

    public void test() {
        //test 1: works
//        DateTimeFormatter DATEFORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        LocalDate tempWeekStartDate = LocalDate.parse("2022-03-13", DATEFORMATTER);
//        List<WeeklySchedule> result = weeklyScheduleRepository.findByBadgeIdAndStartDate("1584", tempWeekStartDate);
//        result.stream().forEach(temp->{
//            System.out.println("....xccc....." + temp);
//        });

        //test 2:
        DateTimeFormatter DATEFORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-d");
        LocalDate tempWeekStartDate1 = LocalDate.parse("2022-12-6", DATEFORMATTER);
        LocalDate tempWeekStartDate2 = LocalDate.parse("2022-12-11", DATEFORMATTER);
        LocalDate tempWeekStartDate3 = LocalDate.parse("2022-12-02", DATEFORMATTER);
        LocalDate tempWeekStartDate4 = LocalDate.parse("2022-12-24", DATEFORMATTER);

        LocalDate result1 = tempWeekStartDate1.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate result2 = tempWeekStartDate2.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate result3 = tempWeekStartDate3.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate result4 = tempWeekStartDate4.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        System.out.println(tempWeekStartDate1 + "----->" + result1);
        System.out.println(tempWeekStartDate2 + "----->" + result2);
        System.out.println(tempWeekStartDate3 + "----->" + result3);
        System.out.println(tempWeekStartDate4 + "----->" + result4);
        ///output:
//        2022-12-06----->2022-12-04
//        2022-12-11----->2022-12-11/////important!
//        2022-12-02----->2022-11-27
//        2022-12-24----->2022-12-18

        String day1 = tempWeekStartDate1.getDayOfWeek().toString();
        String day2 = tempWeekStartDate2.getDayOfWeek().toString();
        String day3 = tempWeekStartDate3.getDayOfWeek().toString();
        String day4 = tempWeekStartDate4.getDayOfWeek().toString();
        System.out.println(day1);
        System.out.println(day2);
        System.out.println(day3);
        System.out.println(day4);
        //output:
//        TUESDAY
//        SUNDAY
//        FRIDAY
//        SATURDAY
    }

    ///////////////////////////////
    public List<String> extractTestPdfFile() throws IOException {
        logger.info("entering extractTestPdfFile() method ...");
        List<String> resultList = new ArrayList<>();
        try (PDDocument document = PDDocument.load(new File(TEST_PDF))) {
            document.getClass();
            if (!document.isEncrypted()) {
                PDFTextStripperByArea stripper = new PDFTextStripperByArea();///?? remove?
                stripper.setSortByPosition(true);//??? remove?
                PDFTextStripper tStripper = new PDFTextStripper();
                String pdfFileInText = tStripper.getText(document);
                String lines[] = pdfFileInText.split(LINE_ENDING_REGEX);
                Arrays.stream(lines).forEach(temp->{
                    resultList.add(temp);
                });
            }
        }
        storageService.deleteAll();
        storageService.init();
        logger.info("leaving extractTestPdfFile() method ...");
        return resultList;
    }

    public List<Object[]> findSwitchRecordsOfPickup() {
        List<Object[]> queryResult = this.switchRecordRepository.findAllPickup();
        System.out.println(">>>>>" + queryResult.get(0)[0] + "<<<<<<<");
        System.out.println(">>>>>" + queryResult.get(0)[1] + "<<<<<<<");
        return queryResult;
    }

    public Map<String, Integer> findSwitchRecordsOfTop10Dealers() {
        Map<String, Integer> resultMap = new HashMap<>();
        List<Object[]> queryResult1 = this.switchRecordRepository.findSwitchRecordCountByDealer1();
        List<Object[]> queryResult2 = this.switchRecordRepository.findSwitchRecordCountByDealer2();
        queryResult1.stream().forEach(record -> {
            BigInteger bi = (BigInteger) record[1];
            resultMap.put((String) record[0], bi.intValue());
        });
        queryResult2.stream().forEach(record -> {
            String key = (String) record[0];
            BigInteger bi = (BigInteger) record[1];

            if(resultMap.get(key) == null || resultMap.get(key) == 0){
                resultMap.put((String) record[0], bi.intValue());
            }else{
                int sum = resultMap.get(key) + bi.intValue();
                resultMap.put(key, sum);
            }
        });

        return resultMap;
    }
}
