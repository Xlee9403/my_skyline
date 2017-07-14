package datastructure;

import java.util.Comparator;

public class Compare_MPI implements Comparator<Tuple> {
    public int compare(Tuple o1, Tuple o2) {
        if (o1.MPI > o2.MPI) {
            return 1;
        } else if (o1.MPI < o2.MPI) {
            return -1;
        } else {
            return 0;
        }
    }

}

