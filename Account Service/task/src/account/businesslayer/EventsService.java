package account.businesslayer;


import account.persistence.SecurityEventsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service public class EventsService {
    @Autowired
    SecurityEventsRepository repository;

    public EventsService(SecurityEventsRepository repository) {
        this.repository = repository;
    }

  public  List<SecurityEvent> getAllEvents(){
        Iterable<SecurityEvent> all = repository.findAll();
        List<SecurityEvent> securityEvents = new ArrayList<>();

        for (SecurityEvent event : all
        ) {
            securityEvents.add(event);
        }

        return securityEvents;
    }

    public void  saveEvent(SecurityEvent event) {
        repository.save(event);
    }
}
