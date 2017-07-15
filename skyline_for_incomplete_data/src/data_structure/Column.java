package src.data_structure;

import src.global.Info;


/**s
 * Created by Xue on 2017/7/14.
 */
public class Column
{
    public long sub_num;

    public long pi;
    public long value;

    //解析初始属性列
    public void parse_column(byte[] buf)
    {
        if (buf.length != Info.COLUMN_BYTES_LENGTH)
        {
            System.out.println("error");
            return;
        }
        else
        {
            pi =
                    (((long)(buf[0] & 0xff) << 56) |
                            ((long)(buf[1] & 0xff) << 48) |
                            ((long)(buf[2] & 0xff) << 40) |
                            ((long)(buf[3] & 0xff) << 32) |
                            ((long)(buf[4] & 0xff) << 24) |
                            ((long)(buf[5] & 0xff) << 16) |
                            ((long)(buf[6] & 0xff) <<  8) |
                            ((long)(buf[7] & 0xff)));

            value =
                    (((long)(buf[8 + 0] & 0xff) << 56) |
                            ((long)(buf[8 + 1] & 0xff) << 48) |
                            ((long)(buf[8 + 2] & 0xff) << 40) |
                            ((long)(buf[8 + 3] & 0xff) << 32) |
                            ((long)(buf[8 + 4] & 0xff) << 24) |
                            ((long)(buf[8 + 5] & 0xff) << 16) |
                            ((long)(buf[8 + 6] & 0xff) <<  8) |
                            ((long)(buf[8 + 7] & 0xff)));
        }
    }

    //解析初始属性列
    public void parse_sub_column(byte[] buf)
    {
        if (buf.length != Info.SUB_COLUMN_BYTES_LENGTH)
        {
            System.out.println("error");
            return;
        }
        else
        {
            sub_num =
                    (((long)(buf[0] & 0xff) << 56) |
                            ((long)(buf[1] & 0xff) << 48) |
                            ((long)(buf[2] & 0xff) << 40) |
                            ((long)(buf[3] & 0xff) << 32) |
                            ((long)(buf[4] & 0xff) << 24) |
                            ((long)(buf[5] & 0xff) << 16) |
                            ((long)(buf[6] & 0xff) <<  8) |
                            ((long)(buf[7] & 0xff)));

            pi =
                    (((long)(buf[8 + 0] & 0xff) << 56) |
                            ((long)(buf[8 + 1] & 0xff) << 48) |
                            ((long)(buf[8 + 2] & 0xff) << 40) |
                            ((long)(buf[8 + 3] & 0xff) << 32) |
                            ((long)(buf[8 + 4] & 0xff) << 24) |
                            ((long)(buf[8 + 5] & 0xff) << 16) |
                            ((long)(buf[8 + 6] & 0xff) <<  8) |
                            ((long)(buf[8 + 7] & 0xff)));

            value =
                    (((long)(buf[8*2 + 0] & 0xff) << 56) |
                            ((long)(buf[8*2 + 1] & 0xff) << 48) |
                            ((long)(buf[8*2 + 2] & 0xff) << 40) |
                            ((long)(buf[8*2 + 3] & 0xff) << 32) |
                            ((long)(buf[8*2 + 4] & 0xff) << 24) |
                            ((long)(buf[8*2 + 5] & 0xff) << 16) |
                            ((long)(buf[8*2 + 6] & 0xff) <<  8) |
                            ((long)(buf[8*2 + 7] & 0xff)));
        }
    }

    public void copyfrom(Column temp)
    {
        this.sub_num = temp.sub_num;

        this.pi = temp.pi;

        this.value = temp.value;
    }

    public String toString()
    {
        String str = new String();

        str += "Sub_Num: " + this.sub_num + "|";

        str += "PI: " + this.pi + "|";

        str += "Value: " + this.value;

        str += "\r\n";

        return str;
    }
}
