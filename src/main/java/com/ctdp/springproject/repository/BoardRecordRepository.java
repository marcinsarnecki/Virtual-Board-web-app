package com.ctdp.springproject.repository;

import com.ctdp.springproject.model.BoardRecord;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BoardRecordRepository extends CrudRepository<BoardRecord, Long> {
    Optional<BoardRecord> findBoardRecordByPersonIdAndProjectId(Long person_id, Long project_id);
}
