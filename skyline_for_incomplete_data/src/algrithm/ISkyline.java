package src.algrithm;

import src.data_structure.*;
import src.global.Info;
import java.io.*;
import java.util.ArrayList;

/**
 * Created by Xue on 2017/7/19.
 */
public class ISkyline
{
    //记录I/O数
    int in_out_count = 0;

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

            //顺序读出初始表
            for (int i = 0 ; i < Info.TUPLE_NUMBER ; i ++)
            {
                while (bytesRead < bytesToRead)
                    bytesRead += pt_reader.read(buf , bytesRead ,
                            bytesToRead - bytesRead);
                bytesRead = 0;

                in_out_count ++;

                Tuple temp = new Tuple();
                temp.parse_initial(buf);

                Index_Bitmap ib_temp = new Index_Bitmap();
                ib_temp.bitmap = 0;

                //确定表示当前元组的位图
                for (int j = 0 ; j < Info.ATTRIBUTE_NUMBER ; j ++)
                    if (temp.attributes[j] != Long.MIN_VALUE)
                        ib_temp.bitmap |= 1 << (8 - 1 - (j + 1));

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
                byte[] write_buf;

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

                    in_out_count ++;

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

                    in_out_count ++;
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

        int tuple_count = 0;
        for (int i = 0 ; i < arr.size() ; i ++)
            tuple_count += arr.get(i).block_size;

        System.out.println("元组总个数为：" + tuple_count);
    }

    //计算每个桶内的skyline，并将得出的skyline写入文件
    public void generate_bucket_skyline()
    {
        try
        {
            //读桶
            BufferedInputStream[] bucket_reader =
                    new BufferedInputStream[arr.size()];
            for (int i = 0 ; i < bucket_reader.length ; i ++)
                bucket_reader[i] = new BufferedInputStream(new FileInputStream(
                        Info.ROOT_PATH + Info.BUCKET_ROOT
                                + Info.BUCKET_PATH + i + Info.extension));

            byte[] read_buf = new byte[Info.TUPLE_BUCKET_BYTES_LENGTH];
            int bytesToRead = read_buf.length;
            int bytesRead = 0;

            //将计算好的skyline写到文件
            BufferedOutputStream[] bucket_writer =
                    new BufferedOutputStream[arr.size()];
            for (int i = 0 ; i < bucket_writer.length ; i ++)
                bucket_writer[i] = new BufferedOutputStream(new FileOutputStream(
                        Info.ROOT_PATH + Info.BUCKET_ROOT +
                                Info.BUCKET_SKYLINE_PATH + i + Info.extension));

            byte[] write_buf;

            for (int i = 0 ; i < arr.size() ; i ++)
            {
                //记录写到桶的元组数
                int bucket_count = 0;

                //每次读出一个块，计算块内的skyline，并写回
                Tuple[] comp_skyline = new Tuple[arr.get(i).block_size];

                //将块内所有的元组都读入数组
                for (int j = 0 ; j < arr.get(i).block_size ; j ++)
                {
                    while (bytesRead < bytesToRead)
                        bytesRead += bucket_reader[i].read(read_buf , bytesRead ,
                                bytesToRead - bytesRead);
                    bytesRead = 0;

                   in_out_count ++;

                    comp_skyline[j] = new Tuple();
                    comp_skyline[j].parse_bucket(read_buf);
                }
                //计算块内skyline
                for (int j = 0 ; j < comp_skyline.length ; j ++)
                {
                    boolean dominated = false;

                    for (int k = 0 ; k < comp_skyline.length ; k ++)
                    {
                        if (j != k)
                        {
                            Dominate_Compute dc = new Dominate_Compute();
                            int dom = dc.dominate_comp(
                                    comp_skyline[k],comp_skyline[j]);

                            //当前元组被支配，标记被支配，跳出循环
                            if (dom == 1)
                            {
                                dominated = true;
                                break;
                            }
                        }
                    }
                    //不被支配，直接写入
                    if (dominated == false)
                    {
                        Reverse_Parse rp = new Reverse_Parse();
                        write_buf = rp.rev_parse_tuple_with_bucket_num(
                                comp_skyline[j]);

                        bucket_writer[i].write(write_buf);
                        bucket_count ++;

                        in_out_count ++;
                    }
                }
                arr.get(i).block_size = bucket_count;
            }
            for (int i = 0 ; i < arr.size() ; i ++)
            {
                bucket_writer[i].flush();
                bucket_writer[i].close();

                bucket_reader[i].close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        int tuple_count = 0;
        for (int i = 0 ; i < arr.size() ; i ++)
            tuple_count += arr.get(i).block_size;

        System.out.println("剪切掉的具有传递性的元组个数为："
                + (Info.TUPLE_NUMBER - tuple_count));
    }

    //按块读出当前已计算出的各个桶内skyline点，剪掉一部分写入shadow中
    //未剪掉部分作为候选集，生成result,最后将result集与shadow比较，剩余的元组即为最后的结果
    public void generate_skyline()
    {
        //存放各个桶的虚拟点
        Virtual_Points[] virtual = new Virtual_Points[arr.size()];
        for (int i = 0 ; i < virtual.length ; i ++)
        {
            virtual[i] = new Virtual_Points();
            virtual[i].index = i;
        }

        //存放候选集
        ArrayList<Tuple> candidate_arr = new ArrayList<Tuple>();

        //存放临时结果集
        ArrayList<Tuple> result_arr = new ArrayList<Tuple>();

        //记录shadow中的元组数
        int shadow_num = 0;

        try
        {
            BufferedInputStream[] bucket_reader =
                    new BufferedInputStream[arr.size()];

            byte[] read_buf = new byte[Info.TUPLE_BUCKET_BYTES_LENGTH];
            int bytesToRead = read_buf.length;
            int bytesRead = 0;

            BufferedOutputStream shadow_writer = new BufferedOutputStream(
                    new FileOutputStream(Info.ROOT_PATH +
                            Info.SHADOW_SKYLINE_PATH));

            byte[] write_buf;

            for (int i = 0 ; i < arr.size() ; i ++)
            {
                bucket_reader[i] = new BufferedInputStream(new FileInputStream(
                        Info.ROOT_PATH + Info.BUCKET_ROOT +
                                Info.BUCKET_SKYLINE_PATH+ i + Info.extension));

                //------------每次将块内的所有元组读出----------------//
                ArrayList<Tuple> bucket_arr = new ArrayList<>();
                for (int j = 0; j < arr.get(i).block_size; j ++)
                {
                    while (bytesRead < bytesToRead)
                        bytesRead += bucket_reader[i].read(read_buf , bytesRead ,
                                bytesToRead - bytesRead);
                    bytesRead = 0;

                    in_out_count ++;

                    Tuple temp = new Tuple();
                    temp.parse_bucket(read_buf);

                    bucket_arr.add(temp);
                }
                /////////////------------------------------///////////////

                //读出来后看看是否存在当前桶的虚拟点，若有，则先做一遍剪切
                for (int r = 0 ; r < virtual[i].vir_arr.size() ; r ++)
                {
                    for (int k = 0 ; k < bucket_arr.size() ; k ++)
                    {
                        Dominate_Compute dc = new Dominate_Compute();
                        int domi = dc.dominate_comp(
                                virtual[i].vir_arr.get(r),bucket_arr.get(k));

                        //被虚拟点支配的元组写入shadow
                        if (domi == 1)
                        {
                            Reverse_Parse rp = new Reverse_Parse();
                            write_buf = rp.rev_parse_tuple_with_bucket_num(
                                    bucket_arr.get(k));

                            shadow_writer.write(write_buf);
                            shadow_num ++;

                            in_out_count ++;

                            //从桶内移除当前元组，桶内元组数减一
                            bucket_arr.remove(k);
                            arr.get(i).block_size --;
                            k --;
                        }
                    }
                }
                for (int j = i+1 ; j < arr.size() ; j ++)
                {
                    bucket_reader[j] = new BufferedInputStream(new FileInputStream(
                            Info.ROOT_PATH + Info.BUCKET_ROOT +
                                    Info.BUCKET_SKYLINE_PATH + j + Info.extension));

                    //读取每个桶内的元组
                    for (int k = 0 ; k < arr.get(j).block_size ; k ++)
                    {
                        while (bytesRead < bytesToRead)
                            bytesRead += bucket_reader[j].read(read_buf , bytesRead ,
                                    bytesToRead - bytesRead);
                        bytesRead = 0;

                        in_out_count ++;

                        Tuple bucket_temp = new Tuple();
                        bucket_temp.parse_bucket(read_buf);

                        //用当前元组剪切当前桶
                        for (int l = 0 ; l < bucket_arr.size() ; l ++)
                        {
                            Dominate_Compute dc = new Dominate_Compute();
                            int domi = dc.dominate_comp(
                                    bucket_temp, bucket_arr.get(l) );

                            //若桶内元组被支配，直接放入shadow，shadow计数加一
                            //并将该元组从桶内移除
                            if(domi == 1)
                            {
                                Reverse_Parse rp = new Reverse_Parse();
                                write_buf = rp.rev_parse_tuple_with_bucket_num(
                                        bucket_arr.get(l));

                                shadow_writer.write(write_buf);
                                shadow_num ++;

                                in_out_count ++;

                                bucket_arr.remove(l);
                                arr.get(i).block_size --;
                                l --;
                            }

                            //若比较元组被支配，将虚拟点插入比较元组所在桶内
                            else if (domi == 0)
                            {
                                int bucket_index = (int) bucket_temp.bucket_index;

                                virtual[i].vir_arr.add(bucket_arr.get(l));
                            }
                        }
                    }
                    bucket_reader[j].close();
                }

                bucket_reader[i].close();

                //经过比较后，若桶内还有元组，则直接插入候选集
                for (int j = 0 ; j < bucket_arr.size() ; j ++)
                    candidate_arr.add(bucket_arr.get(j));
            }

            shadow_writer.flush();
            shadow_writer.close();

            //候选集内的元组进行比较得出result，再与shadow比较得出最后的final-result
            BufferedInputStream shadow_reader = new BufferedInputStream(new
                    FileInputStream(Info.ROOT_PATH
                    + Info.SHADOW_SKYLINE_PATH));

            System.out.println("用virtual剪切过的候选集中元组个数为："
                    + candidate_arr.size());

            //候选集内部比较
            for (int i = 0 ; i < candidate_arr.size() ; i ++)
            {
                boolean flag = false;
                for (int k = 0 ; k < candidate_arr.size() ; k ++)
                {
                    if (i != k)
                    {
                        Dominate_Compute dc = new Dominate_Compute();
                        int domi = dc.dominate_comp(
                                candidate_arr.get(k),candidate_arr.get(i));

                        if (domi == 1)
                            flag = true;
                    }

                    if (flag == true)
                        break;
                }
                //元组没有被支配，则应放入结果集
                if (flag == false)
                    result_arr.add(candidate_arr.get(i));
            }
            System.out.println("结果集中等待和shadow比较的元组个数为："
                    + result_arr.size());

            //与shadow中的元组进行比较
            for (int i = 0 ; i < shadow_num ; i ++)
            {
                while (bytesRead < bytesToRead)
                    bytesRead += shadow_reader.read(read_buf , bytesRead ,
                            bytesToRead - bytesRead);
                bytesRead = 0;

                in_out_count ++;

                Tuple shadow_temp = new Tuple();
                shadow_temp.parse_bucket(read_buf);

                for (int j = 0 ; j < result_arr.size() ; j ++)
                {
                    Dominate_Compute dc = new Dominate_Compute();
                    int domi = dc.dominate_comp(shadow_temp,result_arr.get(j));

                    //当前元组被支配，则移除
                    if (domi == 1)
                    {
                        result_arr.remove(j);
                        j --;
                    }
                }
            }
            System.out.println("I/O数为：" + in_out_count);

            System.out.println("最终的skyline结果元组个数为：");
            System.out.println(result_arr.size());

            for (int i = 0 ; i < result_arr.size() ; i ++)
                System.out.println(result_arr.get(i));

            shadow_reader.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        long startTime = System.currentTimeMillis();

        ISkyline is = new ISkyline();
        is.divide_block();

        is.generate_bucket_skyline();

        is.generate_skyline();

        long endTime = System.currentTimeMillis();

        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
    }
}
