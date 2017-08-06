package src.fileop;

import java.io.*;

import src.data_structure.*;
import src.global.Info;

/**
 * Created by Xue on 2017/7/25.
 */
public class Genarate_Prune_Bitmap
{
    //自顶向下建立位图
    public void generate_bitmap()
    {
        try
        {
            //------------------------读已排序表-----------------------------//
            BufferedInputStream pt_reader = new BufferedInputStream(new
                    FileInputStream(Info.ROOT_PATH
                    + Info.SCAN_COMPARE_TABLE_PATH));

            byte[] tuple_buf = new byte[Info.TUPLE_AVERAGE_BUCKET_BYTES_LENGTH];
            int tupleToRead = tuple_buf.length;
            int tupleRead = 0;
            /////////////------------------------------------------/////////

            //-----------------每列位图的总数量，自顶向下建立位图-------------------//
            int bitmap_count;
            if ((Math.log((double)Info.TUPLE_NUMBER)/
                    Math.log(Info.LOGARITHMIC_BASE)) == 0)
            {
                bitmap_count = (int)(Math.log((double)Info.TUPLE_NUMBER)/
                        Math.log(Info.LOGARITHMIC_BASE));
            }
            else
                bitmap_count = (int)(Math.log((double)Info.TUPLE_NUMBER)/
                        Math.log(Info.LOGARITHMIC_BASE)) + 1;
            /////////////////----------------------------------///////////////

            //-------------------------写位图------------------------------//
            BufferedOutputStream[][] bit_writer = new BufferedOutputStream
                    [Info.ATTRIBUTE_NUMBER][bitmap_count];

            for (int i = 0; i < Info.ATTRIBUTE_NUMBER; i++)
            {
                for (int j = 0; j < bitmap_count; j++)
                {
                    bit_writer[i][j] = new BufferedOutputStream(
                            new FileOutputStream(Info.ROOT_PATH +
                                    Info.BITMAP_ROOT + Info.BITMAP_PATH
                                     + i + Info.INTERVAL
                                    + j + Info.extension));
                }
            }
            //每块包含的元组数
            int tuple_num_per_block = Info.BIT_BLOCK_SIZE * 8;
            ////////////////-------------------------------------////////////////

            //未读取的元组数
            int unread_tuple_number = Info.TUPLE_NUMBER;

            //已读取的块数
            int block_count = 0;

            //最后一个位图的byte数
            int byte_count = 0;

            while (unread_tuple_number != 0)
            {
                //位图 缓冲区
                BMBuffer[][] bitmap = new BMBuffer
                        [Info.ATTRIBUTE_NUMBER][bitmap_count];
                for (int i = 0; i < Info.ATTRIBUTE_NUMBER; i++)
                {
                    for (int j = 0; j < bitmap_count; j++)
                    {
                        bitmap[i][j] = new BMBuffer();
                        for (int k = 0; k < bitmap[i][j].buffew.length; k++)
                        {
                            bitmap[i][j].buffew[k] = 0;
                        }
                    }
                }

                System.out.println(block_count + ":" + unread_tuple_number
                        + ":" + Info.TUPLE_NUMBER);

                block_count ++;

                //读一块包含的元组数
                for (int i = 0; i < tuple_num_per_block; i++)
                {
                    //读出一个元组并解析
                    while (tupleRead < tupleToRead)
                        tupleRead += pt_reader.read(tuple_buf,
                                tupleRead, tupleToRead - tupleRead);
                    tupleRead = 0;

                    Tuple temp = new Tuple();
                    temp.parse_average_bucket(tuple_buf);

                    unread_tuple_number --;

                    for (int j = 0; j < Info.ATTRIBUTE_NUMBER; j++)
                    {
                        int temp_index = (int)(temp.attributes[j]);

                        for (int k = 0; k < bitmap_count; k++)
                        {
                            if (temp_index <= Math.pow(Info.LOGARITHMIC_BASE, k + 1))
                            {
                                bitmap[j][k].buffew[i / 8]
                                        |= 1 << (8 - 1 - i % 8);
                            }

                        }
                    }

                    if (unread_tuple_number == 0)
                    {
                        byte_count = (int)Math.ceil(1.0 * i /
                                Info.ATTRIBUTE_BYTES_LENGTH);
                        break;
                    }
                }
                //写入
                if (unread_tuple_number != 0)
                {
                    for (int i = 0; i < Info.ATTRIBUTE_NUMBER; i++)
                    {
                        for (int j = 0; j < bitmap_count; j++)
                        {
                            bit_writer[i][j].write(bitmap[i][j].buffew);
                        }
                    }
                }
                else
                {
                    System.out.println(block_count);

                    for (int i = 0; i < Info.ATTRIBUTE_NUMBER; i++)
                    {
                        for (int j = 0; j < bitmap_count; j++)
                        {
                            bit_writer[i][j].write(bitmap[i][j].buffew,
                                    0,byte_count);
                        }
                    }
                }

            }

            pt_reader.close();

            for (int i = 0; i < Info.ATTRIBUTE_NUMBER; i++)
            {
                for (int j = 0; j < bitmap_count; j++)
                {
                    bit_writer[i][j].flush();
                    bit_writer[i][j].close();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public static void main(String[] args)
    {
        long startTime = System.currentTimeMillis();

        Genarate_Prune_Bitmap gpb = new Genarate_Prune_Bitmap();
        gpb.generate_bitmap();

        long endTime = System.currentTimeMillis();

        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
    }
}
