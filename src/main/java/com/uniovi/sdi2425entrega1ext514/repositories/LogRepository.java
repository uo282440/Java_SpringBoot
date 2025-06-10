package com.uniovi.sdi2425entrega1ext514.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.uniovi.sdi2425entrega1ext514.entities.LogEntry;

import java.util.List;

@Repository
public interface LogRepository extends JpaRepository<LogEntry, Long> {
    List<LogEntry> findAll();

    List<LogEntry> findByAction(String action);

    void deleteByAction(String action);

}
