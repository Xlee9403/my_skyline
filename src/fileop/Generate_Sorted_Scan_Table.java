package fileop;

import global.Info;

import java.io.*;
import java.util.Arrays;

import datastructure.Compare_MPI;
import datastructure.Reverse_Parse;
import datastructure.Tuple;

public class Generate_Sorted_Scan_Table {
    //生成有序子表
    public void generate_sub_table() {
        try {
            /////-----------------将子表的元组个数写入文件------------------//////////
            //开始写
            BufferedOutputStream writer_length =
                    new BufferedOutputStream(new FileOutputStream(
                            Info.ROOT_PATH + Info.SUB_SCAN_TABLE_LENGTH_PATH));

            //每个子表最多可容纳的元组数
            int sub_num_per_table =
                    Info.ALLOCATED_MEMEORY_SIZE / Info.TUPLE_BYTES_LENGTH;

            //子表的个数
            int sub_table_number;
            if (Info.TUPLE_NUMBER % sub_num_per_table == 0)
                sub_table_number = Info.TUPLE_NUMBER / sub_num_per_table;
            else
                sub_table_number = Info.TUPLE_NUMBER / sub_num_per_table + 1;

            //将子表个数，写入子表长度文件
            byte[] len_buf = new byte[Info.ATTRIBUTE_LENGTH];

            len_buf[0] = (byte) (0xff & ((long) (sub_table_number) >> 56));
            len_buf[1] = (byte) (0xff & ((long) (sub_table_number) >> 48));
            len_buf[2] = (byte) (0xff & ((long) (sub_table_number) >> 40));
            len_buf[3] = (byte) (0xff & ((long) (sub_table_number) >> 32));
            len_buf[4] = (byte) (0xff & ((long) (sub_table_number) >> 24));
            len_buf[5] = (byte) (0xff & ((long) (sub_table_number) >> 16));
            len_buf[6] = (byte) (0xff & ((long) (sub_table_number) >> 8));
            len_buf[7] = (byte) (0xff & (long) (sub_table_number));

            writer_length.write(len_buf);

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

                len_buf[0] = (byte) (0xff & ((long) (sub_tuple_number_arr[i]) >> 56));
                len_buf[1] = (byte) (0xff & ((long) (sub_tuple_number_arr[i]) >> 48));
                len_buf[2] = (byte) (0xff & ((long) (sub_tuple_number_arr[i]) >> 40));
                len_buf[3] = (byte) (0xff & ((long) (sub_tuple_number_arr[i]) >> 32));
                len_buf[4] = (byte) (0xff & ((long) (sub_tuple_number_arr[i]) >> 24));
                len_buf[5] = (byte) (0xff & ((long) (sub_tuple_number_arr[i]) >> 16));
                len_buf[6] = (byte) (0xff & ((long) (sub_tuple_number_arr[i]) >> 8));
                len_buf[7] = (byte) (0xff & (long) (sub_tuple_number_arr[i]));

                writer_length.write(len_buf);
                System.out.println(sub_tuple_number_arr[i]);
            }
            writer_length.flush();
            writer_length.close();
            //写入完成
            /////////////------------------------------------------///////////////

            //---------------将扫描表划分成几个子表，排序后写入文件--------------------//
            //开始
            //读初始扫描表缓冲区
            BufferedInputStream reader_scan_table =
                    new BufferedInputStream(new FileInputStream
                            (Info.ROOT_PATH + Info.INITIAL_SCAN_TABLE_PATH));

            byte[] read_buf = new byte[Info.TUPLE_BYTES_LENGTH];

            int bytestoread = read_buf.length;
            int bytesread = 0;

            //写排好序的子表缓冲区
            BufferedOutputStream[] writer_sub = new BufferedOutputStream[sub_table_number];
            for (int i = 0; i < writer_sub.length; i++)
                writer_sub[i] = new BufferedOutputStream(
                        new FileOutputStream(Info.ROOT_PATH + Info.SUB_SCAN_ROOT
                                + Info.SUB_SCAN_TABLE_PATH + i + Info.extension));

            byte[] writer_buf = new byte[Info.TUPLE_BYTES_LENGTH];

            //按子表中的元组个数，读出各个子表对应的元组数，排序后写入子表
            for (int i = 0; i < sub_table_number; i++) {
                System.out.println(i + ":" + sub_tuple_number_arr[i]);
                //存放当前子表的数组
                Tuple[] arr = new Tuple[sub_tuple_number_arr[i]];
                for (int j = 0; j < arr.length; j++)
                    arr[j] = new Tuple();

                for (int j = 0; j < sub_tuple_number_arr[i]; j++) {
                    while (bytesread < bytestoread)
                        bytesread +=
                                reader_scan_table.read(read_buf, bytesread, bytestoread - bytesread);
                    bytesread = 0;

                    Tuple temp = new Tuple();
                    temp.parse_tuple(read_buf);

                    arr[j].copyfrom(temp);
                }

                Compare_MPI cm = new Compare_MPI();
                Arrays.sort(arr, cm);

                //写入子表
                for (int j = 0; j < arr.length; j++) {
                    byte[] index_buf = new byte[Info.ATTRIBUTE_LENGTH];

                    //标记属于哪个子表
                    long sub_index = i;
                    index_buf[0] = (byte) (0xff & (sub_index >> 56));
                    index_buf[1] = (byte) (0xff & (sub_index >> 48));
                    index_buf[2] = (byte) (0xff & (sub_index >> 40));
                    index_buf[3] = (byte) (0xff & (sub_index >> 32));
                    index_buf[4] = (byte) (0xff & (sub_index >> 24));
                    index_buf[5] = (byte) (0xff & (sub_index >> 16));
                    index_buf[6] = (byte) (0xff & (sub_index >> 8));
                    index_buf[7] = (byte) (0xff & (sub_index));

                    writer_sub[i].write(index_buf);

                    Reverse_Parse rp = new Reverse_Parse();
                    writer_buf = rp.rev_parse_tuple(arr[j]);

                    writer_sub[i].write(writer_buf);

                }
                writer_sub[i].flush();
                writer_sub[i].close();
            }

            reader_scan_table.close();
            //结束
            /////////////------------------------------------/////////////////
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //归并子表，形成有序大表
    public void merge_sub_table() {
        try {
            //--------------读sub_length表，确定子表个数，及每个子表所含元组数-----------//
            //开始读
            BufferedInputStream reader_length =
                    new BufferedInputStream(new FileInputStream(
                            Info.ROOT_PATH + Info.SUB_SCAN_TABLE_LENGTH_PATH));

            byte[] sub_len = new byte[Info.ATTRIBUTE_LENGTH];

            int sub_lentoread = sub_len.length;
            int sub_lenread = 0;

            while (sub_lenread < sub_lentoread)
                sub_lenread += reader_length.read(sub_len, sub_lenread, sub_lentoread - sub_lenread);
            sub_lenread = 0;

            long val =
                    (((long) (sub_len[0] & 0xff) << 56) |
                            ((long) (sub_len[1] & 0xff) << 48) |
                            ((long) (sub_len[2] & 0xff) << 40) |
                            ((long) (sub_len[3] & 0xff) << 32) |
                            ((long) (sub_len[4] & 0xff) << 24) |
                            ((long) (sub_len[5] & 0xff) << 16) |
                            ((long) (sub_len[6] & 0xff) << 8) |
                            ((long) (sub_len[7] & 0xff)));

            //子表个数
            int sub_table_num = (int) (val);

            //子表元组个数数组
            int[] sub_table_len = new int[sub_table_num];
            for (int i = 0; i < sub_table_num; i++) {
                while (sub_lenread < sub_lentoread)
                    sub_lenread += reader_length.read(sub_len, sub_lenread, sub_lentoread - sub_lenread);
                sub_lenread = 0;

                val =
                        (((long) (sub_len[0] & 0xff) << 56) |
                                ((long) (sub_len[1] & 0xff) << 48) |
                                ((long) (sub_len[2] & 0xff) << 40) |
                                ((long) (sub_len[3] & 0xff) << 32) |
                                ((long) (sub_len[4] & 0xff) << 24) |
                                ((long) (sub_len[5] & 0xff) << 16) |
                                ((long) (sub_len[6] & 0xff) << 8) |
                                ((long) (sub_len[7] & 0xff)));

                sub_table_len[i] = (int) (val);
//				System.out.println(sub_table_len[i]);				
            }
            reader_length.close();
            //结束
            /////////////-------------------------------------///////////////////

            //-----------------------归并子表，形成有序大表-------------------------//
            //开始归并
            BufferedOutputStream writer = new BufferedOutputStream(
                    new FileOutputStream(Info.ROOT_PATH + Info.SORTED_SCAN_TABLE));

            byte[] write_buf = new byte[Info.TUPLE_BYTES_LENGTH];

            BufferedInputStream[] sub_reader = new BufferedInputStream[sub_table_num];
            for (int i = 0; i < sub_reader.length; i++)
                sub_reader[i] = new BufferedInputStream(new FileInputStream(
                        Info.ROOT_PATH + Info.SUB_SCAN_ROOT
                                + Info.SUB_SCAN_TABLE_PATH + i + Info.extension));

            byte[] sub_buf = new byte[Info.TUPLE_SUB_BYTES_LENGTH];

            int sub_buftoread = sub_buf.length;
            int sub_bufread = 0;

            //归并数组
            Tuple[] merge_arr = new Tuple[sub_table_num];

            //每个子表内读出一个，放到merge_arr中
            for (int i = 0; i < merge_arr.length; i++) {
                merge_arr[i] = new Tuple();

                while (sub_bufread < sub_buftoread)
                    sub_bufread += sub_reader[i].read(
                            sub_buf, sub_bufread, sub_buftoread - sub_bufread);
                sub_bufread = 0;

                Tuple temp = new Tuple();
                temp.parse_sub_tuple(sub_buf);

                merge_arr[i].copyfrom(temp);
                sub_table_len[i]--;
            }

            System.out.println("将scan_table按MPI值排序：");
            for (int i = 0; i < Info.TUPLE_NUMBER; i++) {
                //按MPI值升序排序
                Compare_MPI cm = new Compare_MPI();
                Arrays.sort(merge_arr, cm);

                if (i % 1000000 == 0)
                    System.out.println(i + ":" + Info.TUPLE_NUMBER);

                //将当前MPI值最小元组，写入大表
                Reverse_Parse rp = new Reverse_Parse();
                write_buf = rp.rev_parse_tuple(merge_arr[0]);

                if (merge_arr[0].MPI > Info.TUPLE_NUMBER) {
                    System.out.println("wrong");
                    System.out.println(i + ":" + merge_arr[0].MPI);

                }
                writer.write(write_buf);

                int index = (int) (merge_arr[0].sub_index);

                //若当前MPI最小元组所在的子表不空，读出下一个元组
                //否则，将MPI值置为MAX
                if (sub_table_len[index] != 0) {
                    //读子表
                    while (sub_bufread < sub_buftoread)
                        sub_bufread += sub_reader[index].read(sub_buf, sub_bufread, sub_buftoread - sub_bufread);
                    sub_bufread = 0;

                    Tuple temp = new Tuple();
                    temp.parse_sub_tuple(sub_buf);

                    merge_arr[0].copyfrom(temp);
                    sub_table_len[index]--;
                } else
                    merge_arr[0].MPI = Long.MAX_VALUE;

//				Arrays.sort(merge_arr, cm);
            }

            writer.flush();
            writer.close();

            for (int i = 0; i < merge_arr.length; i++)
                sub_reader[i].close();

            //结束归并
            ////////////--------------------------------------///////////////////

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
