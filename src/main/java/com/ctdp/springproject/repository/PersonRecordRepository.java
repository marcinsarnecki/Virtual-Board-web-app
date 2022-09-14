package com.ctdp.springproject.repository;

import com.ctdp.springproject.model.PersonRecord;
import org.springframework.data.repository.CrudRepository;

public interface PersonRecordRepository extends CrudRepository<PersonRecord, Long> {
}
