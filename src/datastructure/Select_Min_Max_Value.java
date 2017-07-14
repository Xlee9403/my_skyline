package datastructure;

import global.Info;

public class Select_Min_Max_Value {
    public long min_value;
    public long max_value;

    //每个范围包含的不同值的数量
    public long number_per_range;

    public void parse_min_max(byte[] buf) {
        if (buf.length != Info.ATTRIBUTE_LENGTH * 3) {
            System.out.println("error");
        } else {
            min_value =
                    (((long) (buf[0] & 0xff) << 56) |
                            ((long) (buf[1] & 0xff) << 48) |
                            ((long) (buf[2] & 0xff) << 40) |
                            ((long) (buf[3] & 0xff) << 32) |
                            ((long) (buf[4] & 0xff) << 24) |
                            ((long) (buf[5] & 0xff) << 16) |
                            ((long) (buf[6] & 0xff) << 8) |
                            ((long) (buf[7] & 0xff)));

            max_value =
                    (((long) (buf[8 + 0] & 0xff) << 56) |
                            ((long) (buf[8 + 1] & 0xff) << 48) |
                            ((long) (buf[8 + 2] & 0xff) << 40) |
                            ((long) (buf[8 + 3] & 0xff) << 32) |
                            ((long) (buf[8 + 4] & 0xff) << 24) |
                            ((long) (buf[8 + 5] & 0xff) << 16) |
                            ((long) (buf[8 + 6] & 0xff) << 8) |
                            ((long) (buf[8 + 7] & 0xff)));

            number_per_range =
                    (((long) (buf[8 * 2 + 0] & 0xff) << 56) |
                            ((long) (buf[8 * 2 + 1] & 0xff) << 48) |
                            ((long) (buf[8 * 2 + 2] & 0xff) << 40) |
                            ((long) (buf[8 * 2 + 3] & 0xff) << 32) |
                            ((long) (buf[8 * 2 + 4] & 0xff) << 24) |
                            ((long) (buf[8 * 2 + 5] & 0xff) << 16) |
                            ((long) (buf[8 * 2 + 6] & 0xff) << 8) |
                            ((long) (buf[8 * 2 + 7] & 0xff)));
        }
    }

    public String toString() {
        String str = new String();
        str = "min_value: " + this.min_value + "|";

        str += "max_value: " + this.max_value + "|";

        str += "number_per_range: " + this.number_per_range + "|";

        str += "\r\n";

        return str;
    }

}
