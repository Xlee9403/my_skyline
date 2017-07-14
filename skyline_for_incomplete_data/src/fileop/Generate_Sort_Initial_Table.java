package fileop;

import global.Info;

import java.io.*;

/**
 * Created by Xue on 2017/7/14.
 */
public class Generate_Sort_Initial_Table
{
    public void generate()
    {
        try
        {
            BufferedInputStream pt_reader = new BufferedInputStream(
                    new FileInputStream(Info.ROOT_PATH
                            + Info.SORTED_AVERAGE_TABLE_PATH));

            byte[] ave_buf = new byte[Info.TUPLE_AVERAGE_BYTES_LENGTH];
            int aveToRead = ave_buf.length;
            int bytesRead = 0;

            BufferedOutputStream pt_writer = new BufferedOutputStream(
                    new FileOutputStream(Info.ROOT_PATH +
                            Info.TABLE_WITH_SORTED_PI_PATH));

            for (int i = 0; i < Info.TUPLE_NUMBER; i++)
            {
                while (bytesRead < aveToRead)
                    bytesRead += pt_reader.read(ave_buf, bytesRead,
                            aveToRead - bytesRead);
                bytesRead = 0;

//				Tuple temp = new Tuple();
//				temp.parse_average(ave_buf);
//
////				temp.average_value = (long)i;
//
//				Reverse_Parse rp = new Reverse_Parse();
//				ave_buf = rp.rev_parse_tuple_average(temp);

                pt_writer.write(ave_buf);
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
        Generate_Sort_Initial_Table gsit =
                new Generate_Sort_Initial_Table();
        gsit.generate();

    }
}
