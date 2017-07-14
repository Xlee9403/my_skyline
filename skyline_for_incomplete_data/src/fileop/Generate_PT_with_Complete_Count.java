package fileop;

import data_structure.Reverse_Parse;
import data_structure.Tuple;
import global.Info;

import java.io.*;

/**
 * Created by Xue on 2017/7/14.
 */
public class Generate_PT_with_Complete_Count
{
    public void genetate()
    {
        try
        {
            BufferedInputStream reader = new BufferedInputStream(
                    new FileInputStream(Info.ROOT_PATH + Info.INITIAL_TABLE_PATH));

            byte[] buf = new byte[Info.TUPLE_INITIAL_BYTES_LENGTH];
            int bytesToRead = buf.length;
            int bytesRead = 0;

            BufferedOutputStream writer = new BufferedOutputStream(
                    new FileOutputStream(Info.ROOT_PATH
                            + Info.TABLE_COMPLETE_ATTRIBUTE_COUNT_PATH));

            byte[] writer_buf = new byte[Info.TUPLE_COMPLETE_COUNT_BYTES_LENGTH];

            for (int i = 0; i < Info.TUPLE_NUMBER; i++)
            {
                if (i % 100000 == 0)
                    System.out.println(i + ":" + Info.TUPLE_NUMBER);

                while (bytesRead < bytesToRead)
                    bytesRead += reader.read(buf, bytesRead, bytesToRead);
                bytesRead = 0;

                Tuple temp = new Tuple();
                temp.parse_initial(buf);

                int complete_count = 0;

                for (int j = 0; j < Info.ATTRIBUTE_NUMBER; j++)
                {
                    if (temp.attributes[j] != Long.MIN_VALUE)
                        complete_count++;
                }

                temp.comlete_count = (long)complete_count;

                Reverse_Parse rp = new Reverse_Parse();
                writer_buf = rp.rev_parse_tuple_complete_count(temp);

                writer.write(writer_buf);
            }

            writer.flush();
            writer.close();

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

        Generate_PT_with_Complete_Count gpcc =
                new Generate_PT_with_Complete_Count();
        gpcc.genetate();

        long endTime = System.currentTimeMillis();

        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");

    }
}
