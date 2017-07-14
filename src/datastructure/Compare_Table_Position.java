package datastructure;

import java.util.Comparator;

public class Compare_Table_Position implements Comparator<Column> {
    public int compare(Column o1, Column o2) {
        if (o1.table_position > o2.table_position) {
            return 1;
        } else if (o1.table_position < o2.table_position) {
            return -1;
        } else {
            return 0;
        }
    }

}
