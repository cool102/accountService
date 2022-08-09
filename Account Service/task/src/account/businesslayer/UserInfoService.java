package account.businesslayer;

import account.UserInfoDetailsImpl;
import account.errors.CompromisedPasswordException;
import account.errors.SamePasswordException;
import account.errors.UserExistException;
import account.persistence.RoleRepository;
import account.persistence.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserInfoService {
    @Autowired
    private final UserInfoRepository userRepository;
    @Autowired
    private final RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    Set<String> breachedPasswords = new HashSet<>(List.of("PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
            "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
            "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember"));

    public UserInfoService(UserInfoRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    private static boolean isEmailExist(UserInfoRepository userRepository, String email) {

        List<UserInfo> allUsers = (List<UserInfo>) userRepository.findAll();
        if (allUsers.isEmpty()) {
            return false;
        } else {
            for (var user : allUsers
            ) {
                if (user.getEmail().equalsIgnoreCase(email))
                    return true;
            }
            return false;
        }

    }

    public UserInfo saveUserToDb(UserInfo toSave) {
        return userRepository.save(toSave);
    }

    public List<UserInfo> findAllUsers() {
        return (List<UserInfo>) userRepository.findAll();
    }

    public UserInfo findByEmailIgnoreCase(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    public UserInfo findByNameIgnoreCase(String name) {

        return userRepository.findByNameIgnoreCase(name);
    }

    public UserInfo register(UserInfo user) {


        Role roleAdmin = new Role(1L, "Administrator","Administrative Group");
        Role roleAccountant = new Role(2L, "Accountant","Business Group");
        Role roleUser = new Role(3L, "User","Business Group");
        Role roleAnonymous = new Role(4L, "Anonymous","Business Group");

        roleRepository.save(roleAdmin);
        roleRepository.save(roleAccountant);
        roleRepository.save(roleUser);
        roleRepository.save(roleAnonymous);


        if (breachedPasswords.contains(user.getPassword())) {
            throw new CompromisedPasswordException();
        }
        if (isEmailExist(userRepository, user.getEmail().toLowerCase())) {
            throw new UserExistException();
        } else {
            user.setPassword(encoder.encode((user.getPassword())));
            updateUserRole(user);

            UserInfo savedUser = userRepository.save(user);
            return new UserInfo(savedUser.getId(),
                    savedUser.getName(),
                    savedUser.getLastname(),
                    savedUser.getEmail().toLowerCase(),
                    savedUser.getUserRoles());
        }
    }

    private void updateUserRole(UserInfo userInfo) {
        if (!userRepository.findAll().iterator().hasNext()) {
            Role role = roleRepository.findByRoleName("Administrator");
            userInfo.getUserRoles().add(role);
        } else {
            Role role = roleRepository.findByRoleName("User");
            userInfo.getUserRoles().add(role);
        }

    }

    public ResponseEntity<Map<String, Object>> changePass(Password password, UserInfoDetailsImpl details) {
        if (breachedPasswords.contains(password.getPassValue())) {
            throw new CompromisedPasswordException();
        } else if (encoder.matches(password.getPassValue(), details.getPassword())) {
            throw new SamePasswordException();
        } else {

            UserInfo toSave = userRepository.findByEmailIgnoreCase(details.getEmail());
            toSave.setPassword(encoder.encode(password.getPassValue()));
            userRepository.save(toSave);

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("email", details.getEmail());
            body.put("status", "The password has been updated successfully");
            return new ResponseEntity<>(body, HttpStatus.OK);
        }
    }
}