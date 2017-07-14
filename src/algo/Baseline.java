package algo;

import java.io.*;

import datastructure.Interval;
import datastructure.LESS_Tuple;
import fileop.Generate_Predicate;
import global.Info;

public class Baseline {
    public void generata() {
        int range_number = 0;

        ////////////////////////////////////////////////////
        //读取Interval开始
        Interval[] predicate = new Interval[Info.RANGE_RELATED_ATTRIBUTE];
        for (int i = 0; i < predicate.length; i++)
            predicate[i] = new Interval();

        Generate_Predicate gt = new Generate_Predicate();
        predicate = gt.get_bound();
        //读Interval结束
        ////////////////////////////////////////////////////////////////

        try {
            //读表进行操作
            BufferedInputStream reader_table = new BufferedInputStream(
                    new FileInputStream(
                            Info.ROOT_PATH + Info.GENERATE_TABLE));

            byte[] buf = new byte[Info.TUPLE_INITIAL_BYTES_LENGTH];

            int bytestoread = buf.length;
            int bytesread = 0;

            LESS_Tuple[] arr = new LESS_Tuple[Info.BUFFERED_TUPLE_NUMBER];
            for (int i = 0; i < arr.length; i++)
                arr[i] = new LESS_Tuple();
            int arr_length = 0;

            for (int i = 0; i < Info.TUPLE_NUMBER; i++) {
                if (i % 1000000 == 0)
                    System.out.println(i + ":" + Info.TUPLE_NUMBER);

                while (bytesread < bytestoread)
                    bytesread += reader_table.read(buf, bytesread, bytestoread - bytesread);
                bytesread = 0;

                LESS_Tuple temp = new LESS_Tuple();
                temp.parse_initial(buf);

                /////首先判断是否满足选择条件
                boolean range_flag = true;
                for (int r = Info.MEASURE_ATTRIBUTE_NUMBER;
                     r < Info.MEASURE_ATTRIBUTE_NUMBER + Info.RANGE_RELATED_ATTRIBUTE; r++) {
                    if ((temp.attribute_arr[r] < predicate[r - Info.MEASURE_ATTRIBUTE_NUMBER].lowerbound)
                            | (temp.attribute_arr[r] > predicate[r - Info.MEASURE_ATTRIBUTE_NUMBER].upperbound))
                        range_flag = false;
                }

                /////////若满足选择条件
                if (range_flag) {
                    range_number++;
                    arr[arr_length].copyfrom(temp);
                    arr_length++;
                }
            }

            int skyline_number = 0;
            for (int i = 0; i < arr_length; i++) {
                boolean flag = true;
                for (int j = 0; j < arr_length; j++) {
                    int dominated = 0;
                    int equalnum = 0;
                    if (i != j) {
                        for (int k = 0; k < Info.DOMINATE_RELATED_ATTRIBUTE; k++) {
                            if (arr[i].attribute_arr[k] >= arr[j].attribute_arr[k])
                                dominated++;

                            if (arr[i].attribute_arr[k] == arr[j].attribute_arr[k])
                                equalnum += 1;
                        }
                        if ((dominated == Info.DOMINATE_RELATED_ATTRIBUTE)
                                && (equalnum != Info.DOMINATE_RELATED_ATTRIBUTE)) {
                            flag = false;
                            break;
                        }
                    }
                }
                if (flag) {
                    skyline_number++;
                    System.out.println(arr[i].pi);
                }
            }

            System.out.println("满足条件的元组个数为" + range_number);
            System.out.println("skyline元组个数为" + skyline_number);

            reader_table.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Baseline bl_app = new Baseline();
        bl_app.generata();
    }

}
