package account.persistence;

import account.businesslayer.Salary;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalaryRepository extends CrudRepository<Salary, Long> {

    Salary findByEmployeeAndPeriodIgnoreCase(String email, String period);


    @Query("select s from Salary s where s.employee = :employee and s.period = :period")
    Salary findByEmployeeAndPeriod(@Param("employee") String employee,
                                   @Param("period") String period);


    // @Query("select s from Salary s where s.employee = :employee ")
    // Salary findByEmployee(@Param("employee") String employee);

    @Query("select s from Salary s where s.period = :period ")
    Salary findByPeriod(@Param("period") String period);


    //List<Salary> findByEmployee(String employee);
    @Query("select s from Salary s where s.employee = :employee order by period desc")
    List<Salary> findByEmployee(@Param("employee") String employee);
}
