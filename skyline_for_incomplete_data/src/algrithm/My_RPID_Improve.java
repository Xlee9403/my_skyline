package src.algrithm;

import src.data_structure.Compare_Tuple_Average_Value;
import src.data_structure.Reverse_Parse;
import src.data_structure.Tuple;
import src.global.Info;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Xue on 2017/8/6.
 */
public class My_RPID_Improve
{
    int partition_num = Info.TUPLE_NUMBER / Info.BLOCK_SIZE;

    //记录I/O数
    int in_out_count = 0;

    //记录skyline结果数
    int skyline_result = 0;

    //记录自剪后的候选集剩余的元组数
    int candidate_prune = 0;

    //记录shadow中的元组数
    int shadow_count = 0;

    int[] shadow_num = new int[partition_num];

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
            BufferedOutputStream[] shadow_writer = new BufferedOutputStream[partition_num];

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
                ArrayList<Tuple> shadow = new ArrayList<>();
                byte[] shadow_buf =
                        new byte[Info.TUPLE_SUB_AVERAGE_BUCKET_BYTES_LENGTH];

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
                                shadow.add(arr[k]);
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
                        shadow.add(arr[j]);
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
                                shadow.add(arr[j]);
                            }

                            break;
                        }
                    }

                    //经过几轮比较，不被支配，写入候选集
                    if (arr[j].dominated == false)
                    {
                        Reverse_Parse rp = new Reverse_Parse();
                        scan_buf = rp.rev_parse_tuple_bucket_average(arr[j]);

                        writer.write(scan_buf);

                        in_out_count ++;

                        candidate_prune ++;
                    }
                }

                //对shadow按照平均值排序后写回,先放入数组，再排序
                Tuple[] shadow_arr = new Tuple[shadow.size()];
                for (int j = 0; j < shadow.size(); j++)
                {
                    shadow_arr[j] = new Tuple();
                    shadow_arr[j].copyfrom(shadow.get(j));
                }
                Compare_Tuple_Average_Value ctav = new Compare_Tuple_Average_Value();
                Arrays.sort(shadow_arr, ctav);

                shadow_count += shadow.size();
                shadow_num[i] = shadow.size();

                //写入shadow
               shadow_writer[i] = new BufferedOutputStream(new FileOutputStream(
                       Info.ROOT_PATH + Info.SHADOW_ROOT
                               + Info.SHADOW_PATH + i + Info.extension));
                for (int j = 0; j < shadow.size(); j++)
                {
                    Reverse_Parse rp = new Reverse_Parse();
                    shadow_buf =
                            rp.rev_parse_sub_tuple_bucket_average(shadow_arr[j]);

                    shadow_writer[i].write(shadow_buf);

                    in_out_count ++;
                }

                shadow_writer[i].flush();
                shadow_writer[i].close();
            }

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

    //以归并的方式读取shadow
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
            BufferedInputStream[] shadow_reader = new BufferedInputStream[partition_num];
            for (int i = 0; i < partition_num; i++)
                shadow_reader[i] = new BufferedInputStream(new FileInputStream(
                        Info.ROOT_PATH + Info.SHADOW_ROOT
                                + Info.SHADOW_PATH + i + Info.extension));

            byte[] shadow_buf =
                    new byte[Info.TUPLE_SUB_AVERAGE_BUCKET_BYTES_LENGTH];
            int shadowToRead = shadow_buf.length;

            Tuple[] min_subtable = new Tuple[partition_num];

            //从每个子表中读出一个
            for (int j = 0; j < min_subtable.length; j++)
            {
                while (bytesRead < shadowToRead)
                    bytesRead += shadow_reader[j].read(shadow_buf,
                            bytesRead, shadowToRead - bytesRead);
                bytesRead = 0;

                in_out_count ++;

                shadow_num[j]--;

                min_subtable[j] = new Tuple();
                min_subtable[j].parse_sub_average_bucket(shadow_buf);
            }

            Compare_Tuple_Average_Value ctav = new Compare_Tuple_Average_Value();

            for (int i = 0; i < shadow_count; i++)
            {
                Arrays.sort(min_subtable, ctav);

                for (int j = 0; j < cand_list.size(); j++)
                {
                    int result = dc.dominate_comp(
                            min_subtable[0], cand_list.get(j));

                    if (result == 1)
                    {
                        cand_list.remove(j);
                        j --;
                    }
                }

                int read_next = (int) min_subtable[0].sub_index;

                //找当前最小值所在的子表中读取一个，若该子表为空，则置为最大值
                if (shadow_num[read_next] != 0)
                {
                    while (bytesRead < shadowToRead)
                        bytesRead += shadow_reader[read_next].read(
                                shadow_buf, bytesRead, shadowToRead - bytesRead);
                    bytesRead = 0;

                    in_out_count ++;

                    shadow_num[read_next]--;

                    Tuple temp = new Tuple();
                    temp.parse_sub_average_bucket(shadow_buf);

                    min_subtable[0].copyfrom(temp);
                }
                else
                    min_subtable[0].bucket_index = Long.MAX_VALUE;


            }

            //输出skyline结果
            for (int i = 0; i < cand_list.size(); i++)
                System.out.print(cand_list.get(i));

            System.out.println("skyline结果数为：" + cand_list.size());
            System.out.println("自剪产生的I/O数为：" + in_out_count);

            for (int i = 0; i < partition_num; i++)
                shadow_reader[i].close();

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

        My_RPID_Improve mri = new My_RPID_Improve();
        mri.prune_candidate();

        mri.generate_skyline();

        long endTime = System.currentTimeMillis();

        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
    }
}
