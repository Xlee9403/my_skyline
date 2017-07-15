package  src.algrithm;

import  src.data_structure.Column;
import  src.data_structure.Processed_count;
import  src.data_structure.Reverse_Parse;
import  src.data_structure.Tuple;
import  src.global.Info;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Xue on 2017/7/14.
 */
public class SIDS
{
    public void genetate_skyline()
    {
        //记录I/O数
        int in_out_count = 0;

        int skyline_result = 0;

        //获取每个列文件长度
        long[] col_count_arr = new long[Info.ATTRIBUTE_NUMBER];
        col_count_arr = read_column_count();

        //记录candidate中剩余的元组数
        int cadidate_count = Info.TUPLE_NUMBER;

        //记录比较过的元组PI值,及比较过的次数
        ArrayList<Processed_count> processed_pi_arr =
                new ArrayList<Processed_count>();

        try
        {
            //-------------------------初始化用到的变量--------------------------//
            //读取列文件
            BufferedInputStream[] column_reader =
                    new BufferedInputStream[Info.ATTRIBUTE_NUMBER];
            for (int i = 0; i < column_reader.length; i++)
                column_reader[i] = new BufferedInputStream(new FileInputStream(
                        Info.ROOT_PATH + Info.COLUMN_ROOT + Info.COLUMN_SORT_PATH
                                + i + Info.extension));

            byte[] col_buf = new byte[Info.COLUMN_BYTES_LENGTH];
            int colToRead = col_buf.length;
            int bytesRead = 0;

            //读比较集用的buf
            byte[] comp_buf = new byte[Info.TUPLE_COMPLETE_COUNT_BYTES_LENGTH];
            int compToRead = comp_buf.length;

            //读候选集用的buf
            byte[] cand_buf = new byte[Info.TUPLE_INITIAL_BYTES_LENGTH];
            int candToRead = cand_buf.length;

            int next_column = 0;

            System.out.println("===========开始进行比较==========");

            //本次要读取的候选集
            int candidate_read = 0;

            //用来和cadidate_count共同控制候选集中剩余的元组数
            int cand_count = cadidate_count;
            /////////////////--------------------------------------////////////////

            //早结束条件，每个点都被执行一次，或者候选集为空
            while((processed_pi_arr.size() != Info.TUPLE_NUMBER)
                    && (cadidate_count != 0))
            {
                //更新cadidate_count的值
                cadidate_count = cand_count;

                //读取比较集,比较集需要随机存取,每次都从头读取
                RandomAccessFile comp =
                        new RandomAccessFile(Info.ROOT_PATH +
                                Info.TABLE_COMPLETE_ATTRIBUTE_COUNT_PATH , "r");

                //要比较的元组的PI值
                long tuple_position = 0;

                //-----------------------轮询读列文件---------------------------//
                //要读取的列文件中的元组数不为空，则可读取
                if (col_count_arr[next_column] != 0)
                {
                    while (bytesRead < colToRead)
                        bytesRead += column_reader[next_column].read(
                                col_buf, bytesRead, colToRead - bytesRead);
                    bytesRead = 0;

                    in_out_count ++;

                    Column col_temp = new Column();
                    col_temp.parse_column(col_buf);

                    tuple_position = col_temp.pi;

                    col_count_arr[next_column] -= 1;
                }

                next_column = (next_column + 1) % Info.ATTRIBUTE_NUMBER;
                //////////////////------------------------------/////////////////

                //---------------随机读取比较集，找到对应PI的元组--------------------//
                comp.seek(tuple_position * Info.TUPLE_COMPLETE_COUNT_BYTES_LENGTH);
                while (bytesRead < compToRead)
                    bytesRead += comp.read(comp_buf, bytesRead,
                            compToRead - bytesRead);
                bytesRead = 0;

                in_out_count ++;

                Tuple tup_comp = new Tuple();
                tup_comp.parse_complete_count(comp_buf);
                ///////////////////----------------------------///////////////////

                //----------------判断当前元组是否存在processed_pi_arr--------------//
                boolean exist_pro = false;
                int pro_position = 0;
                if (processed_pi_arr.size() != 0)
                {
                    for (int i = 0; i < processed_pi_arr.size(); i++)
                    {
                        if (processed_pi_arr.get(i).pi == tuple_position)
                        {
                            exist_pro = true;

                            pro_position = i;
                            break;
                        }
                    }
                }
                //////////////////----------------------------///////////////////

                //如果不在processed_pi_arr,或者processed_pi_arr为空，直接比较
                if ((exist_pro == false) || (processed_pi_arr.size() == 0))
                {
                    //----------将比较元组加入processed_pi_arr--------//
                    Processed_count pc = new Processed_count();
                    pc.pi = tuple_position;
                    pc.pro_count = 1;

                    processed_pi_arr.add(pc);
                    ////////////////-------------------//////////////

                    //读取候选集
                    BufferedInputStream reader = new BufferedInputStream(new
                            FileInputStream(Info.ROOT_PATH + Info.CANDIDATE_TABLE_PATH
                            + (candidate_read % 2) + Info.extension));

                    BufferedOutputStream writer = new BufferedOutputStream(new
                            FileOutputStream(Info.ROOT_PATH + Info.CANDIDATE_TABLE_PATH
                            + ((candidate_read + 1) % 2)+ Info.extension));

                    //标记比较元组是否被支配
                    boolean current_dominated = false;
                    boolean isexist = false;

                    for (int i = 0; i < cadidate_count; i++)
                    {
//						System.out.println( i + ":" + cadidate_count);

                        while (bytesRead < candToRead)
                            bytesRead += reader.read(cand_buf, bytesRead,
                                    candToRead - bytesRead);
                        bytesRead = 0;

                        in_out_count ++;

                        Tuple cand_temp = new Tuple();
                        cand_temp.parse_initial(cand_buf);

                        //记录两个元组均不为空的属性数
                        int con_count = 0;

                        int dominate = 0;

                        int equal_count = 0;

                        //比较集中的元组与候选集中要比较的元组，不相同
                        if (tup_comp.position_index != cand_temp.position_index)
                        {
                            //判断当前元组是否被支配
                            for (int j = 0; j < Info.RELATED_ATTRIBUTES_NUMBER; j++)
                            {
                                if ((tup_comp.attributes[j] != Long.MIN_VALUE) &&
                                        cand_temp.attributes[j] != Long.MIN_VALUE)
                                {
                                    con_count ++;

                                    if (tup_comp.attributes[j]
                                            <= cand_temp.attributes[j])
                                    {
                                        dominate ++;

                                        if (tup_comp.attributes[j]
                                                == cand_temp.attributes[j])
                                        {
                                            equal_count ++;
                                        }
                                    }
                                }

                            }
                            //候选集里的该元组被支配，不再写回,候选集中的元组数减一
                            if((con_count == dominate) && (equal_count < dominate))
                            {
                                cand_count --;
                            }
                            //将该元组写回
                            else
                            {
                                //比较元组被支配,将标记置为true
                                if (dominate == 0)
                                    current_dominated = true;

//								Reverse_Parse rp = new Reverse_Parse();
//								cand_buf = rp.rev_parse_tuple(cand_temp);

                                writer.write(cand_buf);
                                in_out_count ++;
                            }
                        }
                        //候选集中的该元组，即为比较集中的当前元组
                        //暂不写回该元组，则候选集中的元组数减一
                        else
                        {
                            isexist = true;
                            cand_count --;
                        }

                    }
                    //比较元组不被支配，则将其写入候选集，并将候选集中的元组数增一
                    if ((current_dominated == false) && (isexist == true))
                    {
                        Reverse_Parse rp = new Reverse_Parse();
                        cand_buf = rp.rev_parse_tuple(tup_comp);

                        writer.write(cand_buf);

                        in_out_count ++;

                        cand_count ++;
                    }

                    writer.flush();
                    writer.close();

                    reader.close();

                    candidate_read ++;
                }
                //每个完整维度都出现了一次，若候选集中仍存在该元组
                //读取候选元组中的当前元组，并将其输出，删除候选集
                else if (exist_pro && (processed_pi_arr.get(pro_position).pro_count
                        == (tup_comp.comlete_count - 1)))
                {
                    //读取候选集
                    BufferedInputStream reader = new BufferedInputStream(new
                            FileInputStream(Info.ROOT_PATH + Info.CANDIDATE_TABLE_PATH
                            + (candidate_read % 2) + Info.extension));

                    BufferedOutputStream writer = new BufferedOutputStream(new
                            FileOutputStream(Info.ROOT_PATH + Info.CANDIDATE_TABLE_PATH
                            + ((candidate_read + 1) % 2)+ Info.extension));

                    //扫描候选集，判断是否存在该元组
                    //若存在，则输出该元组，并删除候选集中的该元组
                    for (int i = 0; i < cadidate_count; i++)
                    {
                        while (bytesRead < candToRead)
                            bytesRead += reader.read(cand_buf, bytesRead,
                                    candToRead - bytesRead);
                        bytesRead = 0;
                        in_out_count ++;

                        Tuple cand_temp = new Tuple();
                        cand_temp.parse_initial(cand_buf);

                        //若不是该元组，则写回候选集
                        if (tup_comp.position_index != cand_temp.position_index)
                            writer.write(cand_buf);

                            //找到该元组，则直接输出，并将候选集中的元组数减一
                        else
                        {
                            System.out.println("==========结果集=========");
                            System.out.println(cand_temp);

                            skyline_result ++;

                            cand_count --;
                        }
                    }

                    writer.flush();
                    writer.close();

                    reader.close();

                    candidate_read ++;
                }
                else
                {
                    processed_pi_arr.get(pro_position).pro_count ++;
                }

                comp.close();
            }

            for (int i = 0; i < column_reader.length; i++)
                column_reader[i].close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        //若不能以候选集为空早结束，则候选集中剩余的元组即为skyline结果
        //将结果输出
        if (cadidate_count != 0)
        {
            read_candidate(cadidate_count);

            in_out_count += cadidate_count;

            skyline_result += cadidate_count;
        }

        if (processed_pi_arr.size() < (Info.TUPLE_NUMBER - 1))
            System.out.println("=======本次早结束条件为候选集为空========");
        else
            System.out.println("=======本次没有早结束========");

        System.out.println("=====I/O数为：=====");
        System.out.println(in_out_count);

        System.out.println("=====skyline结果数为：=====");
        System.out.println(skyline_result);

    }
    //输出候选集中剩余的元组
    public void read_candidate(int can_count)
    {
        try
        {
            BufferedInputStream reader = new BufferedInputStream(new
                    FileInputStream(Info.ROOT_PATH + Info.CANDIDATE_TABLE_PATH
                    + 0 + Info.extension));

            byte[] buf = new byte[Info.TUPLE_INITIAL_BYTES_LENGTH];
            int bytesToRead = buf.length;
            int bytesRead = 0;

            for (int i = 0; i < can_count; i++)
            {
                while (bytesRead < bytesToRead)
                    bytesRead += reader.read(buf, bytesRead,
                            bytesToRead - bytesRead);
                bytesRead = 0;

                Tuple temp = new Tuple();
                temp.parse_initial(buf);

                System.out.println(temp);
            }
            reader.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    //获取每个列文件的长度
    public long[] read_column_count()
    {
        long[] column_count = new long[Info.ATTRIBUTE_NUMBER];

        try
        {
            //-----------------------读取每个列文件存储的元组数---------------------//
            BufferedInputStream col_count_reader = new BufferedInputStream(
                    new FileInputStream(Info.ROOT_PATH + Info.COLUMN_ROOT +
                            Info.COLUMN_COUNT_PATH));

            byte[] count_buf = new byte[Info.ATTRIBUTE_BYTES_LENGTH];
            int countToRead = count_buf.length;
            int bytesRead = 0;

            for (int i = 0; i < Info.ATTRIBUTE_NUMBER; i++)
            {
                while (bytesRead < countToRead)
                    bytesRead += col_count_reader.read(count_buf,
                            bytesRead, countToRead - bytesRead);
                bytesRead = 0;

                column_count[i]=
                        (((long)(count_buf[0] & 0xff) << 56) |
                                ((long)(count_buf[1] & 0xff) << 48) |
                                ((long)(count_buf[2] & 0xff) << 40) |
                                ((long)(count_buf[3] & 0xff) << 32) |
                                ((long)(count_buf[4] & 0xff) << 24) |
                                ((long)(count_buf[5] & 0xff) << 16) |
                                ((long)(count_buf[6] & 0xff) <<  8) |
                                ((long)(count_buf[7] & 0xff)));
            }
            col_count_reader.close();
            System.out.println("========成功读取每个列文件的长度==========");
            ///////////////////-------------------------------//////////////////
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return column_count;
    }

    public static void main(String[] args)
    {
        long startTime = System.currentTimeMillis();

        SIDS ss = new SIDS();
        ss.genetate_skyline();

        long endTime = System.currentTimeMillis();

        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");

    }
}
