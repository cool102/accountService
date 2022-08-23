package account.businesslayer;

import account.persistence.SalaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SalaryService {

    private final SalaryRepository repository;

    @Autowired
    public SalaryService(SalaryRepository repository) {
        this.repository = repository;
    }

    public Salary saveSalaryToDb(Salary salary) {
        return repository.save(salary);
    }

    public List<Salary> saveSalaryToDb(List<Salary> payroll) {
        List<Salary> salariesList = new ArrayList<>();
        Iterable<Salary> salaries = repository.saveAll(payroll);
        for (Salary salary : salaries
        ) {
            salariesList.add(salary);
        }

        return salariesList;
    }

    public Salary findByEmailIgnoreCaseAndPeriod1(String email, String period) {
        return repository.findByEmployeeAndPeriodIgnoreCase(email, period);
    }

    public Salary findByEmailAndPeriod2(String email, String period) {
        return repository.findByEmployeeAndPeriod(email, period);
    }


    public Salary findByPeriod(String period) {
        return repository.findByPeriod(period);
    }

    public List<Salary> findAllSalariesForCurrentUser(String email) {
        return repository.findByEmployee(email);
    }

    public List<Salary> findAllSalaries() {
        List<Salary> salariesList = new ArrayList<>();
        Iterable<Salary> all = repository.findAll();
        for (Salary salary : all
        ) {
            salariesList.add(salary);
        }

        return salariesList;
    }


}
