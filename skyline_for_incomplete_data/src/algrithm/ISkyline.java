package src.algrithm;

import src.data_structure.Index_Bitmap;
import src.data_structure.Reverse_Parse;
import src.data_structure.Tuple;
import src.global.Info;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import static src.global.Info.RELATED_ATTRIBUTES_NUMBER;
import static src.global.Info.ROOT_PATH;

/**
 * Created by Xue on 2017/7/19.
 */
public class ISkyline
{
    //记录块和位图的对应关系
    ArrayList<Index_Bitmap> arr = new ArrayList<Index_Bitmap>();

    //将大表按照元组位图表示，并分组写入
    public void divide_block()
    {
        try
        {
            //读初始表
            BufferedInputStream pt_reader = new BufferedInputStream(
                    new FileInputStream(Info.ROOT_PATH
                            + Info.INITIAL_TABLE_PATH));

            byte[] buf = new byte[Info.TUPLE_INITIAL_BYTES_LENGTH];
            int bytesToRead = buf.length;
            int bytesRead = 0;

            //初始化写缓冲区
            int writer_num = (int) Math.pow(2, Info.RELATED_ATTRIBUTES_NUMBER);
            BufferedOutputStream[] bucket_writer =
                    new BufferedOutputStream[writer_num];

            for (int i = 0 ; i < Info.TUPLE_NUMBER ; i ++)
            {
                while (bytesRead < bytesToRead)
                    bytesRead += pt_reader.read(buf , bytesRead ,
                            bytesToRead - bytesRead);
                bytesRead = 0;

                Tuple temp = new Tuple();
                temp.parse_initial(buf);

                Index_Bitmap ib_temp = new Index_Bitmap();

                //确定表示当前元组的位图
                for (int j = 0 ; j < Info.ATTRIBUTE_NUMBER ; j ++)
                {
                    if (temp.attributes[j] != Long.MIN_VALUE)
                        ib_temp.bitmap |= 1 << (8 - 1 - j);
                }

                boolean is_exist = false;

                int exist_num = 0;

                for (int j = 0 ; j < arr.size() ; j ++)
                {
                    //若存在，则自加
                    if (arr.get(j).bitmap == ib_temp.bitmap)
                    {
                        ib_temp.block_num = arr.get(j).block_num;

                        arr.get(j).block_size ++;

                        is_exist = true;

                        exist_num = j;
                        break;
                    }
                }

                byte[] write_buf = new byte[Info.TUPLE_BUCKET_BYTES_LENGTH];

                //当前位图，刚出现，初始化写
                if (is_exist == false)
                {
                    bucket_writer[arr.size()] = new BufferedOutputStream(
                            new FileOutputStream(Info.ROOT_PATH
                                    + Info.BUCKET_ROOT + Info.BUCKET_PATH +
                                    arr.size() + Info.extension));

                    temp.bucket_index = arr.size();
                    Reverse_Parse rp = new Reverse_Parse();
                    write_buf = rp.rev_parse_tuple_with_bucket_num(temp);

                    bucket_writer[arr.size()].write(write_buf);

                    ib_temp.block_num = arr.size();
                    ib_temp.block_size ++;
                    arr.add(ib_temp);
                }
                else
                {
                    temp.bucket_index = ib_temp.block_num;
                    Reverse_Parse rp = new Reverse_Parse();
                    write_buf = rp.rev_parse_tuple_with_bucket_num(temp);

                    bucket_writer[ib_temp.block_num].write(write_buf);


                }
            }
            for (int i = 0 ; i < arr.size() ; i ++)
            {
                bucket_writer[i].flush();
                bucket_writer[i].close();
            }

            pt_reader.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void generate_skyline()
    {

    }

    public static void main(String[] args)
    {
        long startTime = System.currentTimeMillis();

        ISkyline is = new ISkyline();
        is.divide_block();

        long endTime = System.currentTimeMillis();

        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");

    }
}
