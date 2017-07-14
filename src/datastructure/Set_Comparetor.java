package datastructure;

import java.util.Comparator;

public class Set_Comparetor implements Comparator<Object> {
    public int compare(Object o1, Object o2) {

        if (o1 instanceof Column) {
            return compare(((Column) o1).table_position, ((Column) o2).table_position);
        } else if (o1 instanceof Tuple) {
            return compare(((Tuple) o1).MPI, ((Tuple) o2).MPI);
        } else {
            return -1;
        }

    }

    public int compare(long o1, long o2) {
        if (o1 > o2) {
            return 1;
        } else if (o1 < o2) {
            return -1;
        } else {
            return 0;
        }
    }


}
