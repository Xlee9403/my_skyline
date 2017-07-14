package fileop;

import global.Info;

import java.io.*;
import java.util.Arrays;

import datastructure.Column;
import datastructure.Compare_Column_Value;
import datastructure.Compare_Table_Position;
import datastructure.Reverse_Parse;

public class Sort_Measure_Column {
    //计算子表个数，确定是否需要归并
    public int is_division() {
        //每个子表最多可容纳的元组数
        int sub_num_per_table =
                Info.ALLOCATED_MEMEORY_SIZE / Info.COLUNME_BYTE_LENGTH;

        //子表的个数
        int sub_table_number;
        if (Info.TUPLE_NUMBER % sub_num_per_table == 0)
            sub_table_number = Info.TUPLE_NUMBER / sub_num_per_table;
        else
            sub_table_number = Info.TUPLE_NUMBER / sub_num_per_table + 1;

        return sub_table_number;
    }

    //计算每个子表的长度
    public int[] sub_table_lenth(int sub_table_number) {
        //平均每个子表中的元组数
        int average_sub_tuple_number = Info.TUPLE_NUMBER / sub_table_number;

        //保存每个子表元组数的数组
        int[] sub_tuple_number_arr = new int[sub_table_number];

        System.out.println("每个子表所含元组数:");
        for (int i = 0; i < sub_tuple_number_arr.length; i++) {
            if (i != sub_table_number - 1)
                sub_tuple_number_arr[i] = average_sub_tuple_number;
            else
                sub_tuple_number_arr[sub_table_number - 1] = Info.TUPLE_NUMBER
                        - ((sub_table_number - 1) * average_sub_tuple_number);

            System.out.println(sub_tuple_number_arr[i]);
        }

        return sub_tuple_number_arr;
    }

    //----------------------不需要归并---------------------------//
    //按照度量属性值排序，写回时，写回column_position和table_position
    public void sort_mesaure_value() {
        try {
            //读度量属性
            BufferedInputStream[] reader =
                    new BufferedInputStream[Info.MEASURE_ATTRIBUTE_NUMBER];

            BufferedOutputStream[] writer =
                    new BufferedOutputStream[Info.MEASURE_ATTRIBUTE_NUMBER];

            byte[] buf = new byte[Info.COLUNME_BYTE_LENGTH];

            int bytestoread = buf.length;
            int bytesread = 0;

            //存放度量属性数组
            Column[] arr = new Column[Info.TUPLE_NUMBER];
            for (int i = 0; i < arr.length; i++)
                arr[i] = new Column();

            System.out.println("按度量属性排序：");
            //读取每列度量属性，排序后写回
            for (int i = 0; i < Info.MEASURE_ATTRIBUTE_NUMBER; i++) {
                reader[i] = new BufferedInputStream(new FileInputStream(
                        Info.ROOT_PATH + Info.MEASURE_COLUMN_ROOT +
                                Info.MEASURE_COLUNME_PATH + i + Info.extension));

//				System.out.println("未排序前：");
                //读取第i列度量属性，放入arr中
                for (int j = 0; j < Info.TUPLE_NUMBER; j++) {
                    if (j % 1000000 == 0)
                        System.out.println(i + ":" + j);

                    while (bytesread < bytestoread)
                        bytesread += reader[i].read(buf, bytesread, bytestoread - bytesread);
                    bytesread = 0;

                    Column temp = new Column();
                    temp.parse(buf);

                    arr[j].copyfrom(temp);
                }
                writer[i] = new BufferedOutputStream(
                        new FileOutputStream(Info.ROOT_PATH
                                + Info.MEASURE_COLUMN_ROOT +
                                Info.SORTED_MEASURE_COLUMN_PATH
                                + i + Info.extension));

                Compare_Column_Value ccv = new Compare_Column_Value();

                //将属性列升序排列
                Arrays.sort(arr, ccv);

                for (int j = 0; j < arr.length; j++) {
                    //将排好序的属性值用当前位置索引代替
                    arr[j].column_value = (long) j;
                }

                Compare_Table_Position ctp = new Compare_Table_Position();

                //按原表索引值升序排序
                Arrays.sort(arr, ctp);

                //将排好序的arr写回，写回时
                for (int j = 0; j < arr.length; j++) {

                    byte[] write_buf = new byte[Info.COLUNME_BYTE_LENGTH];

                    Reverse_Parse rp = new Reverse_Parse();
                    write_buf = rp.rev_parse_colume(arr[j]);

                    writer[i].write(write_buf);

                }
                writer[i].flush();
                writer[i].close();

                reader[i].close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //////////----------------------------需要归并-------------------------///////
    //生成有序子表
    public void generate_sub_table(int sub_table_number, int[] sub_tuple_number_arr) {
        try {
            //---------------将度量属性列划分成几个子表，排序后写入文件--------------------//
            //开始
            //读初始各个度量属性列缓冲区
            BufferedInputStream[] column_reader =
                    new BufferedInputStream[Info.MEASURE_ATTRIBUTE_NUMBER];

            byte[] read_buf = new byte[Info.COLUNME_BYTE_LENGTH];

            int bytestoread = read_buf.length;
            int bytesread = 0;

            //i个列文件，每个列文件j个子表，每个子表的长度为sub_tuple_number_arr[j]
            for (int i = 0; i < Info.MEASURE_ATTRIBUTE_NUMBER; i++) {
                //初始化读缓冲区
                column_reader[i] = new BufferedInputStream(
                        new FileInputStream(Info.ROOT_PATH +
                                Info.MEASURE_COLUMN_ROOT +
                                Info.MEASURE_COLUNME_PATH
                                + i + Info.extension));

                //写排好序的子表缓冲区
                BufferedOutputStream[] writer_sub =
                        new BufferedOutputStream[sub_table_number];
                for (int j = 0; j < writer_sub.length; j++)
                    writer_sub[j] = new BufferedOutputStream(
                            new FileOutputStream(Info.ROOT_PATH
                                    + Info.MEASURE_COLUMN_ROOT + Info.SUB_PATH
                                    + Info.MEASURE_COLUNME_PATH + i
                                    + Info.intervel + j + Info.extension));

                byte[] writer_buf = new byte[Info.COLUNME_BYTE_LENGTH];

                //按子表中的元组个数，读出各个子表对应的元组数，排序后写入子表
                for (int j = 0; j < sub_table_number; j++) {
                    System.out.println(i + ":" + j + ":" + sub_tuple_number_arr[j]);

                    //存放当前子表的数组
                    Column[] arr = new Column[sub_tuple_number_arr[j]];
                    for (int k = 0; k < arr.length; k++)
                        arr[k] = new Column();

                    //按子表应有的元组个数读取
                    for (int k = 0; k < sub_tuple_number_arr[j]; k++) {
                        while (bytesread < bytestoread)
                            bytesread +=
                                    column_reader[i].read(read_buf, bytesread, bytestoread - bytesread);
                        bytesread = 0;

                        Column temp = new Column();
                        temp.parse(read_buf);

                        arr[k].copyfrom(temp);
                        ;
                    }
                    //按度量属性值排序
                    Compare_Column_Value ccv = new Compare_Column_Value();
                    Arrays.sort(arr, ccv);

                    //写入子表
                    for (int k = 0; k < arr.length; k++) {
                        byte[] index_buf = new byte[Info.ATTRIBUTE_LENGTH];

                        //标记属于哪个子表
                        long sub_index = (long) j;
                        index_buf[0] = (byte) (0xff & (sub_index >> 56));
                        index_buf[1] = (byte) (0xff & (sub_index >> 48));
                        index_buf[2] = (byte) (0xff & (sub_index >> 40));
                        index_buf[3] = (byte) (0xff & (sub_index >> 32));
                        index_buf[4] = (byte) (0xff & (sub_index >> 24));
                        index_buf[5] = (byte) (0xff & (sub_index >> 16));
                        index_buf[6] = (byte) (0xff & (sub_index >> 8));
                        index_buf[7] = (byte) (0xff & (sub_index));

                        writer_sub[j].write(index_buf);

                        Reverse_Parse rp = new Reverse_Parse();
                        writer_buf = rp.rev_parse_colume(arr[k]);

                        writer_sub[j].write(writer_buf);
                    }


                }

                for (int j = 0; j < sub_table_number; j++) {
                    writer_sub[j].flush();
                    writer_sub[j].close();
                }

                //结束
                column_reader[i].close();
            }
            /////////////------------------------------------/////////////////

            System.out.println("============成功形成有序子表============");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //归并子表，形成有序大表
    public void merge_sub_table(int sub_table_number, int[] sub_tuple_number_arr) {
        try {
            //-----------------------归并子表，形成有序大表-------------------------//
            //开始归并
            BufferedOutputStream[] column_writer =
                    new BufferedOutputStream[Info.MEASURE_ATTRIBUTE_NUMBER];

            //每个列属性都要归并
            for (int i = 0; i < column_writer.length; i++) {
                //初始化每个子表的长度
                int[] sub_tuple_length = new int[sub_table_number];
                for (int k = 0; k < sub_tuple_length.length; k++) {
                    sub_tuple_length[k] = sub_tuple_number_arr[k];
                }

                //初始化当前要读的列
                column_writer[i] = new BufferedOutputStream(
                        new FileOutputStream(Info.ROOT_PATH
                                + Info.MEASURE_COLUMN_ROOT +
                                Info.TEMP_MEASURE_COLUMN_PATH
                                + i + Info.extension));

                byte[] write_buf = new byte[Info.COLUNME_BYTE_LENGTH];

                //读取对应的子表
                BufferedInputStream[] sub_reader =
                        new BufferedInputStream[sub_table_number];
                for (int j = 0; j < sub_reader.length; j++)
                    sub_reader[j] = new BufferedInputStream(
                            new FileInputStream(Info.ROOT_PATH
                                    + Info.MEASURE_COLUMN_ROOT + Info.SUB_PATH
                                    + Info.MEASURE_COLUNME_PATH +
                                    i + Info.intervel + j + Info.extension));

                byte[] sub_buf = new byte[Info.COLUMN_SUB_BYTE_LENGTH];

                int sub_buftoread = sub_buf.length;
                int sub_bufread = 0;

                //归并数组
                Column[] merge_arr = new Column[sub_table_number];

                //每个子表内读出一个，放到merge_arr中
                for (int j = 0; j < sub_table_number; j++) {
                    merge_arr[j] = new Column();

                    while (sub_bufread < sub_buftoread)
                        sub_bufread += sub_reader[j].read(
                                sub_buf, sub_bufread, sub_buftoread - sub_bufread);
                    sub_bufread = 0;

                    Column temp = new Column();
                    temp.parse_sub_column(sub_buf);

                    merge_arr[j].copyfrom(temp);
                    sub_tuple_length[j]--;
                }

                System.out.println("将sub_table按measure_attribute值归并排序：");

                for (int j = 0; j < Info.TUPLE_NUMBER; j++) {
                    //按measure_attribute值升序排序
                    Compare_Column_Value ccv = new Compare_Column_Value();
                    Arrays.sort(merge_arr, ccv);

                    if (j % 1000000 == 0)
                        System.out.println(i + ":" + j + ":" + Info.TUPLE_NUMBER);

                    //------------将当前measure_attribute值最小元组，写入-------------//
                    //写入时，用列文件的位置索引代替度量属性值
                    long column_index = (long) j;
                    merge_arr[0].column_value = column_index;

                    Reverse_Parse rp = new Reverse_Parse();
                    write_buf = rp.rev_parse_colume(merge_arr[0]);

                    column_writer[i].write(write_buf);
                    /////////////////////-----------------------//////////////////

                    int index = (int) (merge_arr[0].sub_index);

                    //若当前measure_attribute最小元组所在的子表不空，读出下一个元组
                    //否则，将measure_attribute值置为MAX
                    if (sub_tuple_length[index] != 0) {
                        //读子表
                        while (sub_bufread < sub_buftoread)
                            sub_bufread += sub_reader[index].read(sub_buf, sub_bufread, sub_buftoread - sub_bufread);
                        sub_bufread = 0;

                        Column temp = new Column();
                        temp.parse_sub_column(sub_buf);

                        merge_arr[0].copyfrom(temp);
                        sub_tuple_length[index]--;

                    } else
                        merge_arr[0].column_value = Long.MAX_VALUE;
                }

                column_writer[i].flush();
                column_writer[i].close();

                for (int k = 0; k < merge_arr.length; k++)
                    sub_reader[k].close();
                //结束归并
                ////////////--------------------------------------///////////////////
            }

            System.out.println("=============归并成功==============");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        Sort_Measure_Column smc = new Sort_Measure_Column();

        int sub_table_number = smc.is_division();

        if (sub_table_number == 1) {
            smc.sort_mesaure_value();
        } else {
            int[] sub_tuple_number_arr =
                    smc.sub_table_lenth(sub_table_number);

            smc.generate_sub_table(sub_table_number, sub_tuple_number_arr);
            smc.merge_sub_table(sub_table_number, sub_tuple_number_arr);
        }

    }

}
