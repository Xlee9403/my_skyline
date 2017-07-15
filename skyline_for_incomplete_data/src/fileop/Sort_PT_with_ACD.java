package  src.fileop;

import  src.data_structure.Compare_Tuple_Average_Value;
import  src.data_structure.Reverse_Parse;
import  src.data_structure.Tuple;
import  src.global.Info;

import java.io.*;
import java.util.Arrays;

/**
 * Created by Xue on 2017/7/14.
 */
public class Sort_PT_with_ACD
{
    public void sort()
    {
        try
        {
            //-----------------读PT，并按照平均值排序---------------------//
            BufferedInputStream pt_reader = new BufferedInputStream(
                    new FileInputStream(Info.ROOT_PATH
                            + Info.TABLE_AVERAGE_VALUE_PATH));

            byte[] tup_buf = new byte[Info.TUPLE_AVERAGE_BYTES_LENGTH];
            int tupToRead = tup_buf.length;
            int bytesRead = 0;

            BufferedOutputStream pt_writer = new BufferedOutputStream(
                    new FileOutputStream(Info.ROOT_PATH
                            + Info.SORTED_AVERAGE_TABLE_PATH));
            //////////////////-----------------------//////////////////

            //-----------若内存可以放下，则直接读入内存排序后写回--------------//
            if (Info.TUPLE_AVERAGE_BYTES_LENGTH * Info.TUPLE_NUMBER
                    <= Info.ALLOCATED_MEMEORY_SIZE)
            {
                System.out.println("========可以直接放入内存排序=======");

                //存放列的数组
                Tuple[] sort_arr = new Tuple[Info.TUPLE_NUMBER];
                for (int j = 0; j < sort_arr.length; j++)
                    sort_arr[j] = new Tuple();

                //将整个列文件读入内存
                for (int j = 0; j < sort_arr.length; j++)
                {
                    while (bytesRead < tupToRead)
                        bytesRead += pt_reader.read(tup_buf,
                                bytesRead, tupToRead - bytesRead);
                    bytesRead = 0;

                    Tuple temp = new Tuple();
                    temp.parse_average(tup_buf);

//					System.out.println(temp);

                    sort_arr[j].copyfrom(temp);
                }

                //对列文件，按平均值排序
                Compare_Tuple_Average_Value cta =
                        new Compare_Tuple_Average_Value();
                Arrays.sort(sort_arr, cta);

                //将排好序的属性列写回
                for (int j = 0; j < sort_arr.length; j++)
                {
                    sort_arr[j].average_value = (long)j;

                    Reverse_Parse rp = new Reverse_Parse();
                    tup_buf = rp.rev_parse_tuple_average(sort_arr[j]);

//					System.out.println(sort_arr[j]);

                    pt_writer.write(tup_buf);

                }

                pt_writer.flush();
                pt_writer.close();
            }
            //////////////////--------------------------////////////////

            //-----------若内存，放不下，考虑要划分成几块，然后归并------------//
            else
            {
                System.out.println("========不可直接放入内存排序=======");

                byte[] sub_tup_buf =
                        new byte[Info.TUPLE_AVERAGE_SUB_BYTES_LENGTH];
                int subToRead = sub_tup_buf.length;

                //-----------------计算应划分的块数--------------------//
                int partition_num;
                int table_size = Info.TUPLE_AVERAGE_BYTES_LENGTH
                        * Info.TUPLE_NUMBER;
                if (table_size % Info.ALLOCATED_MEMEORY_SIZE == 0)
                    partition_num =
                            table_size / Info.ALLOCATED_MEMEORY_SIZE;
                else
                    partition_num = 1 +
                            table_size / Info.ALLOCATED_MEMEORY_SIZE;
                //////////////////-------------------/////////////////

                //-------------------计算每块的元组数--------------------//
                int[] par_num_per_block = new int[partition_num];
                for (int j = 0; j < partition_num; j++)
                {
                    if (j == (partition_num - 1))
                        par_num_per_block[j] = Info.TUPLE_NUMBER -
                                (Info.TUPLE_NUMBER/partition_num) * j;

                    else
                        par_num_per_block[j] =
                                Info.TUPLE_NUMBER/partition_num;
                }
                //////////////////------------------------///////////////

                BufferedOutputStream[] sub_writer =
                        new BufferedOutputStream[partition_num];

                //------------------按块读出，排序后写入子表----------------//
                for (int j = 0; j < partition_num; j++)
                {
                    sub_writer[j] = new BufferedOutputStream(new
                            FileOutputStream(Info.ROOT_PATH
                            + Info.SUB_AVERAGE_PATH + j + Info.extension));

                    Tuple[] sub_arr = new Tuple[par_num_per_block[j]];
                    for (int k = 0; k < sub_arr.length; k++)
                        sub_arr[k] = new Tuple();

                    //按块大小读出
                    for (int k = 0; k < par_num_per_block[j]; k++)
                    {
                        while (bytesRead < tupToRead)
                            bytesRead += pt_reader.read(tup_buf,
                                    bytesRead, tupToRead - bytesRead);
                        bytesRead = 0;

                        Tuple temp = new Tuple();
                        temp.parse_average(tup_buf);

                        sub_arr[k].copyfrom(temp);
                    }

                    Compare_Tuple_Average_Value cta =
                            new Compare_Tuple_Average_Value();
                    Arrays.sort(sub_arr, cta);

                    for (int k = 0; k < sub_arr.length; k++)
                    {
                        sub_arr[k].sub_index = j;

                        Reverse_Parse rp = new Reverse_Parse();
                        sub_tup_buf =
                                rp.rev_parse_tuple_sub_average(sub_arr[k]);

                        sub_writer[j].write(sub_tup_buf);
                    }

                    sub_writer[j].flush();
                    sub_writer[j].close();

                }
                ////////////////------------------------////////////////

                //--------------------归并子表，得到有序列------------------//
                BufferedInputStream[] sub_reader =
                        new BufferedInputStream[partition_num];
                for (int j = 0; j < sub_reader.length; j++)
                    sub_reader[j] = new BufferedInputStream(new
                            FileInputStream(Info.ROOT_PATH +
                            Info.SUB_AVERAGE_PATH +
                            j + Info.extension));

                Tuple[] min_subtable = new Tuple[partition_num];
                for (int j = 0; j < min_subtable.length; j++)
                    min_subtable[j] = new Tuple();

                //从每个子表中读出一个
                for (int j = 0; j < min_subtable.length; j++)
                {
                    while (bytesRead < subToRead)
                        bytesRead += sub_reader[j].read(sub_tup_buf,
                                bytesRead, subToRead - bytesRead);
                    bytesRead = 0;

                    par_num_per_block[j] --;

                    Tuple temp = new Tuple();
                    temp.parse_sub_average(sub_tup_buf);

                    min_subtable[j].copyfrom(temp);
                }

                for (int j = 0; j < Info.TUPLE_NUMBER; j++)
                {
                    Compare_Tuple_Average_Value cta =
                            new Compare_Tuple_Average_Value();
                    Arrays.sort(min_subtable, cta);

                    min_subtable[0].average_value = (long)j;

                    Reverse_Parse rp = new Reverse_Parse();
                    tup_buf = rp.rev_parse_tuple_average(min_subtable[0]);

                    pt_writer.write(tup_buf);

                    int read_next = (int) min_subtable[0].sub_index;

                    //找当前最小值所在的子表中读取一个，若该子表为空，则置为最大值
                    if (par_num_per_block[read_next] != 0)
                    {
                        while (bytesRead < subToRead)
                            bytesRead += sub_reader[read_next].read(
                                    sub_tup_buf,bytesRead,
                                    subToRead - bytesRead);
                        bytesRead = 0;

                        par_num_per_block[read_next] --;

                        Tuple temp = new Tuple();
                        temp.parse_sub_average(sub_tup_buf);

                        min_subtable[0].copyfrom(temp);
                    }
                    else
                        min_subtable[0].average_value = Long.MAX_VALUE;
                }

                for (int k = 0; k < partition_num; k++)
                    sub_reader[k].close();

                ////////////////------------------------///////////

            }
            //////////////////-----------------------------//////////
            pt_writer.flush();
            pt_writer.close();

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

        Sort_PT_with_ACD spa = new Sort_PT_with_ACD();
        spa.sort();

        long endTime = System.currentTimeMillis();

        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");

    }
}
