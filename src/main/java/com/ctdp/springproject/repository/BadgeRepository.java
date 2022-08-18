package com.ctdp.springproject.repository;

import com.ctdp.springproject.model.Badge;
import org.springframework.data.repository.CrudRepository;

public interface BadgeRepository extends CrudRepository<Badge, Long> {
}
