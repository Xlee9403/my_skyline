package  src.algrithm;

import  src.data_structure.Tuple;
import  src.global.Info;

import java.io.*;

/**
 * Created by Xue on 2017/7/14.
 */
public class MY_RPID
{
    public void generate_skyline()
    {
        //记录I/O数
        int in_out_count = 0;

        int skyline_result = 0;
        try
        {
            BufferedInputStream pt_reader = new BufferedInputStream(
                    new FileInputStream(Info.ROOT_PATH
                            + Info.TABLE_WITH_SORTED_PI_PATH));

            byte[] pt_buf = new byte[Info.TUPLE_AVERAGE_BYTES_LENGTH];
            int ptToRead = pt_buf.length;
            int bytesRead = 0;

            int[] dominated_sorted_pi = new int[Info.TUPLE_NUMBER];
            for (int i = 0; i < dominated_sorted_pi.length; i++)
                dominated_sorted_pi[i] = 0;

            int skip_count = 0;

            for (int i = 0; i < Info.TUPLE_NUMBER; i++)
            {
                //当前元组确定被支配，直接跳过候选集中的该元组
                if (dominated_sorted_pi[i] == 1)
                {
                    long skipnum = Info.TUPLE_AVERAGE_BYTES_LENGTH;
                    while(skipnum > 0)
                        skipnum -= pt_reader.skip(skipnum);

                    //while(skipnum > 0)
                    //   skipnum -= pt_reader.skip(skipnum);

                    skip_count ++;
                }
                else
                {
                    //读候选集
                    while (bytesRead < ptToRead)
                        bytesRead += pt_reader.read(pt_buf, bytesRead,
                                ptToRead - bytesRead);
                    bytesRead = 0;

                    in_out_count ++;

                    Tuple pt_temp = new Tuple();
                    pt_temp.parse_average(pt_buf);

                    //每次要从头读取
                    BufferedInputStream reader = new BufferedInputStream(new
                            FileInputStream(Info.ROOT_PATH +
                            Info.SORTED_AVERAGE_TABLE_PATH));

                    byte[] buf = new byte[Info.TUPLE_AVERAGE_BYTES_LENGTH];
                    int bytesToRead = buf.length;

                    boolean dominated = false;

                    int domi_count = 0;

                    for (int j = 0; j < Info.TUPLE_NUMBER; j++)
                    {
//						domi_count = j;
                        //读比较集
                        while (bytesRead < bytesToRead)
                            bytesRead += reader.read(buf, bytesRead,
                                    bytesToRead - bytesRead);
                        bytesRead = 0;

                        in_out_count ++;

                        Tuple comp_temp = new Tuple();
                        comp_temp.parse_average(buf);

                        //记录两个元组均不为空的属性数
                        int con_count = 0;

                        int dominate = 0;

                        int equal_count = 0;

                        //比较集中的元组与候选集中要比较的元组，不相同
                        if (pt_temp.position_index != comp_temp.position_index)
                        {
                            //判断当前元组是否被支配
                            for (int k = 0;
                                 k < Info.RELATED_ATTRIBUTES_NUMBER; k++)
                            {
                                if ((comp_temp.attributes[k] != Long.MIN_VALUE)
                                        &&(pt_temp.attributes[k] != Long.MIN_VALUE))
                                {
                                    con_count ++;

                                    if (comp_temp.attributes[k]
                                            <= pt_temp.attributes[k])
                                    {
                                        dominate ++;

                                        if (comp_temp.attributes[k]
                                                == pt_temp.attributes[k])
                                        {
                                            equal_count ++;
                                        }
                                    }
                                }

                            }

                            //比较集中的该元组被支配，更新支配表
                            if ((dominate == 0) && (equal_count < con_count))
                            {
                                dominated_sorted_pi[(int)(comp_temp.average_value)]
                                        = 1;

                                domi_count ++;
                            }
                            //候选集里的该元组被支配，结束当前循环
                            else if ((con_count == dominate) &&
                                    (equal_count < con_count))
                            {
//								System.out.println(i + ":" + j);

                                dominated = true;

//								break;
                                if (i >= 3 )
                                    break;


                            }
                        }

                    }

//					if (domi_count != 0)
//					{
//						System.out.println("当前可剪切个数为");
//						System.out.println(domi_count);
//					}

                    if (dominated == false)
                    {

//						System.out.println("======当前元组支配的元组数======" + domi_count);
                        skyline_result ++;
                        System.out.print(pt_temp);
                    }

                    reader.close();
                }
            }

            System.out.println("=============I/O数为：=============");
            System.out.println(in_out_count);

            System.out.println("=============跳过的元组个数有==========");
            System.out.println(skip_count);

            System.out.println("=============skyline结果元组个数有==========");
            System.out.println(skyline_result);
            pt_reader.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public static void main(String[] args)
    {
        long startTime = System.currentTimeMillis();

        MY_RPID mr = new MY_RPID();
        mr.generate_skyline();

        long endTime = System.currentTimeMillis();

        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");

    }
}
