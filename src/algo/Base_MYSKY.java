package algo;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import fileop.Generate_Predicate;
import global.Info;
import datastructure.Interval;
import datastructure.Tuple;

public class Base_MYSKY {
    public void generate_skyline() {
        //--------------------------读取Interval--------------------------------//
        Interval[] predicate = new Interval[Info.RANGE_RELATED_ATTRIBUTE];
        for (int i = 0; i < predicate.length; i++)
            predicate[i] = new Interval();

        Generate_Predicate gt = new Generate_Predicate();
        predicate = gt.get_bound();
        /////////////----------------------------------------------///////////////

        try {
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


            for (int i = 0; i < Info.TUPLE_NUMBER; i++) {
                while (bytesRead < bytesToRead)
                    bytesRead +=
                            reader.read(buf, bytesRead, bytesToRead - bytesRead);
                bytesRead = 0;

                Tuple temp = new Tuple();
                temp.parse_tuple(buf);

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
            }
            System.out.println("==============满足选择条件的元组个数为：");
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

        Base_MYSKY bal = new Base_MYSKY();
        bal.generate_skyline();

        long endTime = System.currentTimeMillis();


        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");

    }
}
