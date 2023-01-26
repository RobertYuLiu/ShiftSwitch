package com.jos.dem.springboot.h2.repository;

import com.jos.dem.springboot.h2.model.Shift;
import com.jos.dem.springboot.h2.model.WeekShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Integer> {
    @Query(nativeQuery = true, value = "SELECT * FROM shift where badge_id =:badgeId ")
    List<Shift> findRecentShiftsByBadgeId(@Param("badgeId") String badgeId);//works

    List<Shift> findByShiftDateBetween(LocalDate to, LocalDate from);




    @Query(nativeQuery = true, value = "SELECT * FROM shift where shift_date >= :startDate and shift_date <= :endDate")
    List<Shift> findLast4WeekShifts(@Param("startDate") LocalDate startDate, @Param("endDate")LocalDate endDate);

    List<Shift> findByShiftDateLessThan(LocalDate endDate);

    @Query(nativeQuery = true, value = "SELECT * FROM shift where badge_id = :badgeId and shift_date = :shiftDate")
    List<Shift> findByBadgeIdAndShiftDateWithQuery(@Param("badgeId") String badgeId, @Param("shiftDate")LocalDate shiftDate);

    @Query(nativeQuery = true, value = "SELECT * FROM shift where shift_date >= :sundayDate and shift_date <:nextSunday")
    List<Shift> findRecentShiftsBySundayDate(String sundayDate, String nextSunday);

    @Query(nativeQuery = true, value = "SELECT * FROM shift where badge_id = :badgeId and shift_date >= :lastSunday and shift_date <:sunday order by shift_date")
    List<Shift> findByBadgeIdAndShiftDateBetweenWithQuery(String badgeId, String lastSunday, String sunday);

//public interface ShiftRepository extends JpaRepository<Shift, Integer> {
//    @Query(nativeQuery = true, value = "SELECT * FROM shift where badge_id =:badgeId ")
//    List<Shift> findRecentShiftsByBadgeId(@Param("badgeId") String badgeId);//works

    //    @Query(nativeQuery = true, value = "SELECT * FROM shift where badge_id = :badgeId ")
//    @Query(nativeQuery = true, value = "SELECT top(28) * FROM shift where badge_id = :badgeId ")
}
