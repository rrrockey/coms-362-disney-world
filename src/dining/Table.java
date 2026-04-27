package dining;

public class Table {

    private int tableNum;
    private int capacity;

    public Table(int tableNum, int capacity) {
        this.tableNum = tableNum;
        this.capacity = capacity;
    }

    public int getTableNumber() {return tableNum;}
    public int getCapacity() {return capacity;}
}