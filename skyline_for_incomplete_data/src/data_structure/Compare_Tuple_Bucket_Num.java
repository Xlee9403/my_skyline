package  src.data_structure;

import java.util.Comparator;

/**
 * Created by Xue on 2017/7/14.
 */
public class Compare_Tuple_Bucket_Num implements Comparator<Tuple>
{
    public int compare(Tuple o1, Tuple o2)
    {
        if (o1.bucket_index > o2.bucket_index)
        {
            return 1;
        }
        else if (o1.bucket_index < o2.bucket_index)
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }
}
