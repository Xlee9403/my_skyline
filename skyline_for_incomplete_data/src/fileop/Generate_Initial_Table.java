package  src.fileop;

import  src.global.Info;

import java.io.*;
import java.util.Random;

/**
 * Created by Xue on 2017/7/14.
 */
public class Generate_Initial_Table
{
    public void generate()
    {
        try
        {
            BufferedOutputStream writer = new BufferedOutputStream(
                    new FileOutputStream(Info.ROOT_PATH + Info.INITIAL_TABLE_PATH));

            byte[] buf = new byte[Info.ATTRIBUTE_BYTES_LENGTH];

            Random rnd = new Random();

            for (int i = 0; i < Info.TUPLE_NUMBER; i++)
            {
                if (i % 1000000 == 0)
                    System.out.println(i + ":" + Info.TUPLE_NUMBER);

                long position = i;

                buf[0] = (byte)(0xff & (position >> 56));
                buf[1] = (byte)(0xff & (position >> 48));
                buf[2] = (byte)(0xff & (position >> 40));
                buf[3] = (byte)(0xff & (position >> 32));
                buf[4] = (byte)(0xff & (position >> 24));
                buf[5] = (byte)(0xff & (position >> 16));
                buf[6] = (byte)(0xff & (position >>  8));
                buf[7] = (byte)(0xff & position);

                writer.write(buf);

                for (int j = 0; j < Info.ATTRIBUTE_NUMBER; j++)
                {
                    long value = Math.abs(rnd.nextLong());

                    //缺失值用Long型最小值代替
                    if (rnd.nextDouble() <= Info.incompleteness_ratio)
                        value = Long.MIN_VALUE;

                    buf[0] = (byte)(0xff & (value >> 56));
                    buf[1] = (byte)(0xff & (value >> 48));
                    buf[2] = (byte)(0xff & (value >> 40));
                    buf[3] = (byte)(0xff & (value >> 32));
                    buf[4] = (byte)(0xff & (value >> 24));
                    buf[5] = (byte)(0xff & (value >> 16));
                    buf[6] = (byte)(0xff & (value >>  8));
                    buf[7] = (byte)(0xff & value);

                    writer.write(buf);

                }

            }

            writer.flush();
            writer.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        Generate_Initial_Table git = new Generate_Initial_Table();
        git.generate();

    }
}
