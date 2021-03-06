package src.algrithm;

import src.data_structure.Compare_Tuple_Average_Value;
import src.data_structure.Reverse_Parse;
import src.data_structure.Tuple;
import src.global.Info;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Xue on 2017/7/27.
 */
public class test
{
    int partition_num = Info.TUPLE_NUMBER / Info.BLOCK_SIZE;

    //记录I/O数
    int in_out_count = 0;

    //记录自剪后的候选集剩余的元组数
    int candidate_prune = 0;

    //记录shadow中的元组数
    int shadow_count = 0;


    public void prune_candidate()
    {
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
                    bytesRead += prune_reader.read(prune_buf, bytesRead ,
                            pruneToRead - bytesRead);
                bytesRead = 0;

                in_out_count ++ ;

                prune_arr[i] = new Tuple();
                prune_arr[i].parse_initial(prune_buf);

            }

            prune_reader.close();
            /////////////------------------------//////////////

            BufferedInputStream scan_reader =
                    new BufferedInputStream(new FileInputStream(
                            Info.ROOT_PATH + Info.sort_prefix
                                    + Info.SCAN_AVERAGE_BUCKET_TABLE_PATH));

            byte[] scan_buf =
                    new byte[Info.TUPLE_AVERAGE_BUCKET_BYTES_LENGTH];
            int scanToRead = scan_buf.length;

            BufferedOutputStream writer = new BufferedOutputStream(new
                    FileOutputStream(Info.ROOT_PATH
                    + Info.SCAN_CANDIDATE_TABLE_PATH));

            //写shadow
            BufferedOutputStream shadow_writer = new BufferedOutputStream(
                    new FileOutputStream(Info.ROOT_PATH + Info.SHADOW_ROOT
                    + Info.SHADOW_SKYLINE_PATH));

            for (int i = 0; i < partition_num; i++)
            {
                Tuple[] arr = new Tuple[Info.BLOCK_SIZE];

                //将元组按块读出，并放入arr中
                for (int j = 0; j < Info.BLOCK_SIZE; j++)
                {
                    while (bytesRead < scanToRead)
                        bytesRead += scan_reader.read(scan_buf, bytesRead,
                                scanToRead - bytesRead);
                    bytesRead = 0;

                    in_out_count ++ ;

                    Tuple temp = new Tuple();
                    temp.parse_average_bucket(scan_buf);

                    arr[j] = new Tuple();
                    arr[j].copyfrom(temp);
                }

                //存放不被传递支配的元组
                byte[] shadow_buf;

                Reverse_Parse rp = new Reverse_Parse();

                //比较每个块中的元组
                //保证被支配元组只考虑一次，不管以何种方式被支配
                for (int j = 0; j < Info.BLOCK_SIZE; j++)
                {
                    //若当前元组已被支配，不再考虑
                    if (arr[j].dominated)
                        continue;

                    boolean if_trans_dominated = false;

                    for (int k = j + 1; k < Info.BLOCK_SIZE; k++)
                    {
                        //若当前元组已被支配，不再考虑
                        if (arr[k].dominated)
                            continue;

                        //比较两个元组的支配关系
                        Dominate_Compute dc = new Dominate_Compute();
                        int result = dc.dominate_comp(arr[j], arr[k]);

                        //J支配K
                        if (result == 1)
                        {
                            //将被支配的元组标记为ture
                            arr[k].dominated = true;

                            //桶号相同则直接删除，否则放入shadow
                            if (arr[j].bucket_index != arr[k].bucket_index)
                            {
                                arr[k].sub_index = i;

                                shadow_buf =
                                        rp.rev_parse_sub_tuple_bucket_average(arr[k]);
                                shadow_writer.write(shadow_buf);

                                shadow_count ++;
                                in_out_count ++;
                            }
                        }

                        //J被K支配
                        if (result == 0)
                        {
                            //将被支配的元组标记为ture
                            arr[j].dominated = true;

                            //记录是否为传递支配
                            if (arr[j].bucket_index == arr[k].bucket_index)
                                if_trans_dominated = true;
                        }


                    }
                    //若被支配，但非传递支配，则放入shadow
                    if ((!if_trans_dominated) && (arr[j].dominated))
                    {
                        arr[j].sub_index = i;

                        shadow_buf =
                                rp.rev_parse_sub_tuple_bucket_average(arr[j]);
                        shadow_writer.write(shadow_buf);

                        shadow_count ++;
                        in_out_count ++;
                    }
                }

                //用剪切元组进行剪切
                for (int j = 0; j < Info.BLOCK_SIZE; j++)
                {
                    if (arr[j].dominated)
                        continue;

                    for (int k = 0; k < prune_arr.length; k++)
                    {
                        Dominate_Compute dc = new Dominate_Compute();
                        int result = dc.dominate_comp(arr[j], prune_arr[k]);

                        //若J被支配则标记元组，并结束比较
                        if (result == 0)
                        {
                            arr[j].dominated = true;

                            //若非传递支配，则放入shadow
                            if (arr[j].bucket_index != prune_arr[k].bucket_index)
                            {
                                arr[j].sub_index = i;

                                shadow_buf =
                                        rp.rev_parse_sub_tuple_bucket_average(arr[j]);
                                shadow_writer.write(shadow_buf);

                                in_out_count ++;
                                shadow_count ++;
                            }

                            break;
                        }
                    }

                    //经过几轮比较，不被支配，写入候选集
                    if (arr[j].dominated == false)
                    {
                        scan_buf = rp.rev_parse_tuple_bucket_average(arr[j]);

                        writer.write(scan_buf);

                        in_out_count ++;

                        candidate_prune ++;
                    }
                }

            }

            shadow_writer.flush();
            shadow_writer.close();

            writer.flush();
            writer.close();

            scan_reader.close();

            System.out.println("自剪后候选集中的元组数为：" + candidate_prune);
            System.out.println("自剪后写入shadow中的元组数为：" + shadow_count);
            System.out.println("自剪产生的I/O数为：" + in_out_count);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //读取shadow
    public void generate_skyline()
    {
        try
        {
            BufferedInputStream cand_reader = new BufferedInputStream(new FileInputStream(
                    Info.ROOT_PATH + Info.SCAN_CANDIDATE_TABLE_PATH));

            byte[] cand_buf = new byte[Info.TUPLE_AVERAGE_BUCKET_BYTES_LENGTH];
            int candToRead = cand_buf.length;
            int bytesRead = 0;

            Tuple[] cand_arr = new Tuple[candidate_prune];

            //读出整个candidate_list
            for (int i = 0; i < candidate_prune; i++)
            {
                while (bytesRead < candToRead)
                    bytesRead += cand_reader.read(cand_buf, bytesRead,
                            candToRead - bytesRead);
                bytesRead = 0;

                in_out_count ++;

                cand_arr[i] = new Tuple();
                cand_arr[i].parse_average_bucket(cand_buf);
            }

            Dominate_Compute dc = new Dominate_Compute();
            //候选集内剪切
            for (int i = 0; i < cand_arr.length; i++)
            {
                for (int j = i + 1; j < cand_arr.length; j++)
                {
                    int result = dc.dominate_comp(cand_arr[i], cand_arr[j]);

                    //I支配J
                    if (result == 1)
                        cand_arr[j].dominated = true;

                        //I被J支配
                    else if (result == 0)
                        cand_arr[i].dominated = true;

                }
            }

            ArrayList<Tuple> cand_list = new ArrayList<>();

            for ( int i = 0; i < cand_arr.length; i++)
            {
                if (!cand_arr[i].dominated)
                    cand_list.add(cand_arr[i]);
            }

            System.out.println("剪切后剩下的候选元组数为：" + cand_list.size());

            //以归并的方式读取shadow,读取的每个元组与cand_list所有元组比较，被支配的直接丢弃
            BufferedInputStream shadow_reader = new BufferedInputStream(
                    new FileInputStream(Info.ROOT_PATH + Info.SHADOW_ROOT
                            + Info.SHADOW_SKYLINE_PATH));

            byte[] shadow_buf =
                    new byte[Info.TUPLE_SUB_AVERAGE_BUCKET_BYTES_LENGTH];
            int shadowToRead = shadow_buf.length;

            for (int i = 0; i < shadow_count; i++)
            {
                while (bytesRead < shadowToRead)
                    bytesRead += shadow_reader.read(shadow_buf, bytesRead,
                            shadowToRead - bytesRead);
                bytesRead = 0;

                in_out_count ++;


                Tuple temp = new Tuple();
                temp.parse_sub_average_bucket(shadow_buf);

                for (int j = 0; j < cand_list.size(); j++)
                {
                    int result = dc.dominate_comp(
                           temp, cand_list.get(j));

                    if (result == 1)
                    {
                        cand_list.remove(j);
                        j --;
                    }
                }
            }

            //输出skyline结果
            for (int i = 0; i < cand_list.size(); i++)
                System.out.print(cand_list.get(i));

            System.out.println("skyline结果数为：" + cand_list.size());
            System.out.println("自剪产生的I/O数为：" + in_out_count);


            shadow_reader.close();

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

        test tt = new test();
        tt.prune_candidate();
        tt.generate_skyline();

        long endTime = System.currentTimeMillis();

        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");

    }
}
