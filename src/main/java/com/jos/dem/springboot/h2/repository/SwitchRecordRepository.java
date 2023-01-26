package com.jos.dem.springboot.h2.repository;

import com.jos.dem.springboot.h2.model.DealerAndSwitchRecordCount;
import com.jos.dem.springboot.h2.model.SwitchRecord;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface SwitchRecordRepository extends CrudRepository<SwitchRecord,Integer> {

    @Query(value="from SwitchRecord t where switchDate1 BETWEEN :startDate AND :endDate")
    List<SwitchRecord> findByDate(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);//works

    @Query(value="from SwitchRecord t where employee1 < :id")
    List<SwitchRecord> getSwitchRecords(@Param("id") int id);//works

    List<SwitchRecord> findByConfirmationId(String confirmationId);

    List<SwitchRecord> findByConfirmationIdAndEmployee1AndEmployee2(String denied, String employee1, String employee2);

    @Query(nativeQuery = true, value="select employee1 as badgeId, count(employee1) as switchRecordCount from switch_record group by employee1 order by count(employee1) desc;")
    List<Object[]> findSwitchRecordCountByDealer1();

    @Query(nativeQuery = true, value="select employee2 as badgeId, count(employee2) as switchRecordCount from switch_record group by employee2 order by count(employee2) desc;")
    List<Object[]> findSwitchRecordCountByDealer2();

    @Query(nativeQuery = true, value="SELECT CONCAT(employee1, ',', employee2) as employeeID, switch_date1 as pickUpDate \n" +
            "FROM switch_record where trim(type_of_switch) = 'PU/GA';")
    List<Object[]> findAllPickup();

}
