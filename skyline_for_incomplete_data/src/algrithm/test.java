package algrithm;

import data_structure.Tuple;
import global.Info;

import java.io.*;

/**
 * Created by Xue on 2017/7/14.
 */
public class test
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

            byte[] pt_buf = new byte[Info.BLOCK_SIZE * Info.TUPLE_AVERAGE_BYTES_LENGTH] ;
            int ptToRead = pt_buf.length;
            int bytesRead = 0;
            //应划分的块数
            int partition_num = Info.TUPLE_NUMBER / Info.BLOCK_SIZE;

            int[] dominated_sorted_pi = new int[Info.TUPLE_NUMBER];
            for (int i = 0; i < dominated_sorted_pi.length; i++)
                dominated_sorted_pi[i] = 0;

            int skip_count = 0;

            for (int i = 0 ; i < partition_num; i ++)
            {
                //读候选集
                while (bytesRead < ptToRead)
                    bytesRead += pt_reader.read(pt_buf, bytesRead,
                            ptToRead - bytesRead);
                bytesRead = 0;

                in_out_count ++;

                for (int j = 0; j < Info.BLOCK_SIZE ; j ++)
                {
                    //当前元组确定被支配，直接跳过候选集中的该元组
                    if (dominated_sorted_pi[i * Info.BLOCK_SIZE + j] == 1)
                        skip_count ++;

                        //不被支配，则解析
                    else
                    {
                        Tuple temp = new Tuple();
                        byte[] temp_buf = new byte[Info.TUPLE_AVERAGE_BYTES_LENGTH];

                        //将要解析的部分赋值给temp_buf
                        for (int k = 0 ; k < Info.TUPLE_AVERAGE_BYTES_LENGTH ; k ++)
                            temp_buf[k] = pt_buf[j * Info.TUPLE_AVERAGE_BYTES_LENGTH + k];

                        temp.parse_average(temp_buf);

                        //读取比较集，每次要从头读取
                        BufferedInputStream reader = new BufferedInputStream(new
                                FileInputStream(Info.ROOT_PATH +
                                Info.SORTED_AVERAGE_TABLE_PATH));

                        //不早结束的点，按块读取
                        if ((i == 0) && (j <= 2))
                        {
                            int hava_commen_sub = 0;
//							int should_skip = 0;
                            for (int m = 0; m < partition_num; m++)
                            {
                                byte[] comp_buf = new byte[Info.BLOCK_SIZE
                                        * Info.TUPLE_AVERAGE_BYTES_LENGTH];
                                int compToRead = comp_buf.length;

                                while (bytesRead < compToRead)
                                    bytesRead += reader.read(comp_buf, bytesRead,
                                            compToRead - bytesRead);
                                bytesRead = 0;

                                in_out_count++;

                                for (int n = 0; n < Info.BLOCK_SIZE; n++)
                                {

                                    Tuple t_temp = new Tuple();
                                    byte[] t_buf = new byte[Info.TUPLE_AVERAGE_BYTES_LENGTH];

                                    for (int k = 0; k < Info.TUPLE_AVERAGE_BYTES_LENGTH; k++)
                                        t_buf[k] =
                                                comp_buf[n * Info.TUPLE_AVERAGE_BYTES_LENGTH + k];

                                    t_temp.parse_average(t_buf);

                                    //记录两个元组均不为空的属性数
                                    int con_count = 0;

                                    int dominate = 0;

                                    int equal_count = 0;

                                    //比较集中的元组与候选集中要比较的元组，不相同
                                    if (temp.position_index != t_temp.position_index)
                                    {
                                        //判断当前元组是否被支配
                                        for (int l = 0;
                                             l < Info.RELATED_ATTRIBUTES_NUMBER; l++)
                                        {
                                            if ((t_temp.attributes[l] != Long.MIN_VALUE)
                                                    && (temp.attributes[l] != Long.MIN_VALUE))
                                            {
                                                con_count++;

                                                if (t_temp.attributes[l]
                                                        <= temp.attributes[l])
                                                {
                                                    dominate++;

                                                    if (t_temp.attributes[l]
                                                            == temp.attributes[l])
                                                        equal_count++;
                                                }
                                            }

                                        }
                                        //比较集中的该元组被支配，更新支配表
                                        if ((dominate == 0)&&(equal_count < con_count ))
                                        {
//											should_skip ++;
                                            dominated_sorted_pi[(int)(t_temp.average_value)]= 1;
                                        }
                                    }
                                    if (con_count != 0)
                                    {
                                        hava_commen_sub ++;
                                    }
                                }
                            }
                            reader.close();
//							System.out.println(should_skip);
                            System.out.println("可比较的元组数为" + hava_commen_sub);
                        }
                        else
                        {
                            byte[] buf = new byte[Info.TUPLE_AVERAGE_BYTES_LENGTH];
                            int bytesToRead = buf.length;

                            boolean dominated = false;

                            for (int k = 0; k < Info.TUPLE_NUMBER; k++)
                            {
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
                                if (temp.position_index != comp_temp.position_index)
                                {
                                    //判断当前元组是否被支配
                                    for (int l = 0;
                                         l < Info.RELATED_ATTRIBUTES_NUMBER; l++)
                                    {
                                        if ((comp_temp.attributes[l] != Long.MIN_VALUE)
                                                &&(temp.attributes[l] != Long.MIN_VALUE))
                                        {
                                            con_count ++;

                                            if (comp_temp.attributes[l]
                                                    <= temp.attributes[l])
                                            {
                                                dominate ++;

                                                if (comp_temp.attributes[l]
                                                        == temp.attributes[l])
                                                {
                                                    equal_count ++;
                                                }
                                            }
                                        }

                                    }
                                    //比较集中的该元组被支配，更新支配表
                                    if ((dominate == 0) && (equal_count < con_count))
                                        dominated_sorted_pi[(int)(comp_temp.average_value)]
                                                = 1;
                                        //候选集里的该元组被支配，结束当前循环
                                    else if ((con_count == dominate) &&
                                            (equal_count < con_count))
                                    {
                                        dominated = true;

                                        break;
                                    }
                                }
                            }
                            if (dominated == false)
                            {
                                skyline_result ++;
                                System.out.print(temp);
                            }

                            reader.close();
                        }
                    }
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

        test tt = new test();
        tt.generate_skyline();

        long endTime = System.currentTimeMillis();

        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");


    }
}
