package com.uniovi.sdi2425entrega1ext514.services;

import com.uniovi.sdi2425entrega1ext514.entities.LogEntry;
import com.uniovi.sdi2425entrega1ext514.repositories.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class LogService {

    @Autowired
    private LogRepository logRepository;


    /**
     * Guarda un Log en el repositorio
     * @param username
     * @param action
     * @param endpoint
     */
    public void saveLog(String username, String action, String endpoint) {
        LogEntry log = new LogEntry(username, action, endpoint);
        logRepository.save(log);
    }

    /**
     * Devuelve una lista con todos los logs
     * @return
     */
    public List<LogEntry> getAllLogs() {

        return logRepository.findAll();
    }

    /**
     * Elimina todos los logs que hay en ese momento
     */
    @Transactional
    public void deleteLogs() {
        List<LogEntry> logs = logRepository.findAll();
        logRepository.deleteAll(logs);
    }


    /**
     * Devuelve una lista con Logs segun el tipo indicado
     * @param type
     * @return
     */
    public List<LogEntry> getLogsByType(String type) {
        return logRepository.findByAction(type);
    }

    /**
     * Elimina los Logs del sistema segun su tipo
     * @param type
     */
    @Transactional
    public void deleteLogsByType(String type) {
        logRepository.deleteByAction(type);
    }

}
