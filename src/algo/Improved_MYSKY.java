package algo;

import java.io.*;
import java.util.*;

import datastructure.*;
import fileop.Generate_Predicate;
import fileop.Get_Select_Mm_Value;
import global.Info;

//1.添加位图
//基本思路：在读取每个元组之前，先读取位图信息，决定是否读取当前元组。

//2. 支配检测
//基本思路：根据预计算的MCR信息，提前跳过被当前元组支配的元组。

public class Improved_MYSKY {
    //尚未完善，仅仅实现功能
    //功能上实现范围剪切和支配剪切
    public void generate_skyline() {
        //--------------------------读取谓词--------------------------------//
        Interval[] predicate = new Interval[Info.RANGE_RELATED_ATTRIBUTE];
        for (int i = 0; i < predicate.length; i++)
            predicate[i] = new Interval();

        Generate_Predicate gt = new Generate_Predicate();
        predicate = gt.get_bound();
        /////////////----------------------------------------------///////////////

        //-----------------先读出select的最大值和最小值----------------------------//
        Select_Min_Max_Value smmv = new Select_Min_Max_Value();

        Get_Select_Mm_Value gsmv = new Get_Select_Mm_Value();
        smmv = gsmv.get_Mm_value();
        /////////////////////---------------------------------//////////////////

        try {
            //-----------------------确定要读取的选择属性位图----------------------//
            boolean[][] isRead = new boolean[Info.RANGE_RELATED_ATTRIBUTE][2];
            for (int i = 0; i < Info.RANGE_RELATED_ATTRIBUTE; i++) {
                for (int j = 0; j < 2; j++)
                    isRead[i][j] = true;
            }

            BufferedInputStream[][] select_bitmap_reader =
                    new BufferedInputStream[Info.RANGE_RELATED_ATTRIBUTE][2];
            for (int i = 0; i < Info.RANGE_RELATED_ATTRIBUTE; i++) {
                for (int j = 0; j < 2; j++) {
                    int m;

                    if (j == 0) {
                        //下界的前一个，写进的位图
                        m = ((int) ((predicate[i].lowerbound
                                - smmv.min_value) / smmv.number_per_range));

                        if (m == 0)
                            isRead[i][j] = false;
                        else
                            m--;
                    } else {
                        //上界写进的位图
                        m = ((int) ((predicate[i].upperbound
                                - smmv.min_value) / smmv.number_per_range)) + 1;
                    }

                    if (isRead[i][j]) {
                        select_bitmap_reader[i][j] = new BufferedInputStream(
                                new FileInputStream(Info.ROOT_PATH +
                                        Info.SELECT_BIRMAP_ROOT +
                                        Info.SELECT_COLUNME_PATH + Info.BITMAP_PATH
                                        + i + Info.intervel + m + Info.extension));
                    }
                }
            }
            byte[] bit_buf = new byte[Info.BLOCK_SIZE];
            int bitsBufToRead = Info.BLOCK_SIZE;
            int bitsBufRead = 0;

            //临时存放读出的位图
            byte[] temp_buf = new byte[Info.BLOCK_SIZE];

            byte[] bitmap_buf = new byte[Info.BLOCK_SIZE];
            /////////////////////----------------------------------/////////////////

            //------------------------------度量属性位图--------------------------//
            //读度量属性位图的缓冲区
            BufferedInputStream[] measure_bitmap_reader =
                    new BufferedInputStream[Info.DOMINATE_RELATED_ATTRIBUTE];

            //用于支配检测的数组
            Dominate_Bitmap_ToRead[] dominate_detection =
                    new Dominate_Bitmap_ToRead[Info.DOMINATE_DETECTION_TUPLE_NUMBER];
            for (int j = 0; j < dominate_detection.length; j++)
                dominate_detection[j] = new Dominate_Bitmap_ToRead();

            byte[] dominate_bit = new byte[Info.BLOCK_SIZE];

            //每列位图的总数量，自下而上建立位图，只需取整
            int bitmap_count = (int) (Math.log((double) Info.TUPLE_NUMBER) /
                    Math.log(Info.LOGARITHMIC_BASE));

            ///////////////////----------------------------------------///////////

            //满足条件的元组个数
            int range_count = 0;

            long early_mpi = Long.MAX_VALUE;

            //存放skyline的数组
            ArrayList<Tuple> window = new ArrayList<Tuple>();
            int window_length = 0;

            BufferedInputStream reader = new BufferedInputStream(
                    new FileInputStream(Info.ROOT_PATH + Info.SORTED_SCAN_TABLE));

            byte[] buf = new byte[Info.TUPLE_BYTES_LENGTH];
            int bytesToRead = buf.length;
            int bytesRead = 0;

            //读取的选择属性位图的块数
            int read_count = 0;

            //元组数对应的位图的byte数
            int bitmap_byte_length = Info.TUPLE_NUMBER / 8;

            //选择属性位图跳过的元组数
            int range_skip_number = 0;

            //找符合条件的skyline点
            for (int i = 0; i < Info.TUPLE_NUMBER; i++) {
                //-------------------选择属性位图操作，确定符合条件的----------------//
                //按块读取位图，每个读出一块

                //标记是否强制读取当前元组，即在每个位图结束时，判断能否早结束
                boolean early_read_flag = false;

                if (i % (Info.BLOCK_SIZE * 8) == 0) {

                    //--------------每读取一个块，读出当前元组，判断是否早结束-----------//
                    //跳过当前位的判断，直接进行下一次
                    while (bytesRead < bytesToRead)
                        bytesRead +=
                                reader.read(buf, bytesRead, bytesToRead - bytesRead);
                    bytesRead = 0;

                    Tuple temp = new Tuple();
                    temp.parse_tuple(buf);

                    i++;

                    early_read_flag = true;

                    if (temp.MPI >= early_mpi) {
                        System.out.println("=============扫描深度为=====：");
                        System.out.println(i + ":" + Info.TUPLE_NUMBER);
                        break;

                    }
                    ///////////////-------------------------------///////////////

                    if (bitmap_byte_length < Info.BLOCK_SIZE) {
                        bitsBufToRead = bitmap_byte_length;
                    }

                    java.util.Arrays.fill(dominate_bit, (byte) 0xff);
                    //不是第一次读
                    if (read_count != 0) {
                        //用来做支配检测的元组数
                        for (int j = 0; j < Info.DOMINATE_DETECTION_TUPLE_NUMBER; j++) {
                            byte[] tmp_dominate_bit = new byte[Info.BLOCK_SIZE];
                            java.util.Arrays.fill(tmp_dominate_bit, (byte) 0x00);

                            for (int k = 0; k < Info.DOMINATE_RELATED_ATTRIBUTE; k++) {
                                measure_bitmap_reader[k] = new BufferedInputStream(
                                        new FileInputStream(Info.ROOT_PATH + Info.MEASURE_BITMAP_ROOT
                                                + Info.MEASURE_COLUNME_PATH + Info.BITMAP_PATH + k
                                                + Info.intervel + dominate_detection[j].bit_index[k]
                                                + Info.extension));

                                long measkiplength = Info.BLOCK_SIZE * read_count;

                                while (measkiplength > 0)
                                    measkiplength -= measure_bitmap_reader[k].skip(measkiplength);

                                while (bitsBufRead < bitsBufToRead)
                                    bitsBufRead += measure_bitmap_reader[k].read(bit_buf,
                                            bitsBufRead, bitsBufToRead - bitsBufRead);
                                bitsBufRead = 0;

                                for (int l = 0; l < dominate_bit.length; l++)
                                    tmp_dominate_bit[l] |= bit_buf[l];
                            }

                            for (int l = 0; l < dominate_bit.length; l++)
                                dominate_bit[l] &= tmp_dominate_bit[l];
                        }
                    }


                    read_count++;

                    //---------------------------读出一个块------------------------------//
                    for (int j = 0; j < Info.RANGE_RELATED_ATTRIBUTE; j++) {
                        for (int k = 0; k < 2; k++) {
                            if (isRead[j][k]) {
                                while (bitsBufRead < bitsBufToRead)
                                    bitsBufRead += select_bitmap_reader[j][k].read(bit_buf,
                                            bitsBufRead, bitsBufToRead - bitsBufRead);
                                bitsBufRead = 0;

                                if (k == 0) {
                                    for (int l = 0; l < Info.BLOCK_SIZE; l++) {
                                        temp_buf[l] = (byte) ~bit_buf[l];
                                    }
                                } else {
                                    for (int l = 0; l < Info.BLOCK_SIZE; l++) {
                                        temp_buf[l] &= bit_buf[l];

                                        if (j == 0)
                                            bitmap_buf[l] = temp_buf[l];
                                        else
                                            bitmap_buf[l] &= temp_buf[l];
                                    }
                                }
                            } else {
                                java.util.Arrays.fill(temp_buf, (byte) 0xff);

                            }
                        }
                    }

                    bitmap_byte_length -= Info.BLOCK_SIZE;

                    //合并选择属性和支配检测
                    for (int j = 0; j < Info.BLOCK_SIZE; j++)
                        bitmap_buf[j] &= dominate_bit[j];


                }
                //每读取一块操作一次
                //////////////////---------------------------------/////////////////

                //----------------------------判断是否要跳过------------------------//
                //所在的byte位  (i-(read_count * block_size))/8
                //所在的bit位 i % 8
                //判断对应位是否为1，确定要不要读取，true为要读取
                boolean read_flag = false;

                int byte_position = (i - ((read_count - 1) * (Info.BLOCK_SIZE * 8))) / 8;

                if ((bitmap_buf[byte_position] & (1 << (8 - 1 - i % 8))) != 0)
                    read_flag = true;

                if (early_read_flag == true)
                    read_flag = true;

                if (read_flag) {
                    while (bytesRead < bytesToRead)
                        bytesRead +=
                                reader.read(buf, bytesRead, bytesToRead - bytesRead);
                    bytesRead = 0;

                    Tuple temp = new Tuple();
                    temp.parse_tuple(buf);

                    //按measure_count排序，找出，top-k个最小measure_count
                    Compare_Measure_Count cmc = new Compare_Measure_Count();
                    Arrays.sort(dominate_detection, cmc);

                    //判断是否更新支配检测元组
                    long relat_measure_count = 0;
                    for (int j = 0; j < Info.DOMINATE_RELATED_ATTRIBUTE; j++)
                        relat_measure_count += temp.measure_arr[j];

                    if (relat_measure_count < dominate_detection[
                            Info.DOMINATE_DETECTION_TUPLE_NUMBER - 1].measure_count) {
                        dominate_detection[Info.DOMINATE_DETECTION_TUPLE_NUMBER
                                - 1].measure_count = relat_measure_count;

                        //计算每个要读取的度量属性的位图索引
                        for (int j = 0; j < Info.DOMINATE_RELATED_ATTRIBUTE; j++) {
                            int temp_index = (int) (temp.measure_arr[j]);
                            for (int k = 0; k < bitmap_count; k++) {
                                if (temp_index <= Math.pow(Info.LOGARITHMIC_BASE, k + 1)) {
                                    dominate_detection[Info.DOMINATE_DETECTION_TUPLE_NUMBER
                                            - 1].bit_index[j] = k;

                                    break;
                                }
                            }
                        }
                    }


                    if (temp.MPI >= early_mpi) {
                        System.out.println("=============扫描深度为：");
                        System.out.println(i + ":" + Info.TUPLE_NUMBER);
                        break;

                    }

                    //判断是否满足选择条件
                    boolean range_flag = true;
                    for (int j = 0; j < Info.RANGE_RELATED_ATTRIBUTE; j++) {
                        if ((temp.select_arr[j] < predicate[j].lowerbound)
                                | (temp.select_arr[j] > predicate[j].upperbound)) {
                            range_flag = false;
                        }

                    }
                    if (range_flag) {
//						System.out.println(temp.position_index);

                        range_count++;

                        //true为当前元组不被支配
                        boolean flag = true;
                        for (int j = 0; j < window_length; j++) {
                            //支配属性计数
                            int dominate = 0;

                            for (int k = 0; k < Info.DOMINATE_RELATED_ATTRIBUTE; k++) {
                                if (temp.measure_arr[k] >= window.get(j).measure_arr[k])
                                    dominate++;
                            }
                            //当前元组被支配，跳出当前循环
                            if (dominate == Info.DOMINATE_RELATED_ATTRIBUTE) {
                                flag = false;
                                break;
                            }
                            //当前窗口值被支配，移除当前窗口值，并将window_length减一
                            //下次比较时j值不变，故此处j--
                            else if (dominate == 0) {
                                window.remove(j);
                                window_length--;

                                j--;
                            }
                        }
                        //当前元组不被支配，则写入窗口，skyline个数增一
                        if (flag) {
                            Tuple t = new Tuple();
                            t.copyfrom(temp);

                            //找当前元组最差的属性pi
                            long max_pi = 0;
                            for (int j = 0; j < Info.DOMINATE_RELATED_ATTRIBUTE; j++) {
                                if (t.measure_arr[j] > max_pi)
                                    max_pi = t.measure_arr[j];
                            }

                            //更新早结束条件
                            if (max_pi < early_mpi)
                                early_mpi = max_pi;

                            window.add(t);
                            window_length++;
                        }
                    }
                } else {
                    long skiplength = Info.TUPLE_BYTES_LENGTH;

                    while (skiplength > 0)
                        skiplength -= reader.skip(skiplength);

                    range_skip_number++;
                }
            }
            System.out.println("==============读取的位图块数为：");
            System.out.println(read_count);
            System.out.println("==============跳过不满足条件的元组数为：");
            System.out.println(range_skip_number);
            System.out.println("==============需要计算的元组个数为：");
            System.out.println(range_count);
            System.out.println("==============skyline元组个数为：");
            System.out.println(window_length + ":" + window.size());
            long[] arr = new long[window_length];
            for (int j = 0; j < arr.length; j++) {
                arr[j] = window.get(j).position_index;
            }

            Arrays.sort(arr);
            for (int j = 0; j < arr.length; j++) {
                System.out.println(arr[j]);
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        Improved_MYSKY bal = new Improved_MYSKY();
        bal.generate_skyline();

        long endTime = System.currentTimeMillis();

        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");

    }

}
