package com.jos.dem.springboot.h2.service;

import com.jos.dem.springboot.h2.model.Dealer;
import com.jos.dem.springboot.h2.model.FourWeekShift;
import com.jos.dem.springboot.h2.model.Shift;
import com.jos.dem.springboot.h2.model.WeekShift;
import com.jos.dem.springboot.h2.repository.DealerRepository;
import com.jos.dem.springboot.h2.repository.ShiftRepository;
import com.jos.dem.springboot.h2.util.FourWeekStartEndDate;
import com.jos.dem.springboot.h2.util.MyDateUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class ScheduleService {

    private final DealerRepository dealerRepository;
    private final ShiftRepository shiftRepository;
    private final MyDateUtil myDateUtil;
    private final UtilityService utilityService;

    public ScheduleService(DealerRepository dealerRepository, ShiftRepository shiftRepository, MyDateUtil myDateUtil, UtilityService utilityService) {
        this.dealerRepository = dealerRepository;
        this.shiftRepository = shiftRepository;
        this.myDateUtil = myDateUtil;
        this.utilityService = utilityService;
    }

    public void updateScheduleWithScannedShifts(String shiftString, String[] schedule) {

        //Step:1 generate the array of all the shifts, including special shifts such as lieu
        String fromXXToXXRegex = "(\\d{1,2}:\\d{2}[AaPp][Mm][ ]?-[ ]?\\d{1,2}:\\d{2}[AaPp][Mm])";
        Pattern fromToPattern = Pattern.compile(fromXXToXXRegex);
        Matcher shiftFromToMatcher = fromToPattern.matcher(shiftString);

        int position = 0;
        int line = 0;
        String[] shiftArray = new String[schedule.length];
        while (shiftFromToMatcher.find()) {
            int start = shiftFromToMatcher.start();
            //sample of below: D2 P7 TP ;or:LOA(7.50)D2 P7 REL ;or:LOA(7.50)Lieu(7.50)D2 P6 CR
            String substring1 = shiftString.substring(position, start);
            String substring2 = shiftFromToMatcher.group(1);//sample of this string:10:00AM- 6:00PM
            //different way of getting the substring2:
//			int end = shiftFromToMatcher.end();
//			String substring2 = shiftString.substring(start, end);
            position = position + substring1.length() + substring2.length();
            ///insert into schedule list totalSchedule
            String[] subResult = generateSubResult(substring1, substring2);
            for (int i = 0; i < subResult.length; i++) {
                shiftArray[line] = subResult[i];//sample of this string:D2 P6 CR 10:00AM-6:00PM
                line++;
            }
        }

        //Step:2 merge off and shift to finalize the schedule
        int count = 0;
        for (int i = 0; i < shiftArray.length; i++) {
            if (!"off".equalsIgnoreCase(schedule[i])) {
                schedule[i] = shiftArray[count];
                count++;
            }
        }
    }

    private String[] generateSubResult(String substring1, String substring2) {
        String result = divideSpecialShift(substring1);
        return (result + " " + substring2.trim().replaceAll(" ", "")).split(",");
    }

    private String divideSpecialShift(String substring) {
        String loa = "LOA(7.50)";
        String vacation = "Vacation(7.50)";
        String pph = "PPH(7.50)";
        String lieu = "Lieu(7.50)";
        String line = substring.trim();
        if (line.contains(loa)) {
            return loa + "," + divideSpecialShift(line.substring(loa.length()));
        } else {
            if (line.contains(vacation)) {
                return vacation + "," + divideSpecialShift(line.substring(vacation.length()));
            } else {
                if (line.contains(pph)) {
                    return pph + "," + divideSpecialShift(line.substring(pph.length()));
                } else {
                    if (line.contains(lieu)) {
                        return lieu + "," + divideSpecialShift(line.substring(lieu.length()));
                    } else {
                        return line;
                    }
                }
            }
        }
    }

    public String[] constructSeniorityArray(String rawStringAfterPreprocessing, String seniorityNumberPatternRegex,
                                            int numberOfDealers) {
        String[] seniorityArray = new String[numberOfDealers];
        Pattern seniorityPattern = Pattern.compile(seniorityNumberPatternRegex);
        Matcher seniorityMatcher = seniorityPattern.matcher(rawStringAfterPreprocessing);
        System.out.println("....,,,,...." + rawStringAfterPreprocessing + ".....,,,..");
        int count = 0;
        while (seniorityMatcher.find()) {
            seniorityArray[count] = rawStringAfterPreprocessing.substring(seniorityMatcher.start(),
                    seniorityMatcher.end());
            count++;
        }
        return seniorityArray;
    }

    public void saveExtractedShift(String[] schedule, List<Dealer> listOfDealers, int times, LocalDate weekStartDate) {
        List<Shift> shiftList = new ArrayList<>();
        for (int i = 0; i < times; i++) {
            String dealerBadge = listOfDealers.get(i).getBadgeNumber();
            //able to determine if dealer is poker room here???
            for (int j = 0; j < 7; j++) {
                Shift shift;
                String aaa = schedule[i + j * times];
                String[] content = schedule[i + j * times].split(" ");
                if (content.length > 1) {
                    shift = new Shift(dealerBadge, weekStartDate.plusDays(j), content[0], content[1], content[2], content[3]);
                } else if (content.length == 1) {
                    shift = new Shift(dealerBadge, weekStartDate.plusDays(j), null, null, null, content[0]);
                } else {
                    shift = null;
                }
                System.out.println(shift.toString());
                shiftList.add(shift);
            }
        }
        shiftRepository.saveAll(shiftList);
    }

    public List<Shift> getShiftsByBadgeId(String badgeId) {
        badgeId = "6725";
        return shiftRepository.findRecentShiftsByBadgeId(badgeId);
//        return shiftRepository.findRecentShiftsByBadgeId(badgeId);
    }

    public List<WeekShift> getShiftsBySundayDate(String sundayDate) {
        LocalDate nextSunday = myDateUtil.getDayPlus(sundayDate, 7);
        List<Shift> shiftList = shiftRepository.findRecentShiftsBySundayDate(sundayDate, myDateUtil.convertLocalDateToString(nextSunday));
        Map<String, List<Shift>> map = shiftList
                .stream()
                .collect(
                        Collectors.groupingBy(Shift::getBadgeId)
                );
        List<Dealer> dealerList = this.dealerRepository.findAll();
        List<String> badgeIdList = dealerList.stream().map(dealer -> dealer.getBadgeNumber()).collect(Collectors.toList());
        List<WeekShift> resultList = new ArrayList<>();
        for(Map.Entry entry:map.entrySet()){
            WeekShift weekShift = new WeekShift();
            weekShift.setBadgeId((String) entry.getKey());
            weekShift.setStartSundayStr(sundayDate);
            Shift[] tempShiftArray = constructArray(sundayDate, entry);
            weekShift.setShift(tempShiftArray);
            resultList.add(weekShift);
        }
        List<String> existingDealers = resultList.stream().map(weekShift -> weekShift.getBadgeId()).collect(Collectors.toList());
        boolean unprocessedDealerExist = badgeIdList.removeAll(existingDealers);
        if(unprocessedDealerExist){
            for(String badgeId: badgeIdList){
                WeekShift weekShift = new WeekShift();
                weekShift.setBadgeId(badgeId);
                weekShift.setStartSundayStr(sundayDate);
                LocalDate lastSunday = myDateUtil.convertStringToDate(sundayDate).minusDays(7);
                List<Shift> shifts = this.shiftRepository.findByBadgeIdAndShiftDateBetweenWithQuery(badgeId, myDateUtil.convertLocalDateToString(lastSunday), sundayDate);
                Shift[] tempShiftArray = constructArrayFromList(shifts);
                weekShift.setShift(tempShiftArray);
                resultList.add(weekShift);
            }
        }
        return resultList;
    }

    private Shift[] constructArrayFromList(List<Shift> shiftList) {
        Shift[] shifts = new Shift[7];
        if (shiftList == null || shiftList.size() == 0) {
            return shifts;
        }
        for (int i = 0; i < 7; i++) {
            if(shiftList.size() <= i){
                break;
            }
            shifts[i] = shiftList.get(i);
        }
        return shifts;
    }

    private Shift[] constructArray(String sundayDate, Map.Entry entry) {
        Shift[] shifts = new Shift[7];
        LocalDate startingSunday = myDateUtil.convertStringToDate(sundayDate);
        List<Shift> shiftList = (List<Shift>) entry.getValue();
        String shiftCode = "";
        for (Shift shift : shiftList) {
            long daysBetween = DAYS.between(startingSunday, shift.getShiftDate());
            shifts[(int) daysBetween] = shift;
            shiftCode = shift.getShiftCode();
        }
        String defaultDescription = getDefaultDescription(shiftCode);
        for (int i = 0; i < 7; i++) {
            if (shifts[i] == null) {
                Shift temp = new Shift();
                temp.setBadgeId((String) entry.getKey());
                temp.setShiftDate(myDateUtil.getDayPlus(sundayDate, i));
                temp.setDescription(defaultDescription);
                shifts[i] = temp;
            }
        }
        return shifts;
    }

    private String getDefaultDescription(String shiftCode) {
        return utilityService.getShiftStartEndTimeByCode(shiftCode);
    }

    public void saveDealers(List<Dealer> listOfDealers) {
        List<Dealer> existingDealers = dealerRepository.findAll();
        List<Dealer> duplicateDealerList = new ArrayList<>();
        for (Dealer dealer : listOfDealers) {
            for (Dealer exist : existingDealers) {
                if (dealer.getBadgeNumber().equals(exist.getBadgeNumber())) {
                    duplicateDealerList.add(dealer);
                }
            }
        }
        listOfDealers.removeAll(duplicateDealerList);
        dealerRepository.saveAll(listOfDealers);
    }

    public List<Shift> getLast4WeekShiftsOfAll() {
        List<Shift> resultList = new ArrayList<>();
//        if(){

        List<Dealer> dealerList = dealerRepository.findAll();
        for(Dealer dealer:dealerList){
            Shift shift = new Shift();
            shift.setShiftDate(LocalDate.of(2022,12,24));
            shift.setBadgeId(dealer.getBadgeNumber());
            String startTimeCategory = dealer.getStartTimeCategory();
            shift.setShiftCode(startTimeCategory);
            if(startTimeCategory.equalsIgnoreCase("10AM")){
                shift.setDescription("10AM-6PM");
            }
            resultList.add(shift);
        }
        shiftRepository.saveAll(resultList);
//        }else {
        /*
//        Shift shift = new Shift("1584", LocalDate.of(2022,12,24),"aaa", "sss", "ddd", "qqq");
//        shiftRepository.save(shift);
//        Shift shift1 = new Shift("1584", LocalDate.of(2022,12,25),"bbb", "sss", "ddd", "qqq");
//        shiftRepository.save(shift1);
//        Shift shift2 = new Shift("1584", LocalDate.of(2022,12,26),"ccc", "sss", "ddd", "qqq");
//        shiftRepository.save(shift2);
//        Shift shift3 = new Shift("1584", LocalDate.of(2022,12,31),"ddd", "sss", "ddd", "qqq");
//        shiftRepository.save(shift3);
//        Shift shift4 = new Shift("1584", LocalDate.of(2023,1,1),"eee", "sss", "ddd", "qqq");
//        shiftRepository.save(shift4);
//        LocalDate[] startEndDates = generateScheduleStartEndDatesFor4Weeks();
//        List<Shift> shiftsOfWeekZero =
            LocalDate startDate = LocalDate.of(2022, 12, 25);
            LocalDate endDate = LocalDate.of(2023, 12, 31);
//        resultList = shiftRepository.findByShiftDateLessThan(endDate);
//        resultList = shiftRepository.findByShiftDateBetween(startDate, endDate);
            resultList = shiftRepository.findLast4WeekShifts(startDate, endDate);
            */
//        }
        return resultList;
    }

    public FourWeekShift getLast4WeekShiftsByBadgeId_old(String badgeId) {
        FourWeekShift fourWeekShift = generateFourWeekShiftWithBaseDates_old(badgeId);

        FourWeekStartEndDate startEndDates = myDateUtil.generateScheduleStartEndDatesFor4Weeks();

//        Dealer dealer = dealerRepository.findByBadgeNumber(badgeId).get(0);
//        List<Shift> fourWeekShifts = shiftRepository.findLast4WeekShifts(startEndDates.getWeekOneSunday(), startEndDates.getWeekFourSaturday());
//        Shift shift = new Shift();
//        shift.setShiftDate(LocalDate.of(2022, 12, 24));
//        shift.setBadgeId(dealer.getBadgeNumber());
//        String startTimeCategory = dealer.getStartTimeCategory();
//        shift.setShiftCode(startTimeCategory);
//        if (startTimeCategory.equalsIgnoreCase("10AM")) {
//            shift.setDescription("10AM-6PM");
//        }
        return fourWeekShift;
    }
    public FourWeekShift getLast4WeekShiftsByBadgeId(Dealer dealer) {
        FourWeekShift fourWeekShift = generateFourWeekShiftWithBaseDates(dealer);

        FourWeekStartEndDate startEndDates = myDateUtil.generateScheduleStartEndDatesFor4Weeks();

        return fourWeekShift;
    }

    private FourWeekShift generateFourWeekShiftWithBaseDates_old(String badgeId) {
        FourWeekShift fourWeekShift = new FourWeekShift();
        fourWeekShift.setBadgeId(badgeId);
        LocalDate currentWeekSunday = this.myDateUtil.getSundayOfCurrentWeek();
        LocalDate startingSunday = currentWeekSunday.minusWeeks(1);
        Shift[] weekOneShifts = new Shift[7];
        Shift[] weekTwoShifts = new Shift[7];
        Shift[] weekThreeShifts = new Shift[7];
        Shift[] weekFourShifts = new Shift[7];
        for (int i = 0; i < 7; i++) {
            weekOneShifts[i] = getShiftFromDBOrCreateNew_old(badgeId, startingSunday.plusDays(i));
            weekTwoShifts[i] = getShiftFromDBOrCreateNew_old(badgeId, startingSunday.plusDays(i + 7));
            weekThreeShifts[i] = getShiftFromDBOrCreateNew_old(badgeId, startingSunday.plusDays(i + 14));
            weekFourShifts[i] = getShiftFromDBOrCreateNew_old(badgeId, startingSunday.plusDays(i + 21));
        }
        fourWeekShift.setWeekOne(weekOneShifts);
        fourWeekShift.setWeekTwo(weekTwoShifts);
        fourWeekShift.setWeekThree(weekThreeShifts);
        fourWeekShift.setWeekFour(weekFourShifts);
        return fourWeekShift;
    }
    private FourWeekShift generateFourWeekShiftWithBaseDates(Dealer dealer) {
        FourWeekShift fourWeekShift = new FourWeekShift();
        fourWeekShift.setBadgeId(dealer.getBadgeNumber());
        LocalDate currentWeekSunday = this.myDateUtil.getSundayOfCurrentWeek();
        LocalDate startingSunday = currentWeekSunday.minusWeeks(1);
        Shift[] weekOneShifts = new Shift[7];
        Shift[] weekTwoShifts = new Shift[7];
        Shift[] weekThreeShifts = new Shift[7];
        Shift[] weekFourShifts = new Shift[7];
        for (int i = 0; i < 7; i++) {
            weekOneShifts[i] = getShiftFromDBOrCreateNew(dealer, startingSunday.plusDays(i));
            weekTwoShifts[i] = getShiftFromDBOrCreateNew(dealer, startingSunday.plusDays(i + 7));
            weekThreeShifts[i] = getShiftFromDBOrCreateNew(dealer, startingSunday.plusDays(i + 14));
            weekFourShifts[i] = getShiftFromDBOrCreateNew(dealer, startingSunday.plusDays(i + 21));
        }
        fourWeekShift.setWeekOne(weekOneShifts);
        fourWeekShift.setWeekTwo(weekTwoShifts);
        fourWeekShift.setWeekThree(weekThreeShifts);
        fourWeekShift.setWeekFour(weekFourShifts);
        return fourWeekShift;
    }

    private Shift getShiftFromDBOrCreateNew_old(String badgeId, LocalDate shiftDate) {
        List<Shift> shifts = shiftRepository.findByBadgeIdAndShiftDateWithQuery(badgeId, shiftDate);
        Shift shift = null;
        if (shifts != null && shifts.size() > 0) {
            shift = shifts.get(0);
        }
        if (shifts == null || shifts.size() == 0) {
            shift = new Shift();
            shift.setShiftDate(shiftDate);
            shift.setBadgeId(badgeId);
            shift.setShiftCode("A4");
            shift.setDescription("10AM - 6PM");
            shift.setPit("p7");
            shift.setGame("BJ");
        }
        return shift;
    }

    private Shift getShiftFromDBOrCreateNew(Dealer dealer, LocalDate shiftDate) {
        List<Shift> shifts = shiftRepository.findByBadgeIdAndShiftDateWithQuery(dealer.getBadgeNumber(), shiftDate);
        Shift shift = null;
        if (shifts != null && shifts.size() > 0) {
            shift = shifts.get(0);
        }
        if (shifts == null || shifts.size() == 0) {
            shift = new Shift();
            shift.setShiftDate(shiftDate);
            shift.setBadgeId(dealer.getBadgeNumber());
            shift.setDescription(getDescription(dealer));
            shift.setPit("p7");
            shift.setGame("BJ");
        }
        return shift;
    }

    private String getDescription(Dealer dealer) {
        return utilityService.getShiftStartEndTimeByCode(dealer.getStartTimeCategory());
    }

    public Shift getShiftsByBadgeIdAndShiftDate(String badgeId, LocalDate shiftDate) {
        List<Shift> shifts = this.shiftRepository.findByBadgeIdAndShiftDateWithQuery(badgeId, shiftDate);
        if (shifts == null || shifts.size() == 0) {
            Shift tempShift = new Shift();
            tempShift.setBadgeId(badgeId);
            tempShift.setShiftDate(shiftDate);
            return tempShift;
        } else {
            return shifts.get(0);
        }
    }

    public boolean updateShifts(List<Shift> shiftList) {
        this.shiftRepository.saveAll(shiftList);
        return true;
    }
}
