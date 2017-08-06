package src.fileop;

import src.data_structure.*;
import src.global.Info;

import java.io.*;
import java.util.Arrays;

/**
 * Created by Xue on 2017/7/25.
 */
public class Generate_Scan_Table
{
    //存放每个列中元组数的数组
    long[] column_count = new long[Info.ATTRIBUTE_NUMBER];

    //将列文件按照PI值排序，并将value值用cpi代替
    public void sort_column_pi()
    {
        try
        {
            //-----------------读出每个属性列的属性数---------------------//
            BufferedInputStream count_reader = new BufferedInputStream(
                    new FileInputStream(Info.ROOT_PATH + Info.COLUMN_ROOT
                            + Info.COLUMN_COUNT_PATH));

            byte[] count_buf = new byte[Info.ATTRIBUTE_NUMBER
                    * Info.ATTRIBUTE_BYTES_LENGTH];
            int countToRead = count_buf.length;
            int countRead = 0;

            while (countRead < countToRead)
                countRead += count_reader.read(count_buf,
                        countRead, countToRead - countRead);

            for (int i = 0; i < Info.ATTRIBUTE_NUMBER; i++)
            {
                column_count[i] =
                        (((long)(count_buf[8*i + 0] & 0xff) << 56) |
                                ((long)(count_buf[8*i + 1] & 0xff) << 48) |
                                ((long)(count_buf[8*i + 2] & 0xff) << 40) |
                                ((long)(count_buf[8*i + 3] & 0xff) << 32) |
                                ((long)(count_buf[8*i + 4] & 0xff) << 24) |
                                ((long)(count_buf[8*i + 5] & 0xff) << 16) |
                                ((long)(count_buf[8*i + 6] & 0xff) <<  8) |
                                ((long)(count_buf[8*i + 7] & 0xff)));

                System.out.println(column_count[i]);
            }
            count_reader.close();
            ////////////////------------------------------//////////////

            //-----------------读出每个列文件中，并按照属性值排序---------------------//
            BufferedInputStream[] column_reader =
                    new BufferedInputStream[Info.ATTRIBUTE_NUMBER];

            byte[] column_buf = new byte[Info.COLUMN_BYTES_LENGTH];
            int ColumnToRead = column_buf.length;
            int ColumnRead = 0;

            BufferedOutputStream[] column_writer =
                    new BufferedOutputStream[Info.ATTRIBUTE_NUMBER];

            for (int i = 0; i < column_count.length; i++)
            {
                column_reader[i] = new BufferedInputStream(new FileInputStream(
                        Info.ROOT_PATH + Info.COLUMN_ROOT +
                                Info.COLUMN_SORT_PATH + i + Info.extension));

                //----------------若内存可以放下，则直接读入内存排序后写回-------------------//
                if (Info.COLUMN_BYTES_LENGTH * column_count[i]
                        <= Info.ALLOCATED_MEMEORY_SIZE)
                {
                    System.out.println("========可直接放入内存排序=======");

                    column_writer[i] = new BufferedOutputStream(new FileOutputStream(
                            Info.ROOT_PATH + Info.COLUMN_ROOT +
                             Info.COLUMN_SORT_PI_PATH + i + Info.extension));

                    //存放列的数组
                    Column[] sort_arr = new Column[(int)column_count[i]];
                    for (int j = 0; j < sort_arr.length; j++)
                        sort_arr[j] = new Column();

                    System.out.println((int)column_count[i]);

                    //将整个列文件读入内存
                    for (int j = 0; j < sort_arr.length; j++)
                    {
                        while (ColumnRead < ColumnToRead)
                            ColumnRead += column_reader[i].read(
                                    column_buf, ColumnRead, ColumnToRead - ColumnRead);
                        ColumnRead = 0;

                        Column temp = new Column();
                        temp.parse_column(column_buf);

                        //用cpi代替value
                        temp.value = j;

                        sort_arr[j].copyfrom(temp);
                    }

                    //对列文件，按属性值排序
                    Compare_Column_PI ccp = new Compare_Column_PI();
                    Arrays.sort(sort_arr, ccp);

                    //将排好序的属性列写回
                    for (int j = 0; j < sort_arr.length; j++)
                    {
                        Reverse_Parse rp = new Reverse_Parse();
                        column_buf = rp.rev_parse_column(sort_arr[j]);

                        column_writer[i].write(column_buf);

                    }

                    column_writer[i].flush();
                    column_writer[i].close();
                }
                //////////////////------------------------------//////////////////

                //---------------若内存，放不下，考虑要划分成几块，然后归并--------------//
                else
                {
                    System.out.println("========不可直接放入内存排序=======");

                    byte[] sub_column_buf = new byte[Info.SUB_COLUMN_BYTES_LENGTH];
                    int sub_columnToRead = sub_column_buf.length;
                    int sub_columnRead = 0;

                    //-----------------计算应划分的块数--------------------//
                    int partition_num;
                    int column_size = (int) ((int)Info.COLUMN_BYTES_LENGTH * column_count[i]);
                    if (column_size % Info.ALLOCATED_MEMEORY_SIZE == 0)
                        partition_num =
                                column_size / Info.ALLOCATED_MEMEORY_SIZE;
                    else
                        partition_num = 1 +
                                column_size / Info.ALLOCATED_MEMEORY_SIZE;
                    //////////////////-------------------/////////////////

                    //-------------------计算每块的元组数--------------------//
                    int[] par_num_per_block = new int[partition_num];
                    for (int j = 0; j < partition_num; j++)
                    {
                        if (j == (partition_num - 1))
                            par_num_per_block[j] = (int)column_count[i] -
                                    ((int)(column_count[i])/partition_num) * j;

                        else
                            par_num_per_block[j] =
                                    (int)(column_count[i])/partition_num;
                    }
                    //////////////////------------------------///////////////

                    BufferedOutputStream[] sub_writer =
                            new BufferedOutputStream[partition_num];

                    //------------------按块读出，排序后写入子表----------------//
                    for (int j = 0; j < partition_num; j++)
                    {
                        sub_writer[j] = new BufferedOutputStream(new
                                FileOutputStream(Info.ROOT_PATH + Info.COLUMN_ROOT
                                + Info.SUB_PATH + Info.COLUMN_SORT_PI_PATH + i +
                                Info.INTERVAL + j + Info.extension));

                        Column[] sub_arr = new Column[par_num_per_block[j]];
                        for (int k = 0; k < sub_arr.length; k++)
                            sub_arr[k] = new Column();

                        for (int k = 0; k < par_num_per_block[j]; k++)
                        {
                            while (ColumnRead < ColumnToRead)
                                ColumnRead += column_reader[i].read(column_buf,
                                        ColumnRead, ColumnToRead - ColumnRead);
                            ColumnRead = 0;

                            Column temp = new Column();
                            temp.parse_column(column_buf);

                            //用cpi代替value
                            temp.value = 0;
                            for (int r = 0 ; r < j ; r ++)
                                temp.value += par_num_per_block[j];
                            temp.value += k;

                            sub_arr[k].copyfrom(temp);
                        }

                       Compare_Column_PI ccp = new Compare_Column_PI();
                        Arrays.sort(sub_arr, ccp);

                        for (int k = 0; k < sub_arr.length; k++)
                        {
                            sub_arr[k].sub_num = j;

                            Reverse_Parse rp = new Reverse_Parse();
                            sub_column_buf = rp.rev_parse_sub_column(sub_arr[k]);

                            sub_writer[j].write(sub_column_buf);
                        }

                        sub_writer[j].flush();
                        sub_writer[j].close();

                    }
                    ////////////////------------------------////////////////

                    //--------------------归并子表，得到有序列------------------//
                    BufferedInputStream[] sub_reader =
                            new BufferedInputStream[partition_num];
                    for (int j = 0; j < sub_reader.length; j++)
                        sub_reader[j] = new BufferedInputStream(new
                                FileInputStream(Info.ROOT_PATH + Info.COLUMN_ROOT
                                + Info.SUB_PATH + Info.COLUMN_SORT_PI_PATH + i +
                                Info.INTERVAL + j + Info.extension));

                    Column[] min_subtable = new Column[partition_num];
                    for (int j = 0; j < min_subtable.length; j++)
                        min_subtable[j] = new Column();

                    //从每个子表中读出一个
                    for (int j = 0; j < min_subtable.length; j++)
                    {
                        while (sub_columnRead < sub_columnToRead)
                            sub_columnRead += sub_reader[j].read(sub_column_buf,
                                    sub_columnRead, sub_columnToRead - sub_columnRead);
                        sub_columnRead = 0;

                        par_num_per_block[j] --;

                        Column temp = new Column();
                        temp.parse_sub_column(sub_column_buf);

                        min_subtable[j].copyfrom(temp);
                    }

                    column_writer[i] = new BufferedOutputStream(new FileOutputStream(
                            Info.ROOT_PATH + Info.COLUMN_ROOT +
                                    Info.COLUMN_SORT_PI_PATH + i + Info.extension));

                    for (int j = 0; j < column_count[i]; j++)
                    {
                        Compare_Column_PI ccp = new Compare_Column_PI();
                        Arrays.sort(min_subtable,ccp);

                        Reverse_Parse rp = new Reverse_Parse();
                        column_buf = rp.rev_parse_sub_column(min_subtable[0]);

                        column_writer[i].write(column_buf);

                        int read_next = (int) min_subtable[0].sub_num;

                        //找当前最小值所在的子表中读取一个，若该子表为空，则置为最大值
                        if (par_num_per_block[read_next] != 0)
                        {
                            while (sub_columnRead < sub_columnToRead)
                                sub_columnRead += sub_reader[read_next].read(sub_column_buf,
                                        sub_columnRead, sub_columnToRead - sub_columnRead);
                            sub_columnRead = 0;

                            par_num_per_block[j] --;

                            Column temp = new Column();
                            temp.parse_sub_column(sub_column_buf);

                            min_subtable[0].copyfrom(temp);
                        }
                        else
                            min_subtable[0].value = Long.MAX_VALUE;
                    }
                    column_writer[i].flush();
                    column_writer[i].close();

                    for (int k = 0; k < partition_num; k++)
                        sub_reader[k].close();

                    ////////////////------------------------/////////////////

                }
                //////////////////-----------------------------//////////////////

            }

            for (int i = 0; i < column_reader.length; i++)
                column_reader[i].close();
            ////////////////////------------------------------//////////////////

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    //根据排序结果生成扫描表
    public void generate()
    {
        try
        {
            BufferedInputStream[] column_reader =
                    new BufferedInputStream[Info.ATTRIBUTE_NUMBER];
            for (int i = 0 ; i < column_reader.length ; i ++)
                column_reader[i] = new BufferedInputStream(new FileInputStream(
                        Info.ROOT_PATH + Info.COLUMN_ROOT +
                                Info.COLUMN_SORT_PI_PATH + i + Info.extension));

            byte[] column_buf = new byte[Info.COLUMN_BYTES_LENGTH];
            int colToRead = column_buf.length;
            int colRead = 0;

            BufferedOutputStream pt_writer = new BufferedOutputStream(
                    new FileOutputStream(Info.ROOT_PATH
                            + Info.SCAN_TABLE_PATH));

            byte[] write_buf;

            //存放每个属性列读出的列值
            Column[] arr = new Column[Info.ATTRIBUTE_NUMBER];

            //先从每个列文件中读出一个放入数组
            for (int i = 0 ; i < Info.ATTRIBUTE_NUMBER ; i ++)
            {
                while (colRead < colToRead)
                    colRead += column_reader[i].read(column_buf,
                            colRead , colToRead - colRead);
                colRead = 0;

                column_count[i] --;

                arr[i] = new Column();
                arr[i].parse_column(column_buf);
            }

            for (int i = 0 ; i < Info.TUPLE_NUMBER ; i ++)
            {
                Tuple temp = new Tuple();
                temp.position_index = (long) i;

                for (int j = 0 ; j < Info.ATTRIBUTE_NUMBER ; j ++)
                {
                    //若该属性为非缺失值
                    if (arr[j].pi == temp.position_index)
                    {
                        temp.attributes[j] = arr[j].value;

                        //读取下一个位置
                        if (column_count[j] != 0)
                        {
                            while (colRead < colToRead)
                                colRead += column_reader[j].read(column_buf,
                                        colRead , colToRead - colRead);
                            colRead = 0;

                            column_count[j] --;

                            Column col_temp = new Column();
                            col_temp.parse_column(column_buf);

                            arr[j].copyfrom(col_temp);
                        }
                        else
                            arr[j].pi = Long.MAX_VALUE;
                    }
                    else
                        temp.attributes[j] = Long.MIN_VALUE;
                }

                Reverse_Parse rp = new Reverse_Parse();
                write_buf = rp.rev_parse_tuple(temp);

                pt_writer.write(write_buf);

            }

            pt_writer.flush();
            pt_writer.close();

            for (int i = 0 ; i < column_reader.length ; i ++)
            {
                column_reader[i].close();
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

        Generate_Scan_Table gst = new Generate_Scan_Table();
        gst.sort_column_pi();

        gst.generate();


        long endTime = System.currentTimeMillis();

        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
    }
}
