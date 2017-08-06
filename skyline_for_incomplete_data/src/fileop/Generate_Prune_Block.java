package src.fileop;

import src.data_structure.Column;
import src.data_structure.Tuple;
import src.global.Info;

import java.io.*;
import java.util.*;

/**
 * Created by Xue on 2017/7/25.
 */
public class Generate_Prune_Block
{
    //存放用来剪切的元组的PI值
    ArrayList<Long> prune_arr = new ArrayList<>();

    public void generate_pi()
    {
        try
        {
            //---------都已排序的列文件，并获取各个维度的前10个，去重------------//
            //初始化读已排序的列文件
            BufferedInputStream[] column_reader =
                    new BufferedInputStream[Info.ATTRIBUTE_NUMBER];
            for (int i = 0 ; i < column_reader.length ; i ++)
                column_reader[i] = new BufferedInputStream(
                        new FileInputStream(Info.ROOT_PATH +
                        Info.COLUMN_ROOT + Info.COLUMN_SORT_PATH
                        + i + Info.extension));

            byte[] column_buf = new byte[Info.COLUMN_BYTES_LENGTH];
            int colToRead = column_buf.length;
            int bytesRead = 0;

            for (int i = 0 ; i < Info.ATTRIBUTE_NUMBER ; i ++ )
            {
                for (int j = 0 ; j < Info.PRUNE_NUM ; j ++)
                {
                    while (bytesRead < colToRead)
                        bytesRead += column_reader[i].read(column_buf,
                                bytesRead , colToRead - bytesRead);
                    bytesRead = 0;

                    Column col_temp = new Column();
                    col_temp.parse_column(column_buf);

                    boolean isexist = false;

                    for (int k = 0 ; k < prune_arr.size() ; k ++)
                    {
                        if (col_temp.pi == prune_arr.get(k))
                        {
                            isexist = true;

                            break;
                        }
                    }

                    if (isexist == false)
                        prune_arr.add(col_temp.pi);
                }

                column_reader[i].close();
            }
             ///////////////////---------------------////////////////////

            //-----------------读取已按ACD排序的前十个-------------------//
            BufferedInputStream reader = new BufferedInputStream(
                    new FileInputStream(Info.ROOT_PATH
                            + Info.SORTED_AVERAGE_TABLE_PATH));

            byte[] tuple_buf = new byte[Info.TUPLE_AVERAGE_BYTES_LENGTH];
            int tupToRead = tuple_buf.length;

            for (int i = 0 ; i < Info.PRUNE_NUM ; i ++)
            {
                while (bytesRead < tupToRead)
                    bytesRead += reader.read(tuple_buf, bytesRead ,
                            tupToRead - bytesRead);
                bytesRead = 0;

                Tuple temp = new Tuple();
                temp.parse_average(tuple_buf);

                boolean isexist = false;

                for (int k = 0 ; k < prune_arr.size() ; k ++)
                {
                    if (temp.position_index == prune_arr.get(k))
                    {
                        isexist = true;

                        break;
                    }
                }

                if (isexist == false)
                    prune_arr.add(temp.position_index);
            }
            reader.close();
            //////////////////---------------------////////////////////
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void generate_block()
    {
        //将剪切块的pi值排序
        long[] sort_pi = new long[prune_arr.size()];
        for (int i = 0 ; i < sort_pi.length ; i ++)
            sort_pi[i] = prune_arr.get(i);

        Arrays.sort(sort_pi);

        prune_arr.clear();
        for (int i = 0 ; i < sort_pi.length ; i ++)
            prune_arr.add(sort_pi[i]);

        try
        {
            BufferedInputStream pt_reader = new BufferedInputStream(
                    new FileInputStream(Info.ROOT_PATH
                            + Info.SCAN_TABLE_PATH));

            byte[] pt_buf =
                    new byte[Info.TUPLE_INITIAL_BYTES_LENGTH];
            int bytesToRead = pt_buf.length;
            int bytesRead = 0;

            BufferedOutputStream block_writer = new BufferedOutputStream(
                    new FileOutputStream(Info.ROOT_PATH
                            + Info.PRUNE_BLOCK_PATH));

            //将剪切块的size先写入
            byte[] buf = new byte[Info.ATTRIBUTE_BYTES_LENGTH];

            buf[0] = (byte)(0xff & ((long)prune_arr.size() >> 56));
            buf[1] = (byte)(0xff & ((long)prune_arr.size() >> 48));
            buf[2] = (byte)(0xff & ((long)prune_arr.size() >> 40));
            buf[3] = (byte)(0xff & ((long)prune_arr.size() >> 32));
            buf[4] = (byte)(0xff & ((long)prune_arr.size() >> 24));
            buf[5] = (byte)(0xff & ((long)prune_arr.size() >> 16));
            buf[6] = (byte)(0xff & ((long)prune_arr.size() >>  8));
            buf[7] = (byte)(0xff & (long)prune_arr.size());

            System.out.println(prune_arr.size());

            block_writer.write(buf);

            //写入符合条件的元组
            for (int i = 0 ; i < sort_pi.length ; i ++)
            {
                long skip_len = 0;

                if (i == 0)
                    skip_len =  sort_pi[i];
                else
                    skip_len = sort_pi[i] - sort_pi[i - 1] - 1;

                skip_len = skip_len * Info.TUPLE_INITIAL_BYTES_LENGTH;

                while (skip_len > 0)
                    skip_len -= pt_reader.skip(skip_len);

                while (bytesRead < bytesToRead)
                    bytesRead += pt_reader.read(pt_buf , bytesRead ,
                            bytesToRead - bytesRead);
                bytesRead = 0;

//                Tuple temp = new Tuple();
//                temp.parse_initial(pt_buf);
//                System.out.print(temp);

                block_writer.write(pt_buf);
            }

            block_writer.flush();
            block_writer.close();
            pt_reader.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void test()
    {
        try
        {
            BufferedInputStream reader = new BufferedInputStream(
                    new FileInputStream(Info.ROOT_PATH
                            + Info.PRUNE_BLOCK_PATH));

            byte[] size_buf = new byte[Info.ATTRIBUTE_BYTES_LENGTH];
            int sizeToRead = size_buf.length;
            int bytesRead = 0;

            while (bytesRead < sizeToRead)
                bytesRead += reader.read(size_buf , bytesRead ,
                        sizeToRead - bytesRead);
            bytesRead = 0;

            long block_size = (((long)(size_buf[0] & 0xff) << 56) |
                               ((long)(size_buf[1] & 0xff) << 48) |
                               ((long)(size_buf[2] & 0xff) << 40) |
                               ((long)(size_buf[3] & 0xff) << 32) |
                               ((long)(size_buf[4] & 0xff) << 24) |
                               ((long)(size_buf[5] & 0xff) << 16) |
                               ((long)(size_buf[6] & 0xff) <<  8) |
                               ((long)(size_buf[7] & 0xff)));

            byte[] tup_buf =
                    new byte[Info.TUPLE_INITIAL_BYTES_LENGTH];
            int bytesToRead = tup_buf.length;

            System.out.println(block_size);

            boolean flag = true;

            for (long i = 0 ; i < block_size ; i ++)
            {
                while (bytesRead < bytesToRead)
                    bytesRead += reader.read(tup_buf , bytesRead ,
                            bytesToRead - bytesRead);
                bytesRead = 0;

                Tuple temp = new Tuple();
                temp.parse_initial(tup_buf);

 //               System.out.print(temp);

                if (temp.position_index != prune_arr.get((int) i))
                {
                    flag = false;
                    System.out.println("something error");
                }
            }

            if (flag == true)
                System.out.println("nothing has problem");

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

        Generate_Prune_Block gpb = new Generate_Prune_Block();
        gpb.generate_pi();

        gpb.generate_block();

        gpb.test();

        long endTime = System.currentTimeMillis();

        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
    }
}
