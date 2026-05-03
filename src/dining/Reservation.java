package dining;

public class Reservation {

    private String guestName;
    private int partySize;
    private String date;
    private String time;
    private int tableNum;

    public Reservation(String guestName, int partySize, String date, String time, int tableNum) {
        this.guestName = guestName;
        this.partySize = partySize;
        this.date = date;
        this.time = time;
        this.tableNum = tableNum;
    }

    public String getGuestName() {
        return guestName;
    }

    public int getPartySize() {
        return partySize;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getTableNumber() { return tableNum; }
}
