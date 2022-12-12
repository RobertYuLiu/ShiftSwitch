package com.jos.dem.springboot.h2.service;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.jos.dem.springboot.h2.model.SwitchForm;
import org.hibernate.type.YesNoType;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

import static com.jos.dem.springboot.h2.util.SwitchFormFields.*;

@Service
public class SwitchFormService {


    public static final String YES = "Yes";//must be uppercase Y due to field setting
    public static final String NO = "No";

    public void manipulateSamplePdf(SwitchForm formData) throws DocumentException, IOException {
        PdfReader reader = new PdfReader(SRC);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(DEST));
        AcroFields form = stamper.getAcroFields();//debug this form can know all the field names
        updateFormFields(form, formData);
        stamper.setFormFlattening(true);
        stamper.close();
    }

    private void updateFormFields(AcroFields form, SwitchForm formData) throws DocumentException, IOException {
        Set<String> fields = form.getFields().keySet();
        fields.stream().forEach((temp) -> {
//            System.out.println("form.setField(" + temp);
//            System.out.println(temp);
        });

        form.setField(IS_DEALER, formData.isDealer() ? YES : NO, formData.isDealer() ? YES : NO);
        form.setField(IS_DAY_DAY_SWITCH, formData.isDayDaySwitch() ? YES : NO, formData.isDayDaySwitch() ? YES : NO);
        form.setField(IS_GIVE_AWAY_PICK_UP, formData.isGiveAwayPickUp() ? YES : NO, formData.isGiveAwayPickUp() ? YES : NO);
        form.setField(IS_TIME_SWITCH, formData.isTimeSwitch() ? YES : NO, formData.isTimeSwitch() ? YES : NO);
        form.setField(IS_PIT_SWITCH, formData.isPitSwitch() ? YES : NO, formData.isPitSwitch() ? YES : NO);

        form.setField(FIRST_LAST_NAME_A1, formData.getFirstLastNameA1(), formData.getFirstLastNameA1());
        String badgeA1 = String.valueOf(formData.getBadgeIdA1());
        form.setField(BADGE_ID_A1, badgeA1, badgeA1);

        form.setField(IS_FULL_TIME_A1, formData.isFullTimeA1() ? YES : NO, formData.isFullTimeA1() ? YES : NO);
        form.setField(IS_FULL_TIME_UTIL_A1, formData.isFullTimeUtilA1() ? YES : NO, formData.isFullTimeUtilA1() ? YES : NO);
        form.setField(IS_PART_TIME_A1, formData.isPartTimeA1() ? YES : NO, formData.isPartTimeA1() ? YES : NO);

        form.setField(FIRST_LAST_NAME_A2, formData.getFirstLastNameA2(), formData.getFirstLastNameA2());
        String badgeA2 = String.valueOf(formData.getBadgeIdA2());
        form.setField(BADGE_ID_A2, badgeA2, badgeA2);

        form.setField(IS_FULL_TIME_A2, formData.isFullTimeA2() ? YES : NO, formData.isFullTimeA2() ? YES : NO);
        form.setField(IS_FULL_TIME_UTIL_A2, formData.isFullTimeUtilA2() ? YES : NO, formData.isFullTimeUtilA2() ? YES : NO);
        form.setField(IS_PART_TIME_A2, formData.isPartTimeA2() ? YES : NO, formData.isPartTimeA2() ? YES : NO);

        form.setField(DAY_OF_WEEK_A, formData.getDayOfWeekA(), formData.getDayOfWeekA());
        form.setField(MONTH_DATE_A, formData.getMonthDateA(), formData.getMonthDateA());
        form.setField(SHIFT_START_END_TIME_A, formData.getShiftStartEndTimeA(), formData.getShiftStartEndTimeA());

        form.setField(IS_POKER_ROOM_A, formData.isPokerRoomA() ? YES : NO, formData.isPokerRoomA() ? YES : NO);
        form.setField(IS_6AM_CRABS_A, formData.is6AmCrabsA() ? YES : NO, formData.is6AmCrabsA() ? YES : NO);

        form.setField(FIRST_LAST_NAME_B1, formData.getFirstLastNameB1(), formData.getFirstLastNameB1());
        String badgeB1 = String.valueOf(formData.getBadgeIdB1());
        form.setField(BADGE_ID_B1, badgeB1, badgeB1);

        form.setField(IS_FULL_TIME_B1, formData.isFullTimeB1() ? YES : NO, formData.isFullTimeB1() ? YES : NO);
        form.setField(IS_FULL_TIME_UTIL_B1, formData.isFullTimeUtilB1() ? YES : NO, formData.isFullTimeUtilB1() ? YES : NO);
        form.setField(IS_PART_TIME_B1, formData.isPartTimeB1() ? YES : NO, formData.isPartTimeB1() ? YES : NO);

        form.setField(FIRST_LAST_NAME_B2, formData.getFirstLastNameB2(), formData.getFirstLastNameB2());
        String badge2 = String.valueOf(formData.getBadgeIdB2());
        form.setField(BADGE_ID_B2, badge2, badge2);

        form.setField(IS_FULL_TIME_B2, formData.isFullTimeB2() ? YES : NO, formData.isFullTimeB2() ? YES : NO);
        form.setField(IS_FULL_TIME_UTIL_B2, formData.isFullTimeUtilB2() ? YES : NO, formData.isFullTimeUtilB2() ? YES : NO);
        form.setField(IS_PART_TIME_B2, formData.isPartTimeB2() ? YES : NO, formData.isPartTimeB2() ? YES : NO);

        form.setField(DAY_OF_WEEK_B, formData.getDayOfWeekB(), formData.getDayOfWeekB());
        form.setField(MONTH_DATE_B, formData.getMonthDateB(), formData.getMonthDateB());
        form.setField(SHIFT_START_END_TIME_B, formData.getShiftStartEndTimeB(), formData.getShiftStartEndTimeB());

        form.setField(IS_POKER_ROOM_B, formData.isPokerRoomB() ? YES : NO, formData.isPokerRoomB() ? YES : NO);
        form.setField(IS_6AM_CRABS_B, formData.is6AmCrabsB() ? YES : NO, formData.is6AmCrabsB() ? YES : NO);

        form.setField(PHONE_NUM_A1, formData.getPhoneNumA1(), formData.getPhoneNumA1());
        form.setField(PHONE_NUM_A2, formData.getPhoneNumA2(), formData.getPhoneNumA2());
        form.setField(PHONE_NUM_A3, formData.getPhoneNumA3(), formData.getPhoneNumA3());

        form.setField(PHONE_NUM_B1, formData.getPhoneNumB1(), formData.getPhoneNumB1());
        form.setField(PHONE_NUM_B2, formData.getPhoneNumB2(), formData.getPhoneNumB2());
        form.setField(PHONE_NUM_B3, formData.getPhoneNumB3(), formData.getPhoneNumB3());
    }
}
