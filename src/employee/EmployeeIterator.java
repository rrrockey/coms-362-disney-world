package employee;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class EmployeeIterator implements Iterator<EmployeeRecord> {
    private final List<EmployeeRecord> employees;
    private int position;

    public EmployeeIterator(List<EmployeeRecord> employees) {
        this.employees = employees;
        this.position = 0;
    }

    @Override
    public boolean hasNext() {
        return position < employees.size();
    }

    @Override
    public EmployeeRecord next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more employees.");
        }

        return employees.get(position++);
    }
}