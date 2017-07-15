package src.algrithm;

import src.data_structure.Tuple;
import src.global.Info;

import java.io.*;

/**
 * Created by Xue on 2017/7/15.
 */
public class MY_RPID_Improve_Block
{
    public void generate_skyline()
    {
        try
        {
            //记录cand_list中被支配的信息
            int[] weather_dominated = new int[Info.TUPLE_NUMBER];

            //记录被跳过的元组数
            int skip_count = 0;

            //记录skyline结果数
            int skyline_cout = 0;

            //记录I/O数
            int in_out_count = 0;

            //应划分的块数
            int partition_num = Info.TUPLE_NUMBER / Info.BLOCK_SIZE;

            //每块的BYTES大小
            int block_bytes_length = Info.BLOCK_SIZE * Info.TUPLE_AVERAGE_BYTES_LENGTH;

            //初始化读候选集
            BufferedInputStream cand_reader = new BufferedInputStream(new FileInputStream(
                    Info.ROOT_PATH + Info.TABLE_WITH_SORTED_PI_PATH));

            byte[] cand_buf = new byte[block_bytes_length];
            int candToRead = cand_buf.length;
            int bytesRead = 0;

            //按块读
            for (int i = 0 ; i < partition_num ; i ++)
            {
                //读出一个块
                while (bytesRead < candToRead)
                    bytesRead += cand_reader.read(cand_buf , bytesRead ,
                            candToRead - bytesRead);
                bytesRead = 0;

                //读一次，I/O加一
                in_out_count ++;

                //每个块内做循环
                for (int j = 0 ; j < Info.BLOCK_SIZE ; j ++)
                {
                    //当前元组确定被支配，直接跳过候选集中的该元组
                    if (weather_dominated[i * Info.BLOCK_SIZE + j] == 1)
                        skip_count ++;

                    //当前元组不被支配，开始执行比较
                    else
                    {
                        //解析不被支配的当前元组
                        byte[] cand_temp_buf = new byte[Info.TUPLE_AVERAGE_BYTES_LENGTH];
                        for (int k = 0 ; k < Info.TUPLE_AVERAGE_BYTES_LENGTH ; k ++)
                            cand_temp_buf[k] = cand_buf[j * Info.TUPLE_AVERAGE_BYTES_LENGTH + k];

                        Tuple cand_temp = new Tuple();
                        cand_temp.parse_average(cand_temp_buf);

                        //解析出一个候选集的元组，从头读比较集
                        BufferedInputStream com_reader = new BufferedInputStream(
                                new FileInputStream(Info.ROOT_PATH
                                        + Info.SORTED_AVERAGE_TABLE_PATH));

                        byte[] com_buf = new byte[block_bytes_length];
                        int comToRead = com_buf.length;

                        //当前元组不被支配
                        boolean dominated = false;

                        //按块读取比较集
                        for (int k = 0; k < partition_num ; k ++)
                        {
                            //判断是否跳出当前循环，即早结束
                            boolean is_break = false;

                            //读出比较集中的一个块
                            while (bytesRead < comToRead)
                                bytesRead += com_reader.read(com_buf , bytesRead ,
                                        comToRead - bytesRead);
                            bytesRead = 0;

                            //读一次，则I/O加一
                            in_out_count ++;

                            //开始按块比较
                            for (int m = 0 ; m < Info.BLOCK_SIZE ; m ++)
                            {
                                //用到一个，解析一个
                                byte[] com_temp_buf = new byte[Info.TUPLE_AVERAGE_BYTES_LENGTH];
                                for (int l = 0 ; l < Info.TUPLE_AVERAGE_BYTES_LENGTH ; l ++)
                                    com_temp_buf[l] = com_buf[m * Info.TUPLE_AVERAGE_BYTES_LENGTH + l];

                                Tuple com_temp = new Tuple();
                                com_temp.parse_average(com_temp_buf);

                                //记录两个元组均不为空的属性数
                                int con_count = 0;

                                //记录被com支配的属性数
                                int dominate = 0;

                                //记录两个元组的相等属性数
                                int equal_count = 0;

                                //比较集中的元组与候选集中要比较的元组，不相同
                                if (com_temp.position_index != cand_temp.position_index)
                                {
                                    //判断当前元组是否被支配
                                    for (int l = 0;
                                         l < Info.RELATED_ATTRIBUTES_NUMBER; l++)
                                    {
                                        if ((cand_temp.attributes[l] != Long.MIN_VALUE)
                                                && (com_temp.attributes[l] != Long.MIN_VALUE))
                                        {
                                            con_count++;

                                            if (com_temp.attributes[l]
                                                    <= cand_temp.attributes[l])
                                            {
                                                dominate++;

                                                if (cand_temp.attributes[l]
                                                        == com_temp.attributes[l])
                                                    equal_count++;
                                            }
                                        }

                                    }
                                    //比较集中的该元组被支配，更新支配表
                                    if ((dominate == 0)&&(equal_count < con_count ))
                                    {
                                        weather_dominated[(int)(com_temp.average_value)]= 1;
                                    }
                                    //如果候选集中的元组被支配，则跳出当前读compare_list
                                    if ((con_count == dominate) && (equal_count < con_count))
                                    {
                                        dominated = true;

                                        is_break = true;
                                        m = Info.BLOCK_SIZE;

//                                        if ((i == 0) && (j <= 2))
//                                        {
//                                        }
//                                        else
//                                        {
//                                            is_break = true;
//                                            m = Info.BLOCK_SIZE;
//                                        }
                                    }
                                }
                            }
                            if (is_break == true)
                                k = partition_num;
                        }
                        com_reader.close();

                        if (dominated == false)
                        {
                            skyline_cout ++;
                            System.out.print(cand_temp);
                        }
                    }
                }
            }
            cand_reader.close();

            System.out.println("=============I/O数为：=============");
            System.out.println(in_out_count);

            System.out.println("=============跳过的元组个数有==========");
            System.out.println(skip_count);

            System.out.println("=============skyline结果元组个数有==========");
            System.out.println(skyline_cout);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        long startTime = System.currentTimeMillis();

        MY_RPID_Improve_Block mib = new MY_RPID_Improve_Block();
        mib.generate_skyline();

        long endTime = System.currentTimeMillis();

        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
    }
}
