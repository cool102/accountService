package account.utils;

import account.businesslayer.Salary;

import java.util.Comparator;

public class SalaryComparator implements Comparator<Salary> {
    @Override
    public int compare(Salary o1, Salary o2) {
        if (o1.getEmployee().equals(o2.getEmployee())) {

        }
        return 1;

    }
}
