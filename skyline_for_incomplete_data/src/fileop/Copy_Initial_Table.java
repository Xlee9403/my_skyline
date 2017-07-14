package fileop;

import global.Info;

import java.io.*;

/**
 * Created by Xue on 2017/7/14.
 */
public class Copy_Initial_Table
{
    public void generate()
    {
        try
        {
            BufferedInputStream pt_reader = new BufferedInputStream(
                    new FileInputStream(Info.ROOT_PATH
                            + Info.INITIAL_TABLE_PATH));

            byte[] buf = new byte[Info.TUPLE_INITIAL_BYTES_LENGTH];
            int aveToRead = buf.length;
            int bytesRead = 0;

            BufferedOutputStream pt_writer = new BufferedOutputStream(
                    new FileOutputStream(Info.ROOT_PATH +
                            Info.COPY_INITIAL_TABLE_PATH));

            for (int i = 0; i < Info.TUPLE_NUMBER; i++)
            {
                while (bytesRead < aveToRead)
                    bytesRead += pt_reader.read(buf, bytesRead,
                            aveToRead - bytesRead);
                bytesRead = 0;

                pt_writer.write(buf);
            }

            pt_writer.close();
            pt_writer.flush();

            pt_reader.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        Copy_Initial_Table cit = new Copy_Initial_Table();
        cit.generate();

    }
}
