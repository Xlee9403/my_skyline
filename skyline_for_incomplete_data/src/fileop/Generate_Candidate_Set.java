package  src.fileop;

import  src.global.Info;

import java.io.*;

/**
 * Created by Xue on 2017/7/14.
 */
public class Generate_Candidate_Set
{
    public void generate()
    {
        try
        {
            BufferedInputStream pt_reader = new BufferedInputStream(
                    new FileInputStream(Info.ROOT_PATH + Info.INITIAL_TABLE_PATH));

            BufferedOutputStream[] pt_writer = new BufferedOutputStream[2];
            for (int i = 0; i < pt_writer.length; i++)
                pt_writer[i] = new BufferedOutputStream(new FileOutputStream(
                        Info.ROOT_PATH + Info.CANDIDATE_TABLE_PATH
                                + i + Info.extension));

            byte[] buf = new byte[Info.TUPLE_INITIAL_BYTES_LENGTH];
            int bytesToRead = buf.length;
            int bytesRead = 0;

            for (int i = 0; i < Info.TUPLE_NUMBER; i++)
            {
                while (bytesRead < bytesToRead)
                    bytesRead += pt_reader.read(buf, bytesRead,
                            bytesToRead - bytesRead);
                bytesRead = 0;

                for (int j = 0; j < pt_writer.length; j++)
                    pt_writer[j].write(buf);
            }

            for (int i = 0; i < pt_writer.length; i++)
            {
                pt_writer[i].flush();
                pt_writer[i].close();
            }
            pt_reader.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        Generate_Candidate_Set gcs = new Generate_Candidate_Set();
        gcs.generate();

    }
}
