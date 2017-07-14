package data_structure;

import java.util.Comparator;

/**
 * Created by Xue on 2017/7/14.
 */
public class Compare_Column_Value implements Comparator<Column>
{
    public int compare(Column o1, Column o2)
    {
        if (o1.value > o2.value)
        {
            return 1;
        }
        else if (o1.value < o2.value)
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }
}
