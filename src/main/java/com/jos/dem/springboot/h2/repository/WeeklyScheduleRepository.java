package com.jos.dem.springboot.h2.repository;

import com.jos.dem.springboot.h2.model.WeeklySchedule;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeeklyScheduleRepository extends CrudRepository<WeeklySchedule,Integer> {
}
