package datastructure;

import java.util.Comparator;

public class Compare_Measure_Count implements Comparator<Dominate_Bitmap_ToRead> {
    public int compare(Dominate_Bitmap_ToRead o1, Dominate_Bitmap_ToRead o2) {
        if (o1.measure_count > o2.measure_count) {
            return 1;
        } else if (o1.measure_count < o2.measure_count) {
            return -1;
        } else {
            return 0;
        }
    }

}
