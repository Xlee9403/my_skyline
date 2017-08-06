package src.algrithm;

import src.data_structure.Reverse_Parse;
import src.data_structure.Tuple;
import src.global.Info;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Xue on 2017/7/26.
 */
public class MY_RPID_Improve_Bitmap
{
    //记录I/O数
    int in_out_count = 0;

    //记录skyline结果数
    int skyline_result = 0;

    //记录自剪后的候选集剩余的元组数
    int candidate_prune = 0;

    //记录被传递支配的元组位图
    byte[] trans_prune_bitmap = new byte[Info.TUPLE_NUMBER / 8];

    //先自剪
    public void prune_candidate()
    {
        for (int i = 0 ; i < trans_prune_bitmap.length ; i++)
            trans_prune_bitmap[i] = 0;

        try
        {
            //---------------将用来剪切的元组读入-------------//
            BufferedInputStream prune_reader =
                    new BufferedInputStream(
                    new FileInputStream( Info.ROOT_PATH
                            + Info.PRUNE_BLOCK_PATH));

            byte[] prune_buf =
                    new byte[Info.TUPLE_INITIAL_BYTES_LENGTH];
            int pruneToRead = prune_buf.length;
            int bytesRead = 0;

            byte[] size_buf = new byte[Info.ATTRIBUTE_BYTES_LENGTH];
            int sizeToRead = size_buf.length;

            while (bytesRead < sizeToRead)
                bytesRead += prune_reader.read(size_buf , bytesRead ,
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

            System.out.println(block_size);

            //存放用来剪切的元组
            Tuple[] prune_arr = new Tuple[(int) block_size];

            for (int i = 0 ; i < block_size ; i ++)
            {
                while (bytesRead < pruneToRead)
                    bytesRead += prune_reader.read(
                            prune_buf, bytesRead ,
                            pruneToRead - bytesRead);
                bytesRead = 0;

                in_out_count ++ ;

                prune_arr[i] = new Tuple();
                prune_arr[i].parse_initial(prune_buf);

            }

            prune_reader.close();
            /////////////------------------------//////////////

            BufferedInputStream scan_reader =
                    new BufferedInputStream(new
                    FileInputStream(Info.ROOT_PATH
                    + Info.sort_prefix
                    + Info.SCAN_AVERAGE_BUCKET_TABLE_PATH));

            byte[] scan_buf =
                    new byte[Info.TUPLE_AVERAGE_BUCKET_BYTES_LENGTH];
            int scanToRead = scan_buf.length;

            BufferedOutputStream writer = new BufferedOutputStream(new
                    FileOutputStream(Info.ROOT_PATH
                    + Info.SCAN_CANDIDATE_TABLE_PATH));

            int partition_num = Info.TUPLE_NUMBER / Info.BLOCK_SIZE;

            for (int i = 0 ; i < partition_num ; i++)
            {
                ArrayList<Tuple> arr = new ArrayList<>();

                //将元组按块读出，并放入arr中
                for (int j = 0 ; j < Info.BLOCK_SIZE ; j++)
                {
                    while (bytesRead < scanToRead)
                        bytesRead += scan_reader.read(
                                scan_buf , bytesRead ,
                                scanToRead - bytesRead);
                    bytesRead = 0;

                    in_out_count ++ ;

                    Tuple temp = new Tuple();
                    temp.parse_average_bucket(scan_buf);

                    //为每个元组设置临时标号，便于标记支配位图
                    temp.temp_position = i * Info.BLOCK_SIZE + j ;

                    arr.add(temp);
                }

                //进行块内比较
                for (int j = 0 ; j < arr.size() ; j ++)
                {
                    for (int k = 0 ; k < arr.size() ; k ++)
                    {
                        Dominate_Compute dc =
                                new Dominate_Compute();
                        int result = dc.dominate_comp(
                                arr.get(j),arr.get(k));

                        //J支配K
                        if (result == 1)
                        {
                            //J和K具有相同的位图，则直接移除K
                            if (arr.get(j).bucket_index ==
                                    arr.get(k).bucket_index)
                            {
                                int position = arr.get(k).temp_position;

                                trans_prune_bitmap[position/8] |=
                                        (1 << (8 - 1 - position % 8));

                                arr.remove(k);
                                k --;
                            }
                            else
                                arr.get(k).dominated = true;
                        }
                        //J被K支配
                        else if (result == 0)
                        {
                            //J和K具有相同的位图，则直接移除J
                            if (arr.get(j).bucket_index ==
                                    arr.get(k).bucket_index)
                            {
                                int position = arr.get(j).temp_position;

                                trans_prune_bitmap[position/8] |=
                                        (1 << (8 - 1 - position % 8));

                                arr.remove(j);
                                j --;

                                break;
                            }
                            else
                                arr.get(j).dominated = true;
                        }
                    }
                }
                //剪去被标记为true的元组
                for (int j = 0 ; j < arr.size() ; j++)
                {
                    if (arr.get(j).dominated == true)
                    {
                        arr.remove(j);
                        j --;
                    }
                }

                //用剪切块，剪切自减剩下的元组
                for (int k = 0 ; k < arr.size() ; k++)
                {
                    for (int j = 0 ; j < prune_arr.length ; j++)
                    {
                        Dominate_Compute dc= new Dominate_Compute();
                        int result = dc.dominate_comp(
                                arr.get(k) , prune_arr[j]);

                        //被剪切块支配
                        if (result == 0)
                        {
                            arr.remove(k);

                            k --;
                            break;
                        }
                    }
                }

                //将剩余元组写回
                for (int k = 0 ; k < arr.size() ; k++)
                {
                    Reverse_Parse rp = new Reverse_Parse();
                    scan_buf = rp.rev_parse_tuple_bucket_average
                            (arr.get(k));

                    writer.write(scan_buf);

                    in_out_count ++ ;
                }

                candidate_prune += arr.size();
            }

            System.out.println("自剪后剩下的元组数为：" + candidate_prune);
            System.out.println("自剪产生的I/O数为：" + in_out_count);

            writer.flush();
            writer.close();

            scan_reader.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void generate_base_skyline()
    {
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
        try
        {
            BufferedInputStream cand_reader = new BufferedInputStream(new
                    FileInputStream(Info.ROOT_PATH
                    + Info.SCAN_CANDIDATE_TABLE_PATH));

            byte[] cand_buf = new byte[Info.TUPLE_AVERAGE_BUCKET_BYTES_LENGTH];
            int candToRead = cand_buf.length;
            int bytesRead = 0;

            for (int i = 0 ; i < candidate_prune ; i++)
            {
                while (bytesRead < candToRead)
                    bytesRead += cand_reader.read(cand_buf , bytesRead ,
                            candToRead - bytesRead);
                bytesRead = 0;

                in_out_count ++ ;

                Tuple temp = new Tuple();
                temp.parse_average_bucket(cand_buf);

                boolean flag = false;

                //----------------------初始化要读的位图-------------------------//
                BufferedInputStream[] bit_reader =
                        new BufferedInputStream[Info.RELATED_ATTRIBUTES_NUMBER];

                for (int j = 0; j < Info.RELATED_ATTRIBUTES_NUMBER ; j++)
                {
                    if (temp.attributes[j] != Long.MIN_VALUE)
                    {
                        int temp_index = (int)(temp.attributes[j]);
                        for (int k = 0; k < bitmap_count; k++)
                        {
                            if (temp_index <= Math.pow(Info.LOGARITHMIC_BASE, k + 1))
                            {
                                bit_reader[j] = new BufferedInputStream(new
                                        FileInputStream(Info.ROOT_PATH
                                        + Info.BITMAP_ROOT + Info.BITMAP_PATH
                                        + j + Info.INTERVAL
                                        + k + Info.extension));

                                break;
                            }
                        }
                    }

                }
                //////////////------------------------------------///////////////

                //读取比较集
                BufferedInputStream compare_reader = new BufferedInputStream(new
                        FileInputStream(Info.ROOT_PATH
                        + Info.SCAN_COMPARE_TABLE_PATH));

                byte[] comp_buf = new byte[Info.TUPLE_AVERAGE_BUCKET_BYTES_LENGTH];
                int compToRead = comp_buf.length;

                byte[] prune_bitmap = new byte[Info.BIT_READ_SIZE];
                for (int j = 0 ; j < Info.TUPLE_NUMBER ; j++)
                {
                    //按块读取位图
                    if (j % Info.BLOCK_SIZE == 0)
                    {
                        Arrays.fill(prune_bitmap , (byte) 0);

                        byte[] temp_prune = new byte[Info.BIT_READ_SIZE];
                        int readlength =  temp_prune.length;
                        for (int k = 0; k < Info.RELATED_ATTRIBUTES_NUMBER; k ++)
                        {
                            if (temp.attributes[k] != Long.MIN_VALUE)
                            {
                                while (bytesRead < temp_prune.length)
                                    bytesRead += bit_reader[k].read(temp_prune , bytesRead ,
                                            readlength - bytesRead);
                                bytesRead = 0;

                                for (int m = 0; m < Info.BIT_READ_SIZE; m++)
                                    prune_bitmap[m] |= temp_prune[m];
                            }
                        }
                        for (int m = 0; m < Info.BIT_READ_SIZE; m++)
                        {
                            //按位取非
                            prune_bitmap[m] = (byte) (~prune_bitmap[m]);

                            trans_prune_bitmap[j/Info.BLOCK_SIZE + m]
                                    |=  prune_bitmap[m];
                        }
                    }
                    if ((trans_prune_bitmap[j / 8] & (1 << (8 - 1 - j % 8))) == 0)
                    {
                        while (bytesRead < compToRead)
                            bytesRead += compare_reader.read(comp_buf , bytesRead ,
                                    compToRead - bytesRead);
                        bytesRead = 0;

                        in_out_count ++ ;

                        Tuple comp_temp = new Tuple();
                        comp_temp.parse_average_bucket(comp_buf);

                        Dominate_Compute dc = new Dominate_Compute();
                        int result = dc.dominate_comp(comp_temp , temp);

                        if (result == 1)
                        {
                            flag = true;

                            break;
                        }
                    }
                    else
                    {
                        int skip_length = Info.TUPLE_AVERAGE_BUCKET_BYTES_LENGTH;
                        while (skip_length > 0)
                            skip_length -= compare_reader.skip(skip_length);
                    }
                }

                for (int j = 0; j < Info.RELATED_ATTRIBUTES_NUMBER ; j++)
                {
                    if (temp.attributes[j] != Long.MIN_VALUE)
                        bit_reader[j].close();
                }

                if (!flag)
                {
                    skyline_result ++;

                   System.out.print(temp);
                }

                compare_reader.close();

            }

            System.out.println("Skyline结果数为：" + skyline_result);
            System.out.println("产生的I/O数为：" + in_out_count);

            cand_reader.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        long startTime = System.currentTimeMillis();

        MY_RPID_Improve_Bitmap mib = new MY_RPID_Improve_Bitmap();
        mib.prune_candidate();

        mib.generate_base_skyline();

        long endTime = System.currentTimeMillis();

        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");

    }
}
