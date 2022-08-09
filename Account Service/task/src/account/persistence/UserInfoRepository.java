package account.persistence;

import account.businesslayer.UserInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoRepository extends CrudRepository<UserInfo, Long> {
    UserInfo findByEmailIgnoreCase(String email);

    UserInfo findByNameIgnoreCase(String name);
}
