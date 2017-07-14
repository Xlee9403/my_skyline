package data_structure;

import java.util.Comparator;

/**
 * Created by Xue on 2017/7/14.
 */
public class Compare_Tuple_Average_Value implements Comparator<Tuple>
{
    public int compare(Tuple o1, Tuple o2)
    {
        if (o1.average_value > o2.average_value)
        {
            return 1;
        }
        else if (o1.average_value < o2.average_value)
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }
}
