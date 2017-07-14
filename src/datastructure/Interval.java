package datastructure;

public class Interval {
    public long upperbound;
    public long lowerbound;

    public void copyform(Interval temp) {
        this.upperbound = temp.upperbound;
        this.lowerbound = temp.lowerbound;
    }

}
