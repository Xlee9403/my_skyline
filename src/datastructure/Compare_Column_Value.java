package datastructure;

import java.util.Comparator;

public class Compare_Column_Value implements Comparator<Column> {
    public int compare(Column o1, Column o2) {
        if (o1.column_value > o2.column_value) {
            return 1;
        } else if (o1.column_value < o2.column_value) {
            return -1;
        } else {
            return 0;
        }

    }

}

