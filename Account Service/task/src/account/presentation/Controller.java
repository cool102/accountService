package account.presentation;

import account.UserInfoDetailsImpl;
import account.businesslayer.*;
import account.errors.DoubleSalaryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Validated
@RestController
public class Controller {


    @Autowired
    UserInfoService userService;

    @Autowired
    SalaryService salaryService;


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

        if (period != null) {
            String currentUserEmail = details.getEmail();

            Salary byPeriod = salaryService.findByEmailIgnoreCaseAndPeriod1(currentUserEmail, period);

            String strPeriod = byPeriod.getPeriod();
            String periodNewFormat = periodFormatter(strPeriod);

            Long salary = byPeriod.getSalary();
            String salaryNewFormat = salaryFormatter(salary);

            String username = details.getName();
            String lastname = details.getLastname();

            return new SalaryResponseDTO(username, lastname, periodNewFormat, salaryNewFormat);

        } else {
            String currentUserEmail = details.getEmail();
            List<Salary> allSalariesForCurrentUser = salaryService.findAllSalariesForCurrentUser(currentUserEmail);

            List<SalaryResponseDTO> list = new ArrayList<>();

            for (Salary oldSalary : allSalariesForCurrentUser
            ) {
                String periodValue = periodFormatter(oldSalary.getPeriod());
                String salaryValue = salaryFormatter(oldSalary.getSalary());
                String username = details.getName();
                String lastname = details.getLastname();
                list.add(new SalaryResponseDTO(username, lastname, periodValue, salaryValue));
            }


            if (!list.isEmpty()) {
                return list;
            }
        }
        return new ArrayList<Salary>();

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
    public UserInfo signup(@Valid @RequestBody UserInfo user) {
        return userService.register(user);
    }


}
