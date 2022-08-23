package account.persistence;

import account.businesslayer.SecurityEvent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface SecurityEventsRepository extends CrudRepository<SecurityEvent, Long> {

    SecurityEvent findByActionIgnoreCase (String code);
}
