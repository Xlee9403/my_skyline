package algo;

import java.io.*;
import java.util.Arrays;

import fileop.Generate_Predicate;
import global.Info;
import datastructure.Compare_Entropy;
import datastructure.Interval;
import datastructure.Overflow_Array;
import datastructure.Reverse_Parse;
import datastructure.LESS_Tuple;;

public class LESS {
    int range_number = 0;

    public void less_skyline() {
        ////////////////读取Interval////////////////////////////////////
        Interval[] predicate = new Interval[Info.RANGE_RELATED_ATTRIBUTE];
        for (int i = 0; i < predicate.length; i++)
            predicate[i] = new Interval();

        Generate_Predicate gt = new Generate_Predicate();
        predicate = gt.get_bound();
        //////////////////////////////
        //建立缓冲区用来存放通过过滤的元组
        LESS_Tuple[] buf_filter = new LESS_Tuple[Info.BUFFERED_TUPLE_NUMBER];
        for (int i = 0; i < buf_filter.length; i++)
            buf_filter[i] = new LESS_Tuple();
        int buf_filter_length = 0;
        int sub_number = 1;//记录这是第几个子表

        try {
            //读表进行操作
            BufferedInputStream reader_table = new BufferedInputStream(new FileInputStream(
                    Info.ROOT_PATH + Info.GENERATE_TABLE));

            byte[] buf = new byte[Info.TUPLE_INITIAL_BYTES_LENGTH];

            int bytestoread = buf.length;
            int bytesread = 0;

            //EF Window
            LESS_Tuple[] ef_window = new LESS_Tuple[Info.ENTROPY_ELIMINATION_WINDOW];
            for (int i = 0; i < ef_window.length; i++)
                ef_window[i] = new LESS_Tuple();
            int ef_length = 0;

            //找最小熵值
            Compare_Entropy ce = new Compare_Entropy();
            Arrays.sort(ef_window, ce);

            //将整个表扫描一遍，过滤
            for (int i = 0; i < Info.TUPLE_NUMBER; i++) {
                if (i % 1000000 == 0)
                    System.out.println(i + ":" + Info.TUPLE_NUMBER);

                while (bytesread < bytestoread)
                    bytesread += reader_table.read(buf, bytesread, bytestoread - bytesread);
                bytesread = 0;

                LESS_Tuple temp = new LESS_Tuple();
                temp.parse_initial(buf);
                temp.merged_index = sub_number - 1;

                //过滤
                /////首先判断是否满足选择条件
                boolean range_flag = true;
                for (int r = Info.MEASURE_ATTRIBUTE_NUMBER; r < Info.MEASURE_ATTRIBUTE_NUMBER +
                        Info.RANGE_RELATED_ATTRIBUTE; r++) {
                    if ((temp.attribute_arr[r] < predicate[r - Info.MEASURE_ATTRIBUTE_NUMBER].lowerbound)
                            | (temp.attribute_arr[r] > predicate[r - Info.MEASURE_ATTRIBUTE_NUMBER].upperbound))
                        range_flag = false;
                }
                /////////若满足选择条件
                if (range_flag) {
                    range_number++;

                    //标记当前元组是否被支配,true为不被支配，可以插入缓冲区
                    boolean flag = true;

                    for (int j = 0; j < ef_length; j++) {
                        //支配属性计数
                        int dominate = 0;

                        for (int k = 0; k < Info.DOMINATE_RELATED_ATTRIBUTE; k++) {
                            if (temp.attribute_arr[k] >= ef_window[j].attribute_arr[k])
                                dominate++;
                        }
                        //当前元组被支配，跳出当前循环
                        if (dominate == Info.DOMINATE_RELATED_ATTRIBUTE) {
                            flag = false;
                            break;
                        }
                        //当前窗口值被支配
                        else if (dominate == 0) {
                            //当前窗口后面的值向前依次移动
                            for (int l = j; l < ef_length - 1; l++) {
                                ef_window[l].copyfrom(ef_window[l + 1]);
                            }
                            ef_length--;

                        }

                    }

                    //当前元组不被支配，判断是否应插入window
                    if (flag) {
                        //不被支配，则直接插入缓冲区
                        buf_filter[buf_filter_length] = new LESS_Tuple();
                        buf_filter[buf_filter_length].copyfrom(temp);

                        buf_filter_length++;

                        ///////////////////////////////////////////////////////
                        //插入后，若此时缓冲区满，则写入磁盘中
                        if (buf_filter_length == buf_filter.length) {
                            Arrays.sort(buf_filter, 0, buf_filter_length - 1, ce);

                            byte[] writer_buf = new byte[Info.TUPLE_BYTES_LENGTH];

                            BufferedOutputStream writer = new BufferedOutputStream(
                                    new FileOutputStream(Info.ROOT_PATH + Info.LESS_PREFIX
                                            + Info.SUB_SCAN_TABLE_PATH + (sub_number - 1) + Info.extension));
                            for (int j = 0; j < buf_filter_length; j++) {
                                //pi值用来标记当前元组所在子表的位置
                                buf_filter[j].merged_index = sub_number - 1;

                                Reverse_Parse rp = new Reverse_Parse();
                                writer_buf = rp.rev_parse_LESStuple(buf_filter[j]);

                                writer.write(writer_buf);
                            }
                            writer.flush();
                            writer.close();

                            buf_filter_length = 0;
                            sub_number++;
                        }
                        //已经写入磁盘
                        //////////////////////////////////////////////

                        //判断是否应该插入ef_window
                        //window有空间
                        if (ef_length < ef_window.length) {
                            ef_window[ef_length].copyfrom(temp);
                            ef_length++;

                            Arrays.sort(ef_window, 0, ef_length - 1, ce);

                        }
                        //Window已满,替换一个比当前元组熵还低的元组，否则不替换
                        else {
                            //当前元组熵值小于窗口中的最小熵值，替换
                            if (temp.entroty < ef_window[0].entroty) {
                                ef_window[0].copyfrom(temp);
                            }
                        }
                    }
                }
            }
            //已经成功扫描一遍大表
            reader_table.close();
            /////////////////---------------------------//////////////////

            System.out.println("符合条件的元组个数为" + range_number);
            System.out.println("过滤后剩余元组个数为" + buf_filter_length);

            //写溢出文件缓冲区
            BufferedOutputStream overflow_writer = new BufferedOutputStream(new FileOutputStream(
                    Info.ROOT_PATH + Info.LESS_PREFIX + Info.OVERFLOW_FILE));
            int overflow_num = 0;

            //将缓冲区内的元组进行排序
            Arrays.sort(buf_filter, 0, buf_filter_length - 1, ce);

            //过滤窗口
            LESS_Tuple[] filter_window = new LESS_Tuple[Info.SKYLINE_FILTER_WINDOW];
            for (int i = 0; i < filter_window.length; i++)
                filter_window[i] = new LESS_Tuple();
            int window_length = 0;

            //子文件元组个数的数组
            int[] count_sub_number = new int[sub_number];
            for (int i = 0; i < count_sub_number.length; i++) {
                if (i == sub_number - 1)
                    count_sub_number[i] = buf_filter_length;
                else
                    count_sub_number[i] = Info.BUFFERED_TUPLE_NUMBER;
            }
            //归并的列表
            LESS_Tuple[] merge_sub_table = new LESS_Tuple[sub_number];

            //同时读所有的子表进行归并,先在每个子表中读出一个元组放入归并列表中
            //最后一个子表在内存中,在过滤缓冲区内
            BufferedInputStream[] sub_reader = new BufferedInputStream[sub_number - 1];

            byte[] sub_buf = new byte[Info.TUPLE_BYTES_LENGTH];

            int subbytestoread = sub_buf.length;
            int subbyteread = 0;

            for (int i = 0; i < sub_reader.length; i++) {
                sub_reader[i] = new BufferedInputStream(new FileInputStream(
                        Info.ROOT_PATH + Info.LESS_PREFIX
                                + Info.SUB_SCAN_TABLE_PATH + i + Info.extension));

                while (subbyteread < subbytestoread)
                    subbyteread += sub_reader[i].read(sub_buf, subbyteread, subbytestoread - subbyteread);
                subbyteread = 0;

                LESS_Tuple temp = new LESS_Tuple();
                temp.parse_with_entropy(sub_buf);

                merge_sub_table[i] = new LESS_Tuple();
                merge_sub_table[i].copyfrom(temp);

                count_sub_number[i]--;
            }
            merge_sub_table[sub_number - 1] = buf_filter[0];
            int start = 1;//下次读的起始位置
            count_sub_number[sub_number - 1]--;

            Arrays.sort(merge_sub_table, ce);

            //skyline点计数
            int skyline_num = 0;

            //进行merge操作
            for (int i = 0; i < ((sub_number - 1) * Info.BUFFERED_TUPLE_NUMBER + buf_filter_length); i++) {
                //标记当前元组是否被支配,true为不被支配
                boolean flag = true;
                //
                for (int j = 0; j < window_length; j++) {
                    //支配属性计数
                    int dominate = 0;

                    for (int k = 0; k < Info.DOMINATE_RELATED_ATTRIBUTE; k++) {
                        if (buf_filter[i].attribute_arr[k] >= filter_window[j].attribute_arr[k])
                            dominate++;
                    }
                    //当前元组被支配，跳出当前循环
                    if (dominate == Info.DOMINATE_RELATED_ATTRIBUTE) {
                        flag = false;
                        break;
                    }
                }

                //当前元组不被支配
                if (flag) {
                    if (window_length < Info.SKYLINE_FILTER_WINDOW) {
                        filter_window[window_length].copyfrom(merge_sub_table[0]);

                        window_length++;
                        skyline_num++;
//						System.out.println(filter_window[window_length].pi);

                        Reverse_Parse rp = new Reverse_Parse();
                        sub_buf = rp.rev_parse_LESStuple(merge_sub_table[0]);

//						writer.write(sub_buf);
                    } else {
                        Reverse_Parse rp = new Reverse_Parse();
                        sub_buf = rp.rev_parse_LESStuple(merge_sub_table[0]);

                        overflow_writer.write(sub_buf);
                        overflow_num++;
                    }
                }
                //无论哪种情形都要读取最小值所在的子表
                int position = (int) (merge_sub_table[0].merged_index);

                //如果所在子表未读取的元组数不为0
                if (count_sub_number[position] > 0) {
//					System.out.println(i);
                    if (position == (sub_number - 1)) {
                        LESS_Tuple temp = new LESS_Tuple();
                        temp.copyfrom(buf_filter[start]);

                        start++;

                        merge_sub_table[0].copyfrom(temp);
                    } else {
                        while (subbyteread < subbytestoread)
                            subbyteread += sub_reader[position].read(sub_buf, subbyteread, subbytestoread - subbyteread);
                        subbyteread = 0;

                        LESS_Tuple temp = new LESS_Tuple();
                        temp.parse_with_entropy(sub_buf);
                        merge_sub_table[0].copyfrom(temp);

                    }
                    Arrays.sort(merge_sub_table, ce);

                    count_sub_number[position]--;
                }
                //若为0，则直接将熵值置为最大值
                else if (count_sub_number[position] == 0) {
                    merge_sub_table[0].entroty = Double.MAX_VALUE;
                }
            }
            overflow_writer.flush();
            overflow_writer.close();
            //将溢出文件内的元组都读到overflow_array里
            BufferedInputStream overflow_reader = new BufferedInputStream(new FileInputStream(
                    Info.ROOT_PATH + Info.LESS_PREFIX + Info.OVERFLOW_FILE));

            Overflow_Array[] arr = new Overflow_Array[overflow_num];
            for (int i = 0; i < arr.length; i++) {
                while (subbyteread < subbytestoread)
                    subbyteread += overflow_reader.read(sub_buf, subbyteread, subbytestoread - subbyteread);
                subbyteread = 0;

                arr[i] = new Overflow_Array();
                arr[i].flage = true;

                LESS_Tuple temp = new LESS_Tuple();
                temp.parse_with_entropy(sub_buf);

                arr[i].tt = new LESS_Tuple();
                arr[i].tt.copyfrom(temp);
            }
            //比较元组之间的支配关系，将被支配的元组标记为false
            for (int i = 0; i < arr.length; i++) {
                for (int j = i + 1; j < arr.length; j++) {
                    int dominate = 0;

                    for (int k = 0; k < Info.DOMINATE_RELATED_ATTRIBUTE; k++) {
                        if (arr[i].tt.attribute_arr[k] < arr[j].tt.attribute_arr[k])
                            dominate++;
                    }
                    //用来比较的元组支配当前比较的元组，则将当前比较的元组标记改为false
                    if (dominate == Info.DOMINATE_RELATED_ATTRIBUTE) {
                        arr[j].flage = false;
                    }
//					//用来比较的元组被当前比较的元组支配，将用来比较的元组标记改为false
//					if (dominate == 0) 
//					{
//						arr[i].flage = false;
//					}
                }
            }
            for (int i = 0; i < arr.length; i++) {
                //不被任一元组支配的元组，写入skyline文件，并将skyline计数增一
                if (arr[i].flage = true) {
                    skyline_num++;

                    Reverse_Parse rp = new Reverse_Parse();
                    sub_buf = rp.rev_parse_LESStuple(arr[i].tt);

//					writer.write(sub_buf);
                }
            }
            overflow_reader.close();

//			writer.flush();
//			writer.close();

            System.out.println("窗口内元组个数为" + window_length);
            System.out.println("溢出文件中的元组个数为" + overflow_num);
            System.out.println("最终的skyline元组个数为" + skyline_num);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();


        LESS le = new LESS();

        le.less_skyline();

        long endTime = System.currentTimeMillis();

        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
    }

}
