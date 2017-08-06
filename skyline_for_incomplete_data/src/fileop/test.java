package src.fileop;

import src.data_structure.Tuple;
import src.global.Info;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;

/**
 * Created by Xue on 2017/7/26.
 */
public class test
{
    public void read()
    {
        try
        {
            BufferedInputStream reader = new BufferedInputStream(new
                    FileInputStream(Info.ROOT_PATH + Info.sort_prefix
                    + Info.SCAN_AVERAGE_BUCKET_TABLE_PATH));

            byte[] buf = new byte[Info.TUPLE_AVERAGE_BUCKET_BYTES_LENGTH];
            int bytesToRead = buf.length;
            int bytesRead = 0;

            Tuple pro_temp = new Tuple();
            Tuple cur_temp = new Tuple();

            boolean sort_flag = true;

            for (int i = 0 ; i < Info.TUPLE_NUMBER ; i ++)
            {
                while (bytesRead < bytesToRead)
                    bytesRead += reader.read(buf , bytesRead ,
                            bytesToRead - bytesRead);
                bytesRead = 0;

               pro_temp.copyfrom(cur_temp);

                cur_temp.parse_average_bucket(buf);

//                if ((i >= 11967) && (i < 11980 ))
//                    System.out.print(cur_temp);

                if (i > 0)
                {
                    if (pro_temp.bucket_index == cur_temp.bucket_index)
                    {
                        if (pro_temp.average_value > cur_temp.average_value)
                            sort_flag = false;
                    }
                }
                if (sort_flag == false)
                {
                    System.out.println( i + "something has wrong");

                    System.out.print(pro_temp);
                    System.out.print(cur_temp);
                    break;
                }

            }
            reader.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public static void main(String[] args)
    {
        long startTime = System.currentTimeMillis();

        test te = new test();
        te.read();

        long endTime = System.currentTimeMillis();

        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
    }
}
