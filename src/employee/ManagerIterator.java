package employee;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class ManagerIterator implements Iterator<ManagerRecord> {
    private final List<ManagerRecord> managers;
    private int position;

    public ManagerIterator(List<ManagerRecord> managers) {
        this.managers = managers;
        this.position = 0;
    }

    @Override
    public boolean hasNext() {
        return position < managers.size();
    }

    @Override
    public ManagerRecord next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more managers.");
        }

        return managers.get(position++);
    }
}