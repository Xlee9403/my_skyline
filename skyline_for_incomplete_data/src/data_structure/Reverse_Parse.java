package data_structure;

import global.Info;

/**
 * Created by Xue on 2017/7/14.
 */
public class Reverse_Parse
{
    //将初始元组转化成byte[]
    public byte[] rev_parse_tuple(Tuple temp)
    {
        byte[] buf = new byte[Info.TUPLE_INITIAL_BYTES_LENGTH];

        buf[0] = (byte)(0xff & (temp.position_index >> 56));
        buf[1] = (byte)(0xff & (temp.position_index >> 48));
        buf[2] = (byte)(0xff & (temp.position_index >> 40));
        buf[3] = (byte)(0xff & (temp.position_index >> 32));
        buf[4] = (byte)(0xff & (temp.position_index >> 24));
        buf[5] = (byte)(0xff & (temp.position_index >> 16));
        buf[6] = (byte)(0xff & (temp.position_index >>  8));
        buf[7] = (byte)(0xff & temp.position_index);

        int attribute_start = Info.POSISION_INDEX_NUMBER;
        for (int i = 0; i < Info.ATTRIBUTE_NUMBER; i++)
        {
            buf[8*(attribute_start + i) + 0] = (byte)(0xff & (temp.attributes[i] >> 56));
            buf[8*(attribute_start + i) + 1] = (byte)(0xff & (temp.attributes[i] >> 48));
            buf[8*(attribute_start + i) + 2] = (byte)(0xff & (temp.attributes[i] >> 40));
            buf[8*(attribute_start + i) + 3] = (byte)(0xff & (temp.attributes[i] >> 32));
            buf[8*(attribute_start + i) + 4] = (byte)(0xff & (temp.attributes[i] >> 24));
            buf[8*(attribute_start + i) + 5] = (byte)(0xff & (temp.attributes[i] >> 16));
            buf[8*(attribute_start + i) + 6] = (byte)(0xff & (temp.attributes[i] >>  8));
            buf[8*(attribute_start + i) + 7] = (byte)(0xff & temp.attributes[i]);
        }

        return buf;
    }

    //将带有桶号的元组转化成byte[]
    public byte[] rev_parse_tuple_with_bucket_num(Tuple temp)
    {
        byte[] buf = new byte[Info.TUPLE_BUCKET_BYTES_LENGTH];

        buf[0] = (byte)(0xff & (temp.bucket_index >> 56));
        buf[1] = (byte)(0xff & (temp.bucket_index >> 48));
        buf[2] = (byte)(0xff & (temp.bucket_index >> 40));
        buf[3] = (byte)(0xff & (temp.bucket_index >> 32));
        buf[4] = (byte)(0xff & (temp.bucket_index >> 24));
        buf[5] = (byte)(0xff & (temp.bucket_index >> 16));
        buf[6] = (byte)(0xff & (temp.bucket_index >>  8));
        buf[7] = (byte)(0xff & temp.bucket_index);

        buf[8 + 0] = (byte)(0xff & (temp.position_index >> 56));
        buf[8 + 1] = (byte)(0xff & (temp.position_index >> 48));
        buf[8 + 2] = (byte)(0xff & (temp.position_index >> 40));
        buf[8 + 3] = (byte)(0xff & (temp.position_index >> 32));
        buf[8 + 4] = (byte)(0xff & (temp.position_index >> 24));
        buf[8 + 5] = (byte)(0xff & (temp.position_index >> 16));
        buf[8 + 6] = (byte)(0xff & (temp.position_index >>  8));
        buf[8 + 7] = (byte)(0xff & temp.position_index);

        int attribute_start =
                Info.POSISION_INDEX_NUMBER + Info.BUCKET_INDEX_NUMBER;

        for (int i = 0; i < Info.ATTRIBUTE_NUMBER; i++)
        {
            buf[8*(attribute_start + i) + 0] = (byte)(0xff & (temp.attributes[i] >> 56));
            buf[8*(attribute_start + i) + 1] = (byte)(0xff & (temp.attributes[i] >> 48));
            buf[8*(attribute_start + i) + 2] = (byte)(0xff & (temp.attributes[i] >> 40));
            buf[8*(attribute_start + i) + 3] = (byte)(0xff & (temp.attributes[i] >> 32));
            buf[8*(attribute_start + i) + 4] = (byte)(0xff & (temp.attributes[i] >> 24));
            buf[8*(attribute_start + i) + 5] = (byte)(0xff & (temp.attributes[i] >> 16));
            buf[8*(attribute_start + i) + 6] = (byte)(0xff & (temp.attributes[i] >>  8));
            buf[8*(attribute_start + i) + 7] = (byte)(0xff & temp.attributes[i]);
        }

        return buf;
    }

    //将带有元组完整维度的元组转化成byte[]
    public byte[] rev_parse_tuple_complete_count(Tuple temp)
    {
        byte[] buf = new byte[Info.TUPLE_COMPLETE_COUNT_BYTES_LENGTH];

        buf[0] = (byte)(0xff & (temp.comlete_count >> 56));
        buf[1] = (byte)(0xff & (temp.comlete_count >> 48));
        buf[2] = (byte)(0xff & (temp.comlete_count >> 40));
        buf[3] = (byte)(0xff & (temp.comlete_count >> 32));
        buf[4] = (byte)(0xff & (temp.comlete_count >> 24));
        buf[5] = (byte)(0xff & (temp.comlete_count >> 16));
        buf[6] = (byte)(0xff & (temp.comlete_count >>  8));
        buf[7] = (byte)(0xff &  temp.comlete_count);

        buf[8 + 0] = (byte)(0xff & (temp.position_index >> 56));
        buf[8 + 1] = (byte)(0xff & (temp.position_index >> 48));
        buf[8 + 2] = (byte)(0xff & (temp.position_index >> 40));
        buf[8 + 3] = (byte)(0xff & (temp.position_index >> 32));
        buf[8 + 4] = (byte)(0xff & (temp.position_index >> 24));
        buf[8 + 5] = (byte)(0xff & (temp.position_index >> 16));
        buf[8 + 6] = (byte)(0xff & (temp.position_index >>  8));
        buf[8 + 7] = (byte)(0xff &  temp.position_index);

        int attribute_start = Info.POSISION_INDEX_NUMBER
                + Info.BUCKET_INDEX_NUMBER;

        for (int i = 0; i < Info.ATTRIBUTE_NUMBER; i++)
        {
            buf[8*(attribute_start + i) + 0] = (byte)(0xff & (temp.attributes[i] >> 56));
            buf[8*(attribute_start + i) + 1] = (byte)(0xff & (temp.attributes[i] >> 48));
            buf[8*(attribute_start + i) + 2] = (byte)(0xff & (temp.attributes[i] >> 40));
            buf[8*(attribute_start + i) + 3] = (byte)(0xff & (temp.attributes[i] >> 32));
            buf[8*(attribute_start + i) + 4] = (byte)(0xff & (temp.attributes[i] >> 24));
            buf[8*(attribute_start + i) + 5] = (byte)(0xff & (temp.attributes[i] >> 16));
            buf[8*(attribute_start + i) + 6] = (byte)(0xff & (temp.attributes[i] >>  8));
            buf[8*(attribute_start + i) + 7] = (byte)(0xff & temp.attributes[i]);
        }

        return buf;
    }
    //将带有元组完整维度的平均值的元组转化成byte[]
    public byte[] rev_parse_tuple_average(Tuple temp)
    {
        byte[] buf = new byte[Info.TUPLE_AVERAGE_BYTES_LENGTH];

        buf[0] = (byte)(0xff & (temp.average_value >> 56));
        buf[1] = (byte)(0xff & (temp.average_value >> 48));
        buf[2] = (byte)(0xff & (temp.average_value >> 40));
        buf[3] = (byte)(0xff & (temp.average_value >> 32));
        buf[4] = (byte)(0xff & (temp.average_value >> 24));
        buf[5] = (byte)(0xff & (temp.average_value >> 16));
        buf[6] = (byte)(0xff & (temp.average_value >>  8));
        buf[7] = (byte)(0xff &  temp.average_value);

        buf[8 + 0] = (byte)(0xff & (temp.position_index >> 56));
        buf[8 + 1] = (byte)(0xff & (temp.position_index >> 48));
        buf[8 + 2] = (byte)(0xff & (temp.position_index >> 40));
        buf[8 + 3] = (byte)(0xff & (temp.position_index >> 32));
        buf[8 + 4] = (byte)(0xff & (temp.position_index >> 24));
        buf[8 + 5] = (byte)(0xff & (temp.position_index >> 16));
        buf[8 + 6] = (byte)(0xff & (temp.position_index >>  8));
        buf[8 + 7] = (byte)(0xff &  temp.position_index);

        int attribute_start = Info.POSISION_INDEX_NUMBER
                + Info.BUCKET_INDEX_NUMBER;

        for (int i = 0; i < Info.ATTRIBUTE_NUMBER; i++)
        {
            buf[8*(attribute_start + i) + 0] = (byte)(0xff & (temp.attributes[i] >> 56));
            buf[8*(attribute_start + i) + 1] = (byte)(0xff & (temp.attributes[i] >> 48));
            buf[8*(attribute_start + i) + 2] = (byte)(0xff & (temp.attributes[i] >> 40));
            buf[8*(attribute_start + i) + 3] = (byte)(0xff & (temp.attributes[i] >> 32));
            buf[8*(attribute_start + i) + 4] = (byte)(0xff & (temp.attributes[i] >> 24));
            buf[8*(attribute_start + i) + 5] = (byte)(0xff & (temp.attributes[i] >> 16));
            buf[8*(attribute_start + i) + 6] = (byte)(0xff & (temp.attributes[i] >>  8));
            buf[8*(attribute_start + i) + 7] = (byte)(0xff & temp.attributes[i]);
        }
        return buf;
    }

    //将子表中，带有元组完整维度的平均值，的元组转化成byte[]
    public byte[] rev_parse_tuple_sub_average(Tuple temp)
    {
        byte[] buf = new byte[Info.TUPLE_AVERAGE_SUB_BYTES_LENGTH];

        buf[0] = (byte)(0xff & (temp.sub_index >> 56));
        buf[1] = (byte)(0xff & (temp.sub_index >> 48));
        buf[2] = (byte)(0xff & (temp.sub_index >> 40));
        buf[3] = (byte)(0xff & (temp.sub_index >> 32));
        buf[4] = (byte)(0xff & (temp.sub_index >> 24));
        buf[5] = (byte)(0xff & (temp.sub_index >> 16));
        buf[6] = (byte)(0xff & (temp.sub_index >>  8));
        buf[7] = (byte)(0xff &  temp.sub_index);

        buf[8 + 0] = (byte)(0xff & (temp.average_value >> 56));
        buf[8 + 1] = (byte)(0xff & (temp.average_value >> 48));
        buf[8 + 2] = (byte)(0xff & (temp.average_value >> 40));
        buf[8 + 3] = (byte)(0xff & (temp.average_value >> 32));
        buf[8 + 4] = (byte)(0xff & (temp.average_value >> 24));
        buf[8 + 5] = (byte)(0xff & (temp.average_value >> 16));
        buf[8 + 6] = (byte)(0xff & (temp.average_value >>  8));
        buf[8 + 7] = (byte)(0xff &  temp.average_value);

        buf[8*2 + 0] = (byte)(0xff & (temp.position_index >> 56));
        buf[8*2 + 1] = (byte)(0xff & (temp.position_index >> 48));
        buf[8*2 + 2] = (byte)(0xff & (temp.position_index >> 40));
        buf[8*2 + 3] = (byte)(0xff & (temp.position_index >> 32));
        buf[8*2 + 4] = (byte)(0xff & (temp.position_index >> 24));
        buf[8*2 + 5] = (byte)(0xff & (temp.position_index >> 16));
        buf[8*2 + 6] = (byte)(0xff & (temp.position_index >>  8));
        buf[8*2 + 7] = (byte)(0xff &  temp.position_index);

        int attribute_start = Info.POSISION_INDEX_NUMBER
                + Info.BUCKET_INDEX_NUMBER + Info.SUB_NUM_NUMBER;

        for (int i = 0; i < Info.ATTRIBUTE_NUMBER; i++)
        {
            buf[8*(attribute_start + i) + 0] = (byte)(0xff & (temp.attributes[i] >> 56));
            buf[8*(attribute_start + i) + 1] = (byte)(0xff & (temp.attributes[i] >> 48));
            buf[8*(attribute_start + i) + 2] = (byte)(0xff & (temp.attributes[i] >> 40));
            buf[8*(attribute_start + i) + 3] = (byte)(0xff & (temp.attributes[i] >> 32));
            buf[8*(attribute_start + i) + 4] = (byte)(0xff & (temp.attributes[i] >> 24));
            buf[8*(attribute_start + i) + 5] = (byte)(0xff & (temp.attributes[i] >> 16));
            buf[8*(attribute_start + i) + 6] = (byte)(0xff & (temp.attributes[i] >>  8));
            buf[8*(attribute_start + i) + 7] = (byte)(0xff & temp.attributes[i]);
        }
        return buf;
    }

    //将初始列属性转化成byte[]
    public byte[] rev_parse_column(Column temp)
    {
        byte[] buf = new byte[Info.COLUMN_BYTES_LENGTH];

        buf[0] = (byte)(0xff & (temp.pi >> 56));
        buf[1] = (byte)(0xff & (temp.pi >> 48));
        buf[2] = (byte)(0xff & (temp.pi >> 40));
        buf[3] = (byte)(0xff & (temp.pi >> 32));
        buf[4] = (byte)(0xff & (temp.pi >> 24));
        buf[5] = (byte)(0xff & (temp.pi >> 16));
        buf[6] = (byte)(0xff & (temp.pi >>  8));
        buf[7] = (byte)(0xff & temp.pi);

        buf[8 + 0] = (byte)(0xff & (temp.value >> 56));
        buf[8 + 1] = (byte)(0xff & (temp.value >> 48));
        buf[8 + 2] = (byte)(0xff & (temp.value >> 40));
        buf[8 + 3] = (byte)(0xff & (temp.value >> 32));
        buf[8 + 4] = (byte)(0xff & (temp.value >> 24));
        buf[8 + 5] = (byte)(0xff & (temp.value >> 16));
        buf[8 + 6] = (byte)(0xff & (temp.value >>  8));
        buf[8 + 7] = (byte)(0xff & temp.value);

        return buf;
    }
    //将子表中的列属性转化成byte[]
    public byte[] rev_parse_sub_column(Column temp)
    {
        byte[] buf = new byte[Info.SUB_COLUMN_BYTES_LENGTH];

        buf[0] = (byte)(0xff & (temp.sub_num >> 56));
        buf[1] = (byte)(0xff & (temp.sub_num >> 48));
        buf[2] = (byte)(0xff & (temp.sub_num >> 40));
        buf[3] = (byte)(0xff & (temp.sub_num >> 32));
        buf[4] = (byte)(0xff & (temp.sub_num >> 24));
        buf[5] = (byte)(0xff & (temp.sub_num >> 16));
        buf[6] = (byte)(0xff & (temp.sub_num >>  8));
        buf[7] = (byte)(0xff & temp.sub_num);

        buf[8 + 0] = (byte)(0xff & (temp.pi >> 56));
        buf[8 + 1] = (byte)(0xff & (temp.pi >> 48));
        buf[8 + 2] = (byte)(0xff & (temp.pi >> 40));
        buf[8 + 3] = (byte)(0xff & (temp.pi >> 32));
        buf[8 + 4] = (byte)(0xff & (temp.pi >> 24));
        buf[8 + 5] = (byte)(0xff & (temp.pi >> 16));
        buf[8 + 6] = (byte)(0xff & (temp.pi >>  8));
        buf[8 + 7] = (byte)(0xff & temp.pi);

        buf[8*2 + 0] = (byte)(0xff & (temp.value >> 56));
        buf[8*2 + 1] = (byte)(0xff & (temp.value >> 48));
        buf[8*2 + 2] = (byte)(0xff & (temp.value >> 40));
        buf[8*2 + 3] = (byte)(0xff & (temp.value >> 32));
        buf[8*2 + 4] = (byte)(0xff & (temp.value >> 24));
        buf[8*2 + 5] = (byte)(0xff & (temp.value >> 16));
        buf[8*2 + 6] = (byte)(0xff & (temp.value >>  8));
        buf[8*2 + 7] = (byte)(0xff & temp.value);

        return buf;
    }
}
