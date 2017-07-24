package src.algrithm;

import src.data_structure.Tuple;
import src.global.Info;

/**
 * Created by Xue on 2017/7/24.
 */
public class Dominate_Compute
{
    public int dominate_comp(Tuple a , Tuple b)
    {
        int com_count = 0;
        int dom_count = 0;
        int equ_count = 0;

        for (int m = 0; m < Info.RELATED_ATTRIBUTES_NUMBER ; m++)
        {
            if ((a.attributes[m] != Long.MIN_VALUE)
                    && (b.attributes[m] != Long.MIN_VALUE))
            {
                com_count ++;

                if (a.attributes[m] <= b.attributes[m])
                {
                    dom_count ++;

                    if (a.attributes[m] ==
                            b.attributes[m])
                        equ_count ++;
                }
            }

        }

        //a支配b
        if ((dom_count == com_count) && (equ_count < com_count))
            return 1;

        //b支配a
        else if ((dom_count == 0) && (equ_count < com_count))
            return 0;

        //无法比较支配关系
        else
            return -1;


    }
}
