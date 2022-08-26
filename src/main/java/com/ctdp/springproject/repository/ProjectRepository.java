package com.ctdp.springproject.repository;

import com.ctdp.springproject.model.Project;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProjectRepository extends CrudRepository<Project, Long> {
    Optional<Project> findByName(String name);
}
