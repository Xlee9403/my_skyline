package datastructure;

import java.util.Comparator;

public class Compare_Tuple_Index implements Comparator<Tuple> {
    public int compare(Tuple o1, Tuple o2) {
        if (o1.position_index > o2.position_index) {
            return 1;
        } else if (o1.position_index < o2.position_index) {
            return -1;
        } else {
            return 0;
        }
    }

}
