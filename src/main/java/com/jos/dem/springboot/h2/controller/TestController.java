package com.jos.dem.springboot.h2.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.DocumentException;
import com.jos.dem.springboot.h2.model.Dealer;
import com.jos.dem.springboot.h2.model.Person;
import com.jos.dem.springboot.h2.model.SwitchForm;
import com.jos.dem.springboot.h2.model.WeeklySchedule;
import com.jos.dem.springboot.h2.repository.DealerRepository;
import com.jos.dem.springboot.h2.repository.WeeklyScheduleRepository;
import com.jos.dem.springboot.h2.service.FillablePdfService;
import com.jos.dem.springboot.h2.util.SwitchFormFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static com.jos.dem.springboot.h2.util.SwitchFormFields.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class TestController {
	
	private String seniorityNumberpatternRegex = "[0][0-9][0-9][0-9][0-9]";
	private String badgeNumberpatternRegex = "[0-9][0-9][0-9][0-9]";

	@Autowired
	private DealerRepository dealerRepository;

	@Autowired
	private WeeklyScheduleRepository weeklyScheduleRepository;

	@Autowired
	private FillablePdfService fillablePdfService;

	@GetMapping("/fillable")
	public ResponseEntity<?> testFillable(@RequestBody SwitchForm switchForm) throws FileNotFoundException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();

		// Java object to JSON string
		String jsonString = mapper.writeValueAsString(new SwitchForm());
//		System.out.println("switch form json generated: " + jsonString);

		try {
			fillablePdfService.manipulateSamplePdf(switchForm);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		File newFile = new File(DEST);
		InputStreamResource resource = new InputStreamResource(new FileInputStream(newFile));
		System.out.println("sending response file ...");
		return ResponseEntity.status(HttpStatus.OK)
				.contentLength(newFile.length()).contentType(MediaType.APPLICATION_PDF).body(resource);
	}

	@PostMapping("/fillable1")
	public String saveUser(@RequestBody String person) {
		System.out.println(person);
		return "something is tested ...";
	}

	@GetMapping(value="/bbb", produces="application/json")
	public List<Dealer> hello() throws IOException {
		System.out.println("hello.....");
		//Step:0 prepare raw string
		String rawString = fileRead_convertScheduleTextFromImageToString();
		String[] arrayIncludingDealerAndShift = rawString.split(seniorityNumberpatternRegex);
		int numberOfDealers = arrayIncludingDealerAndShift.length - 1;
		//Step:1 construct schedule
		String[] schedule = fileRead_initializeScheduleArrayAndAddOffValue(numberOfDealers);
		updateScheduleWithScannedShifts(arrayIncludingDealerAndShift[numberOfDealers], schedule);

		//Step:2 construct seniority list
		String[] seniorities = constructSenioritiesArray(rawString, seniorityNumberpatternRegex, numberOfDealers);
		//Step:3 construct dealer list
		List<Dealer> listOfDealers = new LinkedList<>();
		Pattern badgePattern = Pattern.compile(badgeNumberpatternRegex);
		for(int i = 0; i< numberOfDealers; i++) {
			Dealer dealer = new Dealer();
			String tempStr = arrayIncludingDealerAndShift[i];
			Matcher badgeMatcher = badgePattern.matcher(tempStr);
			if(badgeMatcher.find()) {
				int start = badgeMatcher.start();
				int end = badgeMatcher.end();
				String[] firstLastName = tempStr.substring(0, start).split(",");
				dealer.setLastname(firstLastName[0].trim());
				dealer.setFirstname(firstLastName[1].trim());
				dealer.setBadgeNumber(Integer.parseInt(tempStr.substring(start, end)));
				String[] otherItems = tempStr.substring(end).replaceAll("-", "").trim().split(" ");
				dealer.setStatus(otherItems[0]);
				dealer.setStartTimeCategory(otherItems[1]);
			}
			dealer.setSeniority(Integer.parseInt(seniorities[i]));
			listOfDealers.add(dealer);
		}
		//Step:4 add schedule to dealer list
		int times = schedule.length / 7;
		String tempWeekStartDate = "221105";//week starting on Nov 5th, 2022
		for (int j = 0; j < times; j++) {
			int dealerBadge = listOfDealers.get(j).getBadgeNumber();
			WeeklySchedule weeklySchedule = new WeeklySchedule(dealerBadge,tempWeekStartDate,
					constructShift(schedule[j]),
					constructShift(schedule[j + 1 * times]),
					constructShift(schedule[j + 2 * times]),
					constructShift(schedule[j + 3 * times]),
					constructShift(schedule[j + 4 * times]),
					constructShift(schedule[j + 5 * times]),
					constructShift(schedule[j + 6 * times])
					);
			weeklyScheduleRepository.save(weeklySchedule);
//			listOfDealers.get(j).setCurrentWeekSchedule(weeklySchedule);
		}
		System.out.println("returning to controller  ...");
		dealerRepository.saveAll(listOfDealers);
		return listOfDealers;
	}

	private String constructShift(String shiftStr) {
//		Shift shift;
		int split = shiftStr.lastIndexOf(" ");
		if (split > 0) {// normal shift
//			shift = new Shift(shiftStr.substring(0, split), shiftStr.substring(split + 1));
			return shiftStr.substring(split + 1);
		} else {// vacation, lieu, pph etc...
//			shift = new Shift(shiftStr, "");
			return shiftStr;
		}
	}

	private String[] constructSenioritiesArray(String rawStringAfterPreprocessing, String seniorityNumberpatternRegex,
			int numberOfDealers) {
		String[] seniorities = new String[numberOfDealers];
		Pattern seniorityPattern = Pattern.compile(seniorityNumberpatternRegex);
		Matcher seniorityMatcher = seniorityPattern.matcher(rawStringAfterPreprocessing);
		int count = 0;
		while (seniorityMatcher.find()) {
			seniorities[count] = rawStringAfterPreprocessing.substring(seniorityMatcher.start(),
					seniorityMatcher.end());
			count++;
		}
		return seniorities;
	}

	private String fileRead_convertScheduleTextFromImageToString() throws IOException {
		StringBuilder sb = new StringBuilder();
		// The file is already text from the image using https://www.prepostseo.com/image-to-text
		String scheduleTextFromImage = "schedule.txt";
		File scheduleFile = new File(scheduleTextFromImage);

		try {
			FileReader scheduleFileReader = new FileReader(scheduleFile);
			BufferedReader scheduleFileBufferedReader = new BufferedReader(scheduleFileReader);
			String contentOfLine;
			while ((contentOfLine = scheduleFileBufferedReader.readLine()) != null) {
				sb.append(contentOfLine);
			}
			scheduleFileBufferedReader.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	private String[] fileRead_initializeScheduleArrayAndAddOffValue(int numberOfDealers) throws IOException {
		String[] totalSchedule = new String[numberOfDealers * 7];
		String offDaysMarkerFile = "off.txt";
		File fileOff = new File(offDaysMarkerFile);
		try {
			FileReader frOff = new FileReader(fileOff);
			BufferedReader bufferedReader = new BufferedReader(frOff);
			String stOff;
			int count = 0;
			while ((stOff = bufferedReader.readLine()) != null) {
				String lineSeparatedByComma = generateLineSeparatedbyComma(stOff);
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

	private String generateLineSeparatedbyComma(String stOff) {
		StringBuilder sb = new StringBuilder();
		String[] middleResult = stOff.split(",");
		for (int i = 0; i < middleResult.length; i++) {
			String temp = middleResult[i];
			if (temp.contains("-")) {
				String sss = generateString(temp);
				sb.append(sss);
			} else {
				sb.append(temp + ",");
			}
		}
		return sb.toString();
	}

	private String generateString(String temp) {
		StringBuilder sb = new StringBuilder();
		String[] numbers = temp.split("-");
		int firstNum = Integer.parseInt(numbers[0].trim());
		int secondNum = Integer.parseInt(numbers[1].trim());
		for (int i = firstNum; i <= secondNum; i++) {
			sb.append(i).append(",");
		}
		return sb.toString();
	}

	private void updateScheduleWithScannedShifts(String shiftString, String[] schedule) {
		
		//Step:1 generate the array of all the shifts, including special shifts such as lieu
		String fromXXToXXRegex = "(\\d{1,2}:\\d{2}[AaPp][Mm][ ]?-[ ]?\\d{1,2}:\\d{2}[AaPp][Mm])";
		Pattern fromToPattern = Pattern.compile(fromXXToXXRegex);
		Matcher shiftFromToMatcher = fromToPattern.matcher(shiftString);
		
		int position = 0;
		int line = 0;
		String[] shiftArray = new String[schedule.length];
		while(shiftFromToMatcher.find()) {
			int start = shiftFromToMatcher.start();
			String substring1 = shiftString.substring(position, start);
			String substring2 = shiftFromToMatcher.group(1);
			//different way of getting the substring2:
//			int end = shiftFromToMatcher.end();
//			String substring2 = shiftString.substring(start, end);
			position = position + substring1.length() + substring2.length();
			///insert into schedule list totalSchedule
			String[] subResult = generateSubResult(substring1, substring2);
			for(int i=0; i< subResult.length;i++) {
				shiftArray[line] = subResult[i];
				line++;
			}
		}
		
		//Step:2 merge off and shift to finalize the schedule
		int count=0;
		for(int i=0; i< shiftArray.length; i++) {
			if(!"off".equalsIgnoreCase(schedule[i])) {
				schedule[i] = shiftArray[count];
				count++;
			}
		}
//		printSchedule(schedule);
	}
	
	private void printSchedule(String[] totalSchedule) {
		int times = totalSchedule.length / 7;
		for (int j = 0; j < times; j++) {
			for (int i = 0; i < 7; i++) {
				System.out.print(totalSchedule[j + i * times] + "  ");
			}
			System.out.println();
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

	//https://stackoverflow.com/questions/55789337/best-practice-to-send-response-in-spring-boot
	////////////////////////////////////////////////////////////////////////////////////////////
//	@GetMapping("/branch/{id}")
//	public ResponseEntity<Branch> getBranch(@PathVariable String id) {
//		Branch branch = branchService.getOne(id);
//		if (branch == null) {
//			throw new RecordNotFoundException("Invalid Branch id : " + id);
//		}
//		return new ResponseEntity<Branch>(branch, HttpStatus.OK);
//	}
//	////////////////////////////////////////////////////////////////////////////////////////////
//
//	public String trimDecimal(String str) {
////		String str = "This  is  geek";
//////		StringTokenizer st = new StringTokenizer(str, " ");
////		StringTokenizer st = new StringTokenizer(str1, ".");
////		for (int i = 0; st.hasMoreTokens(); i++)
////			System.out.println("#" + i + ": " + st.nextToken());
//
//		String dot = ".";
//		StringTokenizer st1 = null;
//		if (str.contains(dot)) {
//			st1 = new StringTokenizer(str, dot);
//			str = st1.nextToken();
//		}
//		return str;
//	}
//
//	public int sss(String input) {
//		int result = Integer.parseInt(trimDecimal(input));
//		return result;
//	}
//
//	public boolean isNumeric(String apr) {
//		final String onlyNumbers = "\\d*";
//		Pattern onlyNumber = Pattern.compile(onlyNumbers);
//		return onlyNumber.matcher(apr).matches();
//	}


}
