package src.data_structure;

import src.global.Info;

/**
 * Created by Xue on 2017/7/14.
 */
public class Tuple
{
    public boolean dominated = false;

    public int temp_position;
    //桶号
    public long bucket_index;

    public long sub_index;

    public long average_value;
    public long comlete_count;

    public long position_index;
    public long[] attributes = new long[Info.ATTRIBUTE_NUMBER];

    //解析初始元组
    public void parse_initial(byte[] buf)
    {
        if (buf.length != Info.TUPLE_INITIAL_BYTES_LENGTH)
        {
            System.out.println("error");
            return;
        }
        else
        {
            position_index = (((long)(buf[0] & 0xff) << 56) |
                              ((long)(buf[1] & 0xff) << 48) |
                              ((long)(buf[2] & 0xff) << 40) |
                              ((long)(buf[3] & 0xff) << 32) |
                              ((long)(buf[4] & 0xff) << 24) |
                              ((long)(buf[5] & 0xff) << 16) |
                              ((long)(buf[6] & 0xff) <<  8) |
                              ((long)(buf[7] & 0xff)));

            for (int i = 0; i < Info.ATTRIBUTE_NUMBER; i++)
            {
                int j = i + Info.POSISION_INDEX_NUMBER;

                attributes[i] = (((long)(buf[8*j + 0] & 0xff) << 56) |
                                 ((long)(buf[8*j + 1] & 0xff) << 48) |
                                 ((long)(buf[8*j + 2] & 0xff) << 40) |
                                 ((long)(buf[8*j + 3] & 0xff) << 32) |
                                 ((long)(buf[8*j + 4] & 0xff) << 24) |
                                 ((long)(buf[8*j + 5] & 0xff) << 16) |
                                 ((long)(buf[8*j + 6] & 0xff) <<  8) |
                                 ((long)(buf[8*j + 7] & 0xff)));
            }
        }
    }

    //解析桶内元组
    public void parse_bucket(byte[] buf)
    {
        if (buf.length != Info.TUPLE_BUCKET_BYTES_LENGTH)
        {
            System.out.println("error");
            return;
        }
        else
        {
            bucket_index = (((long)(buf[0] & 0xff) << 56) |
                            ((long)(buf[1] & 0xff) << 48) |
                            ((long)(buf[2] & 0xff) << 40) |
                            ((long)(buf[3] & 0xff) << 32) |
                            ((long)(buf[4] & 0xff) << 24) |
                            ((long)(buf[5] & 0xff) << 16) |
                            ((long)(buf[6] & 0xff) <<  8) |
                            ((long)(buf[7] & 0xff)));

            position_index = (((long)(buf[8 + 0] & 0xff) << 56) |
                              ((long)(buf[8 + 1] & 0xff) << 48) |
                              ((long)(buf[8 + 2] & 0xff) << 40) |
                              ((long)(buf[8 + 3] & 0xff) << 32) |
                              ((long)(buf[8 + 4] & 0xff) << 24) |
                              ((long)(buf[8 + 5] & 0xff) << 16) |
                              ((long)(buf[8 + 6] & 0xff) <<  8) |
                              ((long)(buf[8 + 7] & 0xff)));

            for (int i = 0; i < Info.ATTRIBUTE_NUMBER; i++)
            {
                int j = i + Info.POSISION_INDEX_NUMBER + Info.BUCKET_INDEX_NUMBER;

                attributes[i] = (((long)(buf[8*j + 0] & 0xff) << 56) |
                                 ((long)(buf[8*j + 1] & 0xff) << 48) |
                                 ((long)(buf[8*j + 2] & 0xff) << 40) |
                                 ((long)(buf[8*j + 3] & 0xff) << 32) |
                                 ((long)(buf[8*j + 4] & 0xff) << 24) |
                                 ((long)(buf[8*j + 5] & 0xff) << 16) |
                                 ((long)(buf[8*j + 6] & 0xff) <<  8) |
                                 ((long)(buf[8*j + 7] & 0xff)));
            }
        }
    }

    //解析带有元组完整计数的元组
    public void parse_complete_count(byte[] buf)
    {
        if (buf.length != Info.TUPLE_COMPLETE_COUNT_BYTES_LENGTH)
        {
            System.out.println("error");
            return;
        }
        else
        {
            comlete_count = (((long)(buf[0] & 0xff) << 56) |
                             ((long)(buf[1] & 0xff) << 48) |
                             ((long)(buf[2] & 0xff) << 40) |
                             ((long)(buf[3] & 0xff) << 32) |
                             ((long)(buf[4] & 0xff) << 24) |
                             ((long)(buf[5] & 0xff) << 16) |
                             ((long)(buf[6] & 0xff) <<  8) |
                             ((long)(buf[7] & 0xff)));

            position_index = (((long)(buf[8 + 0] & 0xff) << 56) |
                              ((long)(buf[8 + 1] & 0xff) << 48) |
                              ((long)(buf[8 + 2] & 0xff) << 40) |
                              ((long)(buf[8 + 3] & 0xff) << 32) |
                              ((long)(buf[8 + 4] & 0xff) << 24) |
                              ((long)(buf[8 + 5] & 0xff) << 16) |
                              ((long)(buf[8 + 6] & 0xff) <<  8) |
                              ((long)(buf[8 + 7] & 0xff)));

            for (int i = 0; i < Info.ATTRIBUTE_NUMBER; i++)
            {
                int j = i + Info.POSISION_INDEX_NUMBER
                        + Info.COMPLETE_COUNT_NUMBER ;

                attributes[i] = (((long)(buf[8*j + 0] & 0xff) << 56) |
                                 ((long)(buf[8*j + 1] & 0xff) << 48) |
                                 ((long)(buf[8*j + 2] & 0xff) << 40) |
                                 ((long)(buf[8*j + 3] & 0xff) << 32) |
                                 ((long)(buf[8*j + 4] & 0xff) << 24) |
                                 ((long)(buf[8*j + 5] & 0xff) << 16) |
                                 ((long)(buf[8*j + 6] & 0xff) <<  8) |
                                 ((long)(buf[8*j + 7] & 0xff)));
            }
        }
    }

    //解析带有元组完整维度平均值的元组
    public void parse_average(byte[] buf)
    {
        if (buf.length != Info.TUPLE_AVERAGE_BYTES_LENGTH)
        {
            System.out.println("error");
            return;
        }
        else
        {
            average_value = (((long)(buf[0] & 0xff) << 56) |
                             ((long)(buf[1] & 0xff) << 48) |
                             ((long)(buf[2] & 0xff) << 40) |
                             ((long)(buf[3] & 0xff) << 32) |
                             ((long)(buf[4] & 0xff) << 24) |
                             ((long)(buf[5] & 0xff) << 16) |
                             ((long)(buf[6] & 0xff) <<  8) |
                             ((long)(buf[7] & 0xff)));

            position_index = (((long)(buf[8 + 0] & 0xff) << 56) |
                              ((long)(buf[8 + 1] & 0xff) << 48) |
                              ((long)(buf[8 + 2] & 0xff) << 40) |
                              ((long)(buf[8 + 3] & 0xff) << 32) |
                              ((long)(buf[8 + 4] & 0xff) << 24) |
                              ((long)(buf[8 + 5] & 0xff) << 16) |
                              ((long)(buf[8 + 6] & 0xff) <<  8) |
                              ((long)(buf[8 + 7] & 0xff)));

            for (int i = 0; i < Info.ATTRIBUTE_NUMBER; i++)
            {
                int j = i + Info.POSISION_INDEX_NUMBER
                        + Info.COMPLETE_COUNT_NUMBER ;

                attributes[i] = (((long)(buf[8*j + 0] & 0xff) << 56) |
                                 ((long)(buf[8*j + 1] & 0xff) << 48) |
                                 ((long)(buf[8*j + 3] & 0xff) << 32) |
                                 ((long)(buf[8*j + 4] & 0xff) << 24) |
                                 ((long)(buf[8*j + 5] & 0xff) << 16) |
                                 ((long)(buf[8*j + 6] & 0xff) <<  8) |
                                 ((long)(buf[8*j + 7] & 0xff)));
            }
        }
    }

    //解析带有元组完整维度平均值的元组
    public void parse_sub_average(byte[] buf)
    {
        if (buf.length != Info.TUPLE_AVERAGE_SUB_BYTES_LENGTH)
        {
            System.out.println("error");
            return;
        }
        else
        {
            sub_index = (((long)(buf[0] & 0xff) << 56) |
                         ((long)(buf[1] & 0xff) << 48) |
                         ((long)(buf[2] & 0xff) << 40) |
                         ((long)(buf[3] & 0xff) << 32) |
                         ((long)(buf[4] & 0xff) << 24) |
                         ((long)(buf[5] & 0xff) << 16) |
                         ((long)(buf[6] & 0xff) <<  8) |
                         ((long)(buf[7] & 0xff)));

            average_value = (((long)(buf[8 + 0] & 0xff) << 56) |
                             ((long)(buf[8 + 1] & 0xff) << 48) |
                             ((long)(buf[8 + 2] & 0xff) << 40) |
                             ((long)(buf[8 + 3] & 0xff) << 32) |
                             ((long)(buf[8 + 4] & 0xff) << 24) |
                             ((long)(buf[8 + 5] & 0xff) << 16) |
                             ((long)(buf[8 + 6] & 0xff) <<  8) |
                             ((long)(buf[8 + 7] & 0xff)));

            position_index = (((long)(buf[8*2 + 0] & 0xff) << 56) |
                              ((long)(buf[8*2 + 1] & 0xff) << 48) |
                              ((long)(buf[8*2 + 2] & 0xff) << 40) |
                              ((long)(buf[8*2 + 3] & 0xff) << 32) |
                              ((long)(buf[8*2 + 4] & 0xff) << 24) |
                              ((long)(buf[8*2 + 5] & 0xff) << 16) |
                              ((long)(buf[8*2 + 6] & 0xff) <<  8) |
                              ((long)(buf[8*2 + 7] & 0xff)));

            for (int i = 0; i < Info.ATTRIBUTE_NUMBER; i++)
            {
                int j = i + Info.POSISION_INDEX_NUMBER
                        + Info.AVERATGE_VALUE_NUMBER
                        + Info.SUB_NUM_NUMBER;

                attributes[i] = (((long)(buf[8*j + 0] & 0xff) << 56) |
                                 ((long)(buf[8*j + 1] & 0xff) << 48) |
                                 ((long)(buf[8*j + 2] & 0xff) << 40) |
                                 ((long)(buf[8*j + 3] & 0xff) << 32) |
                                 ((long)(buf[8*j + 4] & 0xff) << 24) |
                                 ((long)(buf[8*j + 5] & 0xff) << 16) |
                                 ((long)(buf[8*j + 6] & 0xff) <<  8) |
                                 ((long)(buf[8*j + 7] & 0xff)));
            }
        }
    }

    //解析带有元组完整维度平均值的元组
    public void parse_average_bucket(byte[] buf)
    {
        if (buf.length != Info.TUPLE_AVERAGE_BUCKET_BYTES_LENGTH)
        {
            System.out.println("error");
            return;
        }
        else
        {
            bucket_index = (((long)(buf[0] & 0xff) << 56) |
                            ((long)(buf[1] & 0xff) << 48) |
                            ((long)(buf[2] & 0xff) << 40) |
                            ((long)(buf[3] & 0xff) << 32) |
                            ((long)(buf[4] & 0xff) << 24) |
                            ((long)(buf[5] & 0xff) << 16) |
                            ((long)(buf[6] & 0xff) <<  8) |
                            ((long)(buf[7] & 0xff)));
            average_value =
                    (((long)(buf[8 + 0] & 0xff) << 56) |
                     ((long)(buf[8 + 1] & 0xff) << 48) |
                     ((long)(buf[8 + 2] & 0xff) << 40) |
                     ((long)(buf[8 + 3] & 0xff) << 32) |
                     ((long)(buf[8 + 4] & 0xff) << 24) |
                     ((long)(buf[8 + 5] & 0xff) << 16) |
                     ((long)(buf[8 + 6] & 0xff) <<  8) |
                     ((long)(buf[8 + 7] & 0xff)));

            position_index =
                    (((long)(buf[8*2 + 0] & 0xff) << 56) |
                     ((long)(buf[8*2 + 1] & 0xff) << 48) |
                     ((long)(buf[8*2 + 2] & 0xff) << 40) |
                     ((long)(buf[8*2 + 3] & 0xff) << 32) |
                     ((long)(buf[8*2 + 4] & 0xff) << 24) |
                     ((long)(buf[8*2 + 5] & 0xff) << 16) |
                     ((long)(buf[8*2 + 6] & 0xff) <<  8) |
                     ((long)(buf[8*2 + 7] & 0xff)));

            for (int i = 0; i < Info.ATTRIBUTE_NUMBER; i++)
            {
                int j = i + Info.POSISION_INDEX_NUMBER
                        + Info.AVERATGE_VALUE_NUMBER
                        + Info.BUCKET_INDEX_NUMBER;

                attributes[i] =
                       (((long)(buf[8*j + 0] & 0xff) << 56) |
                        ((long)(buf[8*j + 1] & 0xff) << 48) |
                        ((long)(buf[8*j + 3] & 0xff) << 32) |
                        ((long)(buf[8*j + 4] & 0xff) << 24) |
                        ((long)(buf[8*j + 5] & 0xff) << 16) |
                        ((long)(buf[8*j + 6] & 0xff) <<  8) |
                        ((long)(buf[8*j + 7] & 0xff)));
            }
        }
    }

    //解析带有元组完整维度平均值子表的元组
    public void parse_sub_average_bucket(byte[] buf)
    {
        if (buf.length != Info.TUPLE_SUB_AVERAGE_BUCKET_BYTES_LENGTH)
        {
            System.out.println("error");
            return;
        }
        else
        {
            bucket_index =
                    (((long)(buf[0] & 0xff) << 56) |
                    ((long)(buf[1] & 0xff) << 48) |
                    ((long)(buf[2] & 0xff) << 40) |
                    ((long)(buf[3] & 0xff) << 32) |
                    ((long)(buf[4] & 0xff) << 24) |
                    ((long)(buf[5] & 0xff) << 16) |
                    ((long)(buf[6] & 0xff) <<  8) |
                    ((long)(buf[7] & 0xff)));

            sub_index = (((long)(buf[8 + 0] & 0xff) << 56) |
                         ((long)(buf[8 + 1] & 0xff) << 48) |
                         ((long)(buf[8 + 2] & 0xff) << 40) |
                         ((long)(buf[8 + 3] & 0xff) << 32) |
                         ((long)(buf[8 + 4] & 0xff) << 24) |
                         ((long)(buf[8 + 5] & 0xff) << 16) |
                         ((long)(buf[8 + 6] & 0xff) <<  8) |
                         ((long)(buf[8 + 7] & 0xff)));



            average_value = (((long)(buf[8*2 + 0] & 0xff) << 56) |
                             ((long)(buf[8*2 + 1] & 0xff) << 48) |
                             ((long)(buf[8*2 + 2] & 0xff) << 40) |
                             ((long)(buf[8*2 + 3] & 0xff) << 32) |
                             ((long)(buf[8*2 + 4] & 0xff) << 24) |
                             ((long)(buf[8*2 + 5] & 0xff) << 16) |
                             ((long)(buf[8*2 + 6] & 0xff) <<  8) |
                             ((long)(buf[8*2 + 7] & 0xff)));

            position_index = (((long)(buf[8*3 + 0] & 0xff) << 56) |
                              ((long)(buf[8*3 + 1] & 0xff) << 48) |
                              ((long)(buf[8*3 + 2] & 0xff) << 40) |
                              ((long)(buf[8*3 + 3] & 0xff) << 32) |
                              ((long)(buf[8*3 + 4] & 0xff) << 24) |
                              ((long)(buf[8*3 + 5] & 0xff) << 16) |
                              ((long)(buf[8*3 + 6] & 0xff) <<  8) |
                              ((long)(buf[8*3 + 7] & 0xff)));

            for (int i = 0; i < Info.ATTRIBUTE_NUMBER; i++)
            {
                int j = i + Info.POSISION_INDEX_NUMBER
                        + Info.AVERATGE_VALUE_NUMBER
                        + Info.BUCKET_INDEX_NUMBER
                        + Info.SUB_NUM_NUMBER;

                attributes[i] = (((long)(buf[8*j + 0] & 0xff) << 56) |
                                 ((long)(buf[8*j + 1] & 0xff) << 48) |
                                 ((long)(buf[8*j + 3] & 0xff) << 32) |
                                 ((long)(buf[8*j + 4] & 0xff) << 24) |
                                 ((long)(buf[8*j + 5] & 0xff) << 16) |
                                 ((long)(buf[8*j + 6] & 0xff) <<  8) |
                                 ((long)(buf[8*j + 7] & 0xff)));
            }
        }
    }

    public void copyfrom(Tuple temp)
    {
        this.bucket_index = temp.bucket_index;

        this.sub_index = temp.sub_index;

        this.comlete_count = temp.comlete_count;

        this.average_value = temp.average_value;

        this.position_index = temp.position_index;

        for (int i = 0; i < Info.ATTRIBUTE_NUMBER; i++)
            this.attributes[i] = temp.attributes[i];

    }

    public String toString()
    {
        String str = new String();

        str += "TPI: " + this.temp_position + "|";

        str += "BI: " + this.bucket_index + "|";

        str += "sub_index:" + this.sub_index + "|";

        str += "Compelte_Count:" + this.comlete_count + "|";

        str += "average_value:" + this.average_value + "|";

        str += "PI: " + this.position_index + "|";

        for(int i = 0; i < Info.ATTRIBUTE_NUMBER; i++)
            str += this.attributes[i] + "|";

        str += "\r\n";

        return str;
    }
}
