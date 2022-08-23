package account.presentation;

import account.UserInfoDetailsImpl;
import account.businesslayer.*;
import account.errors.AdminLockingAttemptException;
import account.errors.DoubleSalaryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Validated
@RestController
public class Controller {


    @Autowired
    UserService userService;

    @Autowired
    SalaryService salaryService;
    @Autowired
    EventsService eventsService;

    @PostMapping("api/acct/payments")
    public ResponseEntity<Map<String, String>> uploadsPayroll(@RequestBody List<@Valid Salary> payroll) {

        Set<Salary> salariesInset = new HashSet<Salary>();
        salariesInset.addAll(payroll);
        System.out.println(salariesInset.size() == payroll.size());
        if (payroll.size() == salariesInset.size()) {
            List<Salary> salaries = salaryService.saveSalaryToDb(payroll);
            if (!salaries.isEmpty()) {

                Map<String, String> body = new LinkedHashMap<>();

                body.put("status", "Added successfully!");

                return new ResponseEntity<>(body, HttpStatus.OK);

            } else {
                Map<String, String> body = new LinkedHashMap<>();

                body.put("status", "WRONG!");

                return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
            }
        } else {
            throw new DoubleSalaryException();
        }


    }

    @PutMapping("api/acct/payments")
    public ResponseEntity<Map<String, String>> changeSalaryOfUser(@RequestBody @Valid Salary salary) {
        Salary toSave = salaryService.findByEmailIgnoreCaseAndPeriod1(salary.getEmployee(),
                salary.getPeriod());
        toSave.setSalary(salary.getSalary());
        salaryService.saveSalaryToDb(toSave);

        Map<String, String> body = new LinkedHashMap<>();

        body.put("status", "Updated successfully!");

        return new ResponseEntity<>(body, HttpStatus.OK);

    }

    @GetMapping(value = "/api/empl/payment")
    public Object getPayment(@RequestParam(required = false) @Pattern(regexp = "([0][1-9]-2[0-9]{3}|[1][0-2]-2[0-9]{3})", message = "Error!") String period, Authentication auth) {

        UserInfoDetailsImpl details = (UserInfoDetailsImpl) auth.getPrincipal();

        String currentUserName = details.getUsername();
        String currentUserLastName = details.getLastname();
        String currentUserEmail = details.getEmail();
        Collection<GrantedAuthority> currUserAuthorities = details.getAuthorities();

        //boolean currUserIsAccountant = currUserAuthorities.contains(new SimpleGrantedAuthority("ROLE_ACCOUNTANT"));
        boolean currUserIsAccountant = isCurrUserIsAccountant(currUserAuthorities);

        if (period != null) {

            Salary byPeriod = salaryService.findByEmailIgnoreCaseAndPeriod1(currentUserEmail, period);
            String strPeriod = byPeriod.getPeriod();
            String periodNewFormat = periodFormatter(strPeriod);
            Long salary = byPeriod.getSalary();
            String salaryNewFormat = salaryFormatter(salary);
            return new SalaryResponseDTO(currentUserName, currentUserLastName, periodNewFormat, salaryNewFormat);

        } else {
            // if (!currUserIsAccountant) {
            List<Salary> allSalariesForCurrentUser = salaryService.findAllSalariesForCurrentUser(currentUserEmail);
            List<SalaryResponseDTO> list = new ArrayList<>();
            for (Salary oldSalary : allSalariesForCurrentUser
            ) {
                String periodValue = periodFormatter(oldSalary.getPeriod());
                String salaryValue = salaryFormatter(oldSalary.getSalary());

                list.add(new SalaryResponseDTO(currentUserName, currentUserLastName, periodValue, salaryValue));
            }
            // if (!list.isEmpty()) {
            return list;
            //}
            // } else {
            //   List<Salary> allSalaries = salaryService.findAllSalaries();
            // return  allSalaries;
            // }

        }
        //eturn new ArrayList<Salary>();

    }

    private boolean isCurrUserIsAccountant(Collection<GrantedAuthority> authorities) {
        return authorities.stream().anyMatch(a -> a.getAuthority().equalsIgnoreCase("role_accountant"));
    }

    private String salaryFormatter(Long salary) {
        String salaryNewFormat = String.format("%d dollar(s) %d cent(s)", salary / 100, salary % 100);
        return salaryNewFormat;
    }

    private String periodFormatter(String strPeriod) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM-yyyy");
        YearMonth thisYearMonth = YearMonth.parse(strPeriod, dateTimeFormatter);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM-yyyy");
        String periodNewFormat = thisYearMonth.format(formatter);
        return periodNewFormat;
    }

    @PostMapping("/api/auth/changepass")
    public ResponseEntity<Map<String, Object>> changePassword(@Valid @RequestBody Password password, Authentication auth) {
        UserInfoDetailsImpl details = (UserInfoDetailsImpl) auth.getPrincipal();

        return userService.changePass(password, details);
    }


    @PostMapping(value = "/api/auth/signup")
    public UserRegistredDTO signup(@Valid @RequestBody UserInfo user, Authentication auth) {

        System.out.println();
        return userService.register(user);
    }

    @GetMapping("/api/admin/user")
    public Set<UserRegistredDTO> getUsers() {
        return
                userService.getAllRegistredUsers();
    }

    @DeleteMapping(value = "api/admin/user/{id}")
    public Map<String, String> deleteUser(@PathVariable("id") String id) {

        return userService.deleteUser(id);
    }

    @PutMapping(value = "/api/admin/user/role")
    public UserRegistredDTO setRole(@RequestBody UserChangeRoleDTO userChangeRoleDTO) {
        return userService.changeRole(userChangeRoleDTO);
    }

    // {
    //     "date": "<date>", // timestamp в дату
    //         "action": "<event_name from table>",  //FORMATTED_MESSAGE
    //         "subject": "<The user who performed the action>", //email того кто вызывал данный api
    //         "object": "<The object on which the action was performed>",
    //         "path": "<api>"
    // }

    //если нельзя определить того кто проводит действие что то указать что это Anon

    //TODO блокировка
    // если было >5 последовательных попыток ввести неправильный пароль то запись попадает в секурные евенты
    // причем должны бвть события LOGIN_FAILED -> BRUTE_FORCE -> LOCK_USER
    // после этого учетка блокируется
    //чтобы разблокировать нужно создать новый эндпойнт PUT api/admin/user/access
    // {
    //     "user": "<String value, not empty>",
    //         "operation": "<[LOCK, UNLOCK]>"
    // }

    // {
    //     "status": "User <username> <[locked, unlocked]>!"
    // }

    //если пользователь правильно ввел пароль, то сбросить счетчик
    // админа нельзя заблокировать
    //
    //{
    //    "timestamp": "<date>",
    //        "status": 400,
    //        "error": "Bad Request",
    //        "message": "Can't lock the ADMINISTRATOR!",
    //        "path": "<api>"
    //}

    //добавить GET api/security/events -возвращать события в в виде c id по возраст[] , если событий нет, то пустой []
    //изменить ролевую модель см таблицу

    @GetMapping(value = "/api/security/events")
    public List<SecurityEvent> getEvents() {
        return eventsService.getAllEvents();

    }

    @PutMapping(value = "/api/admin/user/access")
    public Map<String, String> lockUnlock(@RequestBody UserLockDTO userLockDTO, Authentication auth,  HttpServletRequest request) {

        UserInfoDetailsImpl details = (UserInfoDetailsImpl) auth.getPrincipal();
        if (details.getEmail().equalsIgnoreCase(userLockDTO.getUser()) ) {
            throw new AdminLockingAttemptException();
        }

        UserInfo userInfo = userService.findByEmailIgnoreCase(userLockDTO.getUser());
        if (userLockDTO.getOperation().equalsIgnoreCase("lock")) {
            return userService.lock(userInfo,request);
        } else {
            return userService.unLock(userInfo,request);
        }

    }
}
