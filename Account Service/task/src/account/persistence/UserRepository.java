package account.persistence;

import account.businesslayer.Salary;
import account.businesslayer.UserInfo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends CrudRepository<UserInfo, Long> {
    UserInfo findByEmailIgnoreCase(String email);

    UserInfo findByNameIgnoreCase(String name);


    @Query("UPDATE UserInfo AS u SET u.failedAttempt = :failedAttempt WHERE u.email =:email")
    @Transactional
    @Modifying
    public void updateFailedAttempts(@Param("failedAttempt") int failedAttempts, @Param("email") String email);



}
