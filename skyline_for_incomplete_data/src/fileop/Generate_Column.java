package fileop;


import data_structure.Column;
import data_structure.Reverse_Parse;
import data_structure.Tuple;
import global.Info;

import java.io.*;

/**
 * Created by Xue on 2017/7/14.
 */
public class Generate_Column
{
    public void generate()
    {
        try
        {
            BufferedOutputStream column_count_writer = new BufferedOutputStream(
                    new FileOutputStream(Info.ROOT_PATH + Info.COLUMN_ROOT
                            + Info.COLUMN_COUNT_PATH));

            BufferedInputStream pt_reader = new BufferedInputStream(
                    new FileInputStream(Info.ROOT_PATH + Info.INITIAL_TABLE_PATH));

            BufferedOutputStream[] writer =
                    new BufferedOutputStream[Info.ATTRIBUTE_NUMBER];
            for (int i = 0; i < writer.length; i++)
            {
                writer[i] = new BufferedOutputStream(new FileOutputStream(
                        Info.ROOT_PATH + Info.COLUMN_ROOT
                                + Info.COLUMN_PATH + i + Info.extension));
            }

            byte[] buf =new byte[Info.TUPLE_INITIAL_BYTES_LENGTH];
            int bytesToRead = buf.length;
            int bytesRead = 0;

            byte[] column_buf = new byte[Info.COLUMN_BYTES_LENGTH];

            long[] column_attribute_count = new long[Info.ATTRIBUTE_NUMBER];

            for (int i = 0; i < Info.TUPLE_NUMBER; i++)
            {
                if (i%100000 == 0)
                    System.out.println(i + ":" + Info.TUPLE_NUMBER);

                while (bytesRead < bytesToRead)
                    bytesRead += pt_reader.read(
                            buf, bytesRead, bytesToRead - bytesRead);
                bytesRead = 0;

                Tuple temp = new Tuple();
                temp.parse_initial(buf);

                for (int j = 0; j < Info.ATTRIBUTE_NUMBER; j++)
                {
                    //若当前属性值不缺失，则将该属性值写入列文件
                    if (temp.attributes[j] != Long.MIN_VALUE)
                    {
                        //给列文件赋值
                        Column c_temp = new Column();
                        c_temp.pi = temp.position_index;
                        c_temp.value = temp.attributes[j];

                        column_attribute_count[j]++;

                        Reverse_Parse rp = new Reverse_Parse();
                        column_buf = rp.rev_parse_column(c_temp);

                        writer[j].write(column_buf);
                    }
                }
            }

            byte[] count_buf = new byte[Info.ATTRIBUTE_NUMBER
                    * Info.ATTRIBUTE_BYTES_LENGTH];

            for (int i = 0; i < writer.length; i++)
            {
                writer[i].flush();
                writer[i].close();

                count_buf[8*i + 0] = (byte)(0xff & (column_attribute_count[i] >> 56));
                count_buf[8*i + 1] = (byte)(0xff & (column_attribute_count[i] >> 48));
                count_buf[8*i + 2] = (byte)(0xff & (column_attribute_count[i] >> 40));
                count_buf[8*i + 3] = (byte)(0xff & (column_attribute_count[i] >> 32));
                count_buf[8*i + 4] = (byte)(0xff & (column_attribute_count[i] >> 24));
                count_buf[8*i + 5] = (byte)(0xff & (column_attribute_count[i] >> 16));
                count_buf[8*i + 6] = (byte)(0xff & (column_attribute_count[i] >>  8));
                count_buf[8*i + 7] = (byte)(0xff & column_attribute_count[i]);
            }

            column_count_writer.write(count_buf);
            column_count_writer.flush();
            column_count_writer.close();

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

        Generate_Column gc = new Generate_Column();
        gc.generate();

        long endTime = System.currentTimeMillis();

        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");

    }
}
