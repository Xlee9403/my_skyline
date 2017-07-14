package datastructure;

import java.util.Comparator;

public class Compare_Entropy implements Comparator<LESS_Tuple> {
    public int compare(LESS_Tuple o1, LESS_Tuple o2) {
        if (o1.entroty > o2.entroty) {
            return 1;
        } else if (o1.entroty < o2.entroty) {
            return -1;
        } else {
            return 0;
        }
    }

}
