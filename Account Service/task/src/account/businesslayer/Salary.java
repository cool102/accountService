package account.businesslayer;

import account.errors.SalaryPeriodConstraint;

import javax.persistence.*;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Entity
@Table(name = "salary")
public class Salary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    // @ManyToOne
    // @JoinColumn(name = "employee")
    // private UserInfo userInfo;

    @Column
    private String employee;

    // @Temporal(TemporalType.DATE)
    // @Column(columnDefinition = "date")
    //@Pattern(regexp = "([0][1-9]-2[0-9]{3}|[1][0-2]-2[0-9]{3})", message = "Wrong date!")
    @SalaryPeriodConstraint
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-yyyy")
    private String period;
    @Positive(message = "Salary must be non negative!")
    @Column
    private Long salary;

    public Salary() {
    }

    public Salary(String email, String period, Long salary) {
        this.employee = email;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");
        YearMonth ym = YearMonth.parse(period, formatter);

        LocalDate localDate = ym.atDay(1);
        this.period = period;

        this.salary = salary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Salary salary = (Salary) o;
        return employee.equals(salary.employee) && period.equals(salary.period);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employee, period);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPeriod() {

        return period;
    }

    public void setPeriod(String period) {
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");
        //YearMonth ym = YearMonth.parse(period, formatter);
        //LocalDate localDate = ym.atDay(1);

        this.period = period;


    }

    public String getEmployee() {
        return employee;
    }


    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public Long getSalary() {
        return salary;
    }

    public void setSalary(Long salary) {
        this.salary = salary;
    }
}
