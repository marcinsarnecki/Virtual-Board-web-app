package com.ctdp.springproject.repository;

import com.ctdp.springproject.model.Badge;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface BadgeRepository extends CrudRepository<Badge, Long> {
    @Query("DELETE FROM Badge b")
    @Modifying
    @Transactional
    void deleteCustom();
}
