package datastructure;

import global.Info;

public class Column {
    public long table_position;
    public long column_value;

    public long sub_index;

    public void parse(byte[] buf) {
        if (buf.length != Info.COLUNME_BYTE_LENGTH) {
            System.out.println("error");
        } else {
            table_position =
                    (((long) (buf[0] & 0xff) << 56) |
                            ((long) (buf[1] & 0xff) << 48) |
                            ((long) (buf[2] & 0xff) << 40) |
                            ((long) (buf[3] & 0xff) << 32) |
                            ((long) (buf[4] & 0xff) << 24) |
                            ((long) (buf[5] & 0xff) << 16) |
                            ((long) (buf[6] & 0xff) << 8) |
                            ((long) (buf[7] & 0xff)));

            column_value =
                    (((long) (buf[8 + 0] & 0xff) << 56) |
                            ((long) (buf[8 + 1] & 0xff) << 48) |
                            ((long) (buf[8 + 2] & 0xff) << 40) |
                            ((long) (buf[8 + 3] & 0xff) << 32) |
                            ((long) (buf[8 + 4] & 0xff) << 24) |
                            ((long) (buf[8 + 5] & 0xff) << 16) |
                            ((long) (buf[8 + 6] & 0xff) << 8) |
                            ((long) (buf[8 + 7] & 0xff)));
        }
    }

    public void parse_sub_column(byte[] buf) {
        if (buf.length != Info.COLUMN_SUB_BYTE_LENGTH) {
            System.out.println("error");
        } else {
            sub_index =
                    (((long) (buf[0] & 0xff) << 56) |
                            ((long) (buf[1] & 0xff) << 48) |
                            ((long) (buf[2] & 0xff) << 40) |
                            ((long) (buf[3] & 0xff) << 32) |
                            ((long) (buf[4] & 0xff) << 24) |
                            ((long) (buf[5] & 0xff) << 16) |
                            ((long) (buf[6] & 0xff) << 8) |
                            ((long) (buf[7] & 0xff)));

            table_position =
                    (((long) (buf[8 + 0] & 0xff) << 56) |
                            ((long) (buf[8 + 1] & 0xff) << 48) |
                            ((long) (buf[8 + 2] & 0xff) << 40) |
                            ((long) (buf[8 + 3] & 0xff) << 32) |
                            ((long) (buf[8 + 4] & 0xff) << 24) |
                            ((long) (buf[8 + 5] & 0xff) << 16) |
                            ((long) (buf[8 + 6] & 0xff) << 8) |
                            ((long) (buf[8 + 7] & 0xff)));

            column_value =
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


    public void copyfrom(Column temp) {
        this.table_position = temp.table_position;
        this.column_value = temp.column_value;

        this.sub_index = temp.sub_index;
    }

    public String toString() {
        String str = new String();
        str = "PI: " + this.table_position + "|";

        str += "value: " + this.column_value + "|";
        str += "\r\n";

        return str;
    }

}
