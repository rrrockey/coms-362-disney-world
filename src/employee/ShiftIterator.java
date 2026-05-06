package employee;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class ShiftIterator implements Iterator<ShiftAssignment> {
    private final List<ShiftAssignment> shifts;
    private int position;

    public ShiftIterator(List<ShiftAssignment> shifts) {
        this.shifts = shifts;
        this.position = 0;
    }

    @Override
    public boolean hasNext() {
        return position < shifts.size();
    }

    @Override
    public ShiftAssignment next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more shifts.");
        }

        return shifts.get(position++);
    }
}