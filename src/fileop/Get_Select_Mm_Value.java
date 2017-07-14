package fileop;

import global.Info;

import java.io.*;

import datastructure.Reverse_Parse;
import datastructure.Select_Min_Max_Value;
import datastructure.Tuple;

public class Get_Select_Mm_Value {
    public void read_to_get_select_Mm_value() {
        long min_value = Long.MAX_VALUE;
        long max_value = Long.MIN_VALUE;
        try {
            BufferedInputStream read_initial_pt = new BufferedInputStream(
                    new FileInputStream(Info.ROOT_PATH + Info.GENERATE_TABLE));

            byte[] tuple_buf = new byte[Info.TUPLE_INITIAL_BYTES_LENGTH];
            int bytesToRead = tuple_buf.length;
            int bytesRead = 0;

            BufferedOutputStream select_Mm_writer =
                    new BufferedOutputStream(new FileOutputStream(
                            Info.ROOT_PATH + Info.SELECT_MIN_MAX_PATH));

            for (int i = 0; i < Info.TUPLE_NUMBER; i++) {
                if (i % 1000000 == 0)
                    System.out.println(i + ":" + Info.TUPLE_NUMBER);

                while (bytesRead < bytesToRead)
                    bytesRead += read_initial_pt.read(tuple_buf,
                            bytesRead, bytesToRead - bytesRead);
                bytesRead = 0;

                Tuple temp = new Tuple();
                temp.parse_initial(tuple_buf);

                for (int j = 0; j < Info.SELECT_ATTRIBUTE_NUMBER; j++) {
                    if (temp.select_arr[j] < min_value)
                        min_value = temp.select_arr[j];

                    if (temp.select_arr[j] > max_value)
                        max_value = temp.select_arr[j];
                }

            }

            Select_Min_Max_Value smmv = new Select_Min_Max_Value();
            smmv.min_value = min_value;
            smmv.max_value = max_value;

            long range = max_value - min_value + 1;
            if (range % Info.SELECT_BITMAP_NUM == 0)
                smmv.number_per_range = range / Info.SELECT_BITMAP_NUM;
            else
                smmv.number_per_range = range / Info.SELECT_BITMAP_NUM;

            byte[] buf = new byte[Info.ATTRIBUTE_LENGTH * 3];

            Reverse_Parse rp = new Reverse_Parse();
            buf = rp.rev_parse_select_min_max(smmv);

            select_Mm_writer.write(buf);

            read_initial_pt.close();

            select_Mm_writer.flush();
            select_Mm_writer.close();

            System.out.println(smmv);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //获取选择属性的最大值和最小值
    public Select_Min_Max_Value get_Mm_value() {
        Select_Min_Max_Value smmv = new Select_Min_Max_Value();

        try {
            BufferedInputStream min_max_reader = new BufferedInputStream(
                    new FileInputStream(Info.ROOT_PATH + Info.SELECT_MIN_MAX_PATH));

            byte[] mm_buf = new byte[Info.ATTRIBUTE_LENGTH * 3];
            int mmbytestoread = mm_buf.length;
            int mmbytesread = 0;

            while (mmbytesread < mmbytestoread)
                mmbytesread += min_max_reader.read(
                        mm_buf, mmbytesread, mmbytestoread - mmbytesread);

            smmv.parse_min_max(mm_buf);

            min_max_reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return smmv;
    }

    public static void main(String[] args) {
        Get_Select_Mm_Value gsmv = new Get_Select_Mm_Value();

        gsmv.read_to_get_select_Mm_value();

    }

}
