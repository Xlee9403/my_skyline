package src.fileop;

import src.data_structure.*;
import src.global.Info;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Xue on 2017/7/25.
 */
public class Generate_Average_Bucket
{
    //记录块和位图的对应关系
    ArrayList<Index_Bitmap> arr = new ArrayList<Index_Bitmap>();

    public void generate()
    {
        try
        {
            BufferedInputStream ini_reader = new BufferedInputStream(
                    new FileInputStream(Info.ROOT_PATH
                            + Info.SCAN_TABLE_PATH));

            byte[] ini_buf = new byte[Info.TUPLE_INITIAL_BYTES_LENGTH];
            int iniToRead = ini_buf.length;
            int bytesRead = 0;

            BufferedOutputStream ave_bucket_writer = new BufferedOutputStream(
                    new FileOutputStream(Info.ROOT_PATH
                            + Info.SCAN_AVERAGE_BUCKET_TABLE_PATH));
            byte[] ab_buf = new byte[Info.TUPLE_AVERAGE_BUCKET_BYTES_LENGTH];

            for (int i = 0 ; i < Info.TUPLE_NUMBER ;i ++)
            {
                while (bytesRead < iniToRead)
                    bytesRead += ini_reader.read(ini_buf , bytesRead ,
                            iniToRead - bytesRead);
                bytesRead = 0;

                Tuple temp = new Tuple();
                temp.parse_initial(ini_buf);

                //-------------给定当前元组的位图标号，计算平均值----------------//
                //确定当前元组的表示位图
                Index_Bitmap ib_temp = new Index_Bitmap();
                ib_temp.bitmap = 0;

                int complete_count = 0;
                long attribute_add = 0;

                //确定表示当前元组的位图
                for (int j = 0 ; j < Info.ATTRIBUTE_NUMBER ; j ++)
                {
                    if (temp.attributes[j] != Long.MIN_VALUE)
                    {
                        ib_temp.bitmap |= 1 << (8 - 1 - (j + 1));

                        complete_count ++;

                        attribute_add += temp.attributes[j];
                    }
                }

                temp.average_value = attribute_add / complete_count;

                //标记当前位图是否已经存在
                boolean is_exist = false;

                //判断当前位图是否存在，若存在在哪个位置
                for (int j = 0 ; j < arr.size() ; j ++)
                {
                    //若存在，则自加
                    if (arr.get(j).bitmap == ib_temp.bitmap)
                    {
                        ib_temp.block_num = arr.get(j).block_num;

                        arr.get(j).block_size ++;

                        is_exist = true;
                        break;
                    }
                }

                if (is_exist == false)
                {
                    temp.bucket_index = arr.size();

                    ib_temp.block_num = arr.size();
                    arr.add(ib_temp);
                }
                else
                    temp.bucket_index = ib_temp.block_num;
                //////////////////------------------//////////////////

                Reverse_Parse rp = new Reverse_Parse();
                ab_buf = rp.rev_parse_tuple_bucket_average(temp);

                ave_bucket_writer.write(ab_buf);
            }

            ave_bucket_writer.flush();
            ave_bucket_writer.close();
            ini_reader.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        long startTime = System.currentTimeMillis();

        Generate_Average_Bucket gab = new Generate_Average_Bucket();
        gab.generate();

        long endTime = System.currentTimeMillis();

        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
    }
}
