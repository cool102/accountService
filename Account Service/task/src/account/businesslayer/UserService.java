package account.businesslayer;

import account.UserInfoDetailsImpl;
import account.errors.*;
import account.persistence.RoleRepository;
import account.persistence.SecurityEventsRepository;
import account.persistence.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    public static final int MAX_FAILED_ATTEMPTS = 4;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final RoleRepository roleRepository;

    @Autowired
    private final SecurityEventsRepository eventsRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    @Autowired
    PasswordEncoder encoder;

    Set<String> breachedPasswords = new HashSet<>(List.of("PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
            "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
            "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember"));

    public UserService(UserRepository userRepository, RoleRepository roleRepository, SecurityEventsRepository eventsRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.eventsRepository = eventsRepository;
    }


    public void increaseFailedAttempts(UserInfo userInfo) {
        int newFailedAttempts = userInfo.getFailedAttempt() + 1;
        userRepository.updateFailedAttempts(newFailedAttempts, userInfo.getEmail());
    }

    public void resetFailedAttempts(String email) {
        userRepository.updateFailedAttempts(0, email);
    }

    public Map<String, String> lock(UserInfo userInfo, HttpServletRequest request) {


        userInfo.setAccountNonLocked(false);
        userRepository.save(userInfo);

        LOGGER.info("LOCK_USER");
        SecurityEvent lockUserEvent = new SecurityEvent(
                LocalDateTime.now(), "LOCK_USER", userInfo.getEmail().toLowerCase(),
                "Lock user " + userInfo.getEmail().toLowerCase(), request.getRequestURI()
        );
        eventsRepository.save(lockUserEvent);

        return Map.of("status", "User " + userInfo.getEmail() + " locked!");
    }

    public Map<String, String> unLock(UserInfo userInfo, HttpServletRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("ayth" + auth);
        UserInfoDetailsImpl details = (UserInfoDetailsImpl) auth.getPrincipal();
        System.out.println("details" + details);

        userInfo.setAccountNonLocked(true);
        userInfo.setFailedAttempt(0);
        userRepository.save(userInfo);

        LOGGER.info("UNLOCK_USER");
        SecurityEvent unlockUserEvent = new SecurityEvent(
                LocalDateTime.now(), "UNLOCK_USER", details.getEmail().toLowerCase(),
                "Unlock user " + userInfo.getEmail().toLowerCase(), request.getRequestURI()
        );
        eventsRepository.save(unlockUserEvent);
        return Map.of("status", "User " + userInfo.getEmail() + " unlocked!");
    }

    public void unLock1(UserInfo userInfo) {

        userInfo.setAccountNonLocked(true);
        userInfo.setFailedAttempt(0);
        userRepository.save(userInfo);

    }

    private static boolean isEmailExist(UserRepository userRepository, String email) {

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

    public UserRegistredDTO register(UserInfo user) {
        String subject = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(auth);
        if ((auth.getPrincipal().toString()).equals("anonymousUser")) {
            subject = "Anonymous";
        }
        // UserInfoDetailsImpl details = (UserInfoDetailsImpl) auth.getPrincipal();

        Role roleAdmin = new Role(1L, "ROLE_Administrator".toUpperCase(), "Administrative Group");
        Role roleAccountant = new Role(2L, "ROLE_Accountant".toUpperCase(), "Business Group");
        Role roleUser = new Role(3L, "ROLE_User".toUpperCase(), "Business Group");
        Role roleAnonymous = new Role(4L, "ROLE_Anonymous".toUpperCase(), "-");
        Role roleAuditor = new Role(5L, "ROLE_AUDITOR", "Business Group");

        roleRepository.save(roleAdmin);
        roleRepository.save(roleAccountant);
        roleRepository.save(roleUser);
        roleRepository.save(roleAnonymous);
        roleRepository.save(roleAuditor);


        if (breachedPasswords.contains(user.getPassword())) {
            throw new CompromisedPasswordException();
        }
        if (isEmailExist(userRepository, user.getEmail().toLowerCase())) {
            throw new UserExistException();
        } else {
            user.setPassword(encoder.encode((user.getPassword())));
            updateUserRole(user);

            UserInfo savedUser = userRepository.save(user);
            LOGGER.info("CREATE_USER");

            SecurityEvent event = new SecurityEvent(LocalDateTime.now(), "CREATE_USER", subject, user.getEmail().toLowerCase(), "/api/auth/signup");
            eventsRepository.save(event);

            Set<Role> userRoles = savedUser.getUserRoles();


            Set<String> rolesCodesAsc = getStringCodeOfRole(userRoles);
            return new UserRegistredDTO(savedUser.getId(), savedUser.getName(), savedUser.getLastname(), savedUser.getEmail().toLowerCase(),
                    rolesCodesAsc);


        }
    }


    public Map<String, String> deleteUser(String deletedId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfoDetailsImpl details = (UserInfoDetailsImpl) auth.getPrincipal();

        UserInfo toDelete = userRepository.findByEmailIgnoreCase(deletedId);

        if ((Objects.nonNull(toDelete))) {
            Set<Role> deletingUserRoles = toDelete.getUserRoles();

            if (isAdminDeletingAttemption(deletingUserRoles)) {
                throw new AdminDeletionAttemptException();
            } else {
                LOGGER.info("DELETE_USER");
                SecurityEvent deleteUserEvent = new SecurityEvent(
                        LocalDateTime.now(), "DELETE_USER", details.getEmail().toLowerCase(), deletedId.toLowerCase(), "/api/admin/user"
                );
                eventsRepository.save(deleteUserEvent);
                return delete(deletedId);
            }
        } else {
            throw new UserNotFoundException();
        }
    }


    private boolean isAdminDeletingAttemption(Set<Role> deletingUserRoles) {

        return deletingUserRoles.stream().anyMatch(r -> r.getCode().equalsIgnoreCase("role_administrator"));
        //UserInfo toDelete = userRepository.findByEmailIgnoreCase(deletedId);
        //Set<Role> toDelRoles = toDelete.getUserRoles();
        //Set<String> collect = toDelRoles.stream().map(role -> role.code).collect(Collectors.toSet());
        //Set<SimpleGrantedAuthority> deletedUserAuthorities = collect.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
        //return deletedUserAuthorities;
    }

    private Map<String, String> delete(String id) {
        UserInfo toDelete = userRepository.findByEmailIgnoreCase(id);
        Map<String, String> responseBody = new HashMap<>();

        userRepository.delete(toDelete);
        responseBody.put("user", id);
        responseBody.put("status", "Deleted successfully!");

        return responseBody;

    }

    //если юзер не найден то 404
    //если роль не найдена то  404
    // если удаляется роль которой пользователь не обладает то 400
    // если удаляется единственная роль то 400
    //если удаляется роль Админ то 400
    // если админ пользователю с ролью админ добавлется бизнес роль, то 400
    // если бизнес пользователю добавляется админ роль то 400
    public UserRegistredDTO changeRole(UserChangeRoleDTO input) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfoDetailsImpl details = (UserInfoDetailsImpl) auth.getPrincipal();


        String newRole = "ROLE_" + input.getRole().toUpperCase();
        if (userRepository.findByEmailIgnoreCase(input.getUser()) == null) {
            throw new UserNotFoundException();
        }
        Role foundRole = roleRepository.findByCodeIgnoreCase(newRole);
        if (foundRole == null) {
            throw new RoleNotFoundException();
        }

        UserInfo foundUser = userRepository.findByEmailIgnoreCase(input.getUser());
        Set<Role> foundRoles = foundUser.getUserRoles();

        String operation = input.getOperation();
        Role changingRole = roleRepository.findByCodeIgnoreCase(newRole);
        long id = foundUser.getId();
        boolean grant = false;
        boolean remove = false;

        if (operation.equals("REMOVE")) {
            remove = true;

        }
        if (operation.equals("GRANT")) {
            grant = true;
        }


        if (!foundRoles.contains(changingRole) & remove) {
            throw new UserNotHaveRoleException();
        }


        if (foundRoles.size() == 1 & remove & id > 1) {
            throw new UserMustHaveAtLeastOneRoleException();
        }

        if (id == 1 & remove & newRole.equalsIgnoreCase("role_administrator")) { //TODO посмотреть про admin роль
            throw new AdminRoleRemoveException();
        }


        boolean adminWantsBusiness = inAdminGroup(input) & !wantsAdminRole(newRole);
        boolean userWantAdmin = !inAdminGroup(input) & wantsAdminRole(newRole);
        if ((adminWantsBusiness) | (userWantAdmin)) {
            throw new RolesCombineException();
        }


        if (grant) {
            foundUser.getUserRoles().add(changingRole);

            LOGGER.info("GRANT_ROLE");
            SecurityEvent grantRoleEvent = new SecurityEvent(LocalDateTime.now(),
                    "GRANT_ROLE", details.getEmail(), "Grant role " + changingRole.getCode().toUpperCase().replace("ROLE_", "") +
                    " to " + input.getUser().toLowerCase(), "/api/admin/user/role");
            eventsRepository.save(grantRoleEvent);

            userRepository.save(foundUser);


            String name = foundUser.getName();
            String lastname = foundUser.getLastname();
            String email = foundUser.getEmail().toLowerCase();
            Set<Role> roles = foundUser.getUserRoles();
            Set<String> rolesCodesAsc = getStringCodeOfRole(roles);
            return new UserRegistredDTO(id, name, lastname, email.toLowerCase(), rolesCodesAsc);
        } else {
            foundUser.getUserRoles().remove(changingRole);
            LOGGER.info("REMOVE_ROLE");
            SecurityEvent removeRoleEvent = new SecurityEvent(LocalDateTime.now(),
                    "REMOVE_ROLE", details.getEmail(), "Remove role " + changingRole.getCode().toUpperCase().replace("ROLE_", "") +
                    " from " + input.getUser().toLowerCase(), "/api/admin/user/role");
            eventsRepository.save(removeRoleEvent);

            userRepository.save(foundUser);


            String name = foundUser.getName();
            String lastname = foundUser.getLastname();
            String email = foundUser.getEmail().toLowerCase();
            Set<Role> roles = foundUser.getUserRoles();
            Set<String> rolesCodesAsc = getStringCodeOfRole(roles);
            return new UserRegistredDTO(id, name, lastname, email.toLowerCase(), rolesCodesAsc);
        }

    }

    private boolean inAdminGroup(UserChangeRoleDTO input) {
        UserInfo foundUser = userRepository.findByEmailIgnoreCase(input.getUser());
        Set<Role> foundRoles = foundUser.getUserRoles();
        boolean inAdminGroup = foundRoles.stream().anyMatch(r -> r.name.equals("Administrative Group"));
        return inAdminGroup;
    }

    private boolean wantsAdminRole(String role) {
        Role foundRole = roleRepository.findByCodeIgnoreCase(role);
        boolean wantsAdminRole = foundRole.getName().equals("Administrative Group");
        return wantsAdminRole;

    }

    private Set<String> getStringCodeOfRole(Set<Role> userRoles) {
        Set<String> roleCodes = userRoles.stream().map(Role::getCode).collect(Collectors.toSet());
        Set<String> rolesCodesAsc = new TreeSet<>();
        rolesCodesAsc.addAll(roleCodes);
        return rolesCodesAsc;
    }

    private void updateUserRole(UserInfo userInfo) {
        if (!userRepository.findAll().iterator().hasNext()) {
            Role role = roleRepository.findByCodeIgnoreCase("Role_Administrator".toUpperCase());
            userInfo.getUserRoles().add(role);
        } else {
            Role role = roleRepository.findByCodeIgnoreCase("Role_User".toUpperCase());
            userInfo.getUserRoles().add(role);
        }

    }


    public Set<UserRegistredDTO> getAllRegistredUsers() {
        Set<UserRegistredDTO> registredUsers = new TreeSet<>(Comparator.comparing(UserRegistredDTO::getId));
        Iterable<UserInfo> all = userRepository.findAll();
        Iterator<UserInfo> iterator = all.iterator();

        while (iterator.hasNext()) {
            UserInfo next = iterator.next();

            long id = next.getId();
            String name = next.getName();
            String lastname = next.getLastname();
            String email = next.getEmail();
            Set<Role> userRoles = next.getUserRoles();

            Set<String> stringCodeOfRole = getStringCodeOfRole(userRoles);
            UserRegistredDTO userRegistredDTO = new UserRegistredDTO(id, name, lastname, email.toLowerCase(), stringCodeOfRole);

            registredUsers.add(userRegistredDTO);
        }
        return registredUsers;
    }

    public ResponseEntity<Map<String, Object>> changePass(Password newPassword, UserInfoDetailsImpl details) {

        String currentEmail = details.getEmail();

        if (breachedPasswords.contains(newPassword.getPassValue())) {
            throw new CompromisedPasswordException();
        } else if (encoder.matches(newPassword.getPassValue(), details.getPassword())) {
            throw new SamePasswordException();
        } else {

            LOGGER.info("CHANGE_PASSWORD");
            UserInfo toSave = userRepository.findByEmailIgnoreCase(currentEmail);
            toSave.setPassword(encoder.encode(newPassword.getPassValue()));
            SecurityEvent changePasswordEvent = new SecurityEvent(
                    LocalDateTime.now(),
                    "CHANGE_PASSWORD", details.getEmail().toLowerCase(), details.getEmail().toLowerCase(),
                    "/api/auth/changepass"
            );
            eventsRepository.save(changePasswordEvent);
            userRepository.save(toSave);


            Map<String, Object> body = new LinkedHashMap<>();
            body.put("email", currentEmail.toLowerCase());
            body.put("status", "The password has been updated successfully");
            return new ResponseEntity<>(body, HttpStatus.OK);
        }
    }


}