package src.data_structure;

import java.util.Comparator;

/**
 * Created by Xue on 2017/7/26.
 */
public class Compare_Column_PI implements Comparator<Column>
{
    public int compare(Column o1, Column o2)
    {
        if (o1.pi > o2.pi)
        {
            return 1;
        }
        else if (o1.pi < o2.pi)
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }
}
