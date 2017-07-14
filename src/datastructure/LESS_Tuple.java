package datastructure;

import global.Info;

public class LESS_Tuple {
    public long pi;
    public double entroty;
    public long[] attribute_arr = new long[Info.ATTRIBUTE_NUMBER];

    public int merged_index = -1;

    public void parse_initial(byte[] buf) {
        if (buf.length != Info.TUPLE_INITIAL_BYTES_LENGTH) {
            System.out.println("error");
            return;
        } else {
            pi =
                    (((long) (buf[0] & 0xff) << 56) |
                            ((long) (buf[1] & 0xff) << 48) |
                            ((long) (buf[2] & 0xff) << 40) |
                            ((long) (buf[3] & 0xff) << 32) |
                            ((long) (buf[4] & 0xff) << 24) |
                            ((long) (buf[5] & 0xff) << 16) |
                            ((long) (buf[6] & 0xff) << 8) |
                            ((long) (buf[7] & 0xff)));

            for (int i = 0; i < Info.ATTRIBUTE_NUMBER; i++) {
                int j = i + Info.POSISION_INDEX;

                attribute_arr[i] =
                        (((long) (buf[8 * j + 0] & 0xff) << 56) |
                                ((long) (buf[8 * j + 1] & 0xff) << 48) |
                                ((long) (buf[8 * j + 2] & 0xff) << 40) |
                                ((long) (buf[8 * j + 3] & 0xff) << 32) |
                                ((long) (buf[8 * j + 4] & 0xff) << 24) |
                                ((long) (buf[8 * j + 5] & 0xff) << 16) |
                                ((long) (buf[8 * j + 6] & 0xff) << 8) |
                                ((long) (buf[8 * j + 7] & 0xff)));
            }

            entroty = 0;
            //计算熵值
            for (int k = 0; k < Info.DOMINATE_RELATED_ATTRIBUTE; k++) {
                entroty += Math.abs(Math.log(attribute_arr[k]));
            }
        }
    }

    public void parse_with_entropy(byte[] buf) {
        if (buf.length != Info.TUPLE_SUB_BYTES_LENGTH) {
            System.out.println("error");
            return;
        } else {
            pi =
                    (((long) (buf[0] & 0xff) << 56) |
                            ((long) (buf[1] & 0xff) << 48) |
                            ((long) (buf[2] & 0xff) << 40) |
                            ((long) (buf[3] & 0xff) << 32) |
                            ((long) (buf[4] & 0xff) << 24) |
                            ((long) (buf[5] & 0xff) << 16) |
                            ((long) (buf[6] & 0xff) << 8) |
                            ((long) (buf[7] & 0xff)));

            long en =
                    (((long) (buf[0 + 8] & 0xff) << 56) |
                            ((long) (buf[1 + 8] & 0xff) << 48) |
                            ((long) (buf[2 + 8] & 0xff) << 40) |
                            ((long) (buf[3 + 8] & 0xff) << 32) |
                            ((long) (buf[4 + 8] & 0xff) << 24) |
                            ((long) (buf[5 + 8] & 0xff) << 16) |
                            ((long) (buf[6 + 8] & 0xff) << 8) |
                            ((long) (buf[7 + 8] & 0xff)));

            entroty = Double.longBitsToDouble(en);

            long sub_pi =
                    (((long) (buf[0 + 8 * 2] & 0xff) << 56) |
                            ((long) (buf[1 + 8 * 2] & 0xff) << 48) |
                            ((long) (buf[2 + 8 * 2] & 0xff) << 40) |
                            ((long) (buf[3 + 8 * 2] & 0xff) << 32) |
                            ((long) (buf[4 + 8 * 2] & 0xff) << 24) |
                            ((long) (buf[5 + 8 * 2] & 0xff) << 16) |
                            ((long) (buf[6 + 8 * 2] & 0xff) << 8) |
                            ((long) (buf[7 + 8 * 2] & 0xff)));
            merged_index = (int) sub_pi;

            for (int i = 0; i < Info.ATTRIBUTE_NUMBER; i++) {
                int j = i + Info.POSISION_INDEX + Info.ENTROPY_SCORES_NUMBER;

                attribute_arr[i] =
                        (((long) (buf[8 * j + 0] & 0xff) << 56) |
                                ((long) (buf[8 * j + 1] & 0xff) << 48) |
                                ((long) (buf[8 * j + 2] & 0xff) << 40) |
                                ((long) (buf[8 * j + 3] & 0xff) << 32) |
                                ((long) (buf[8 * j + 4] & 0xff) << 24) |
                                ((long) (buf[8 * j + 5] & 0xff) << 16) |
                                ((long) (buf[8 * j + 6] & 0xff) << 8) |
                                ((long) (buf[8 * j + 7] & 0xff)));
            }

        }
    }

    public void copyfrom(LESS_Tuple temp) {
        this.pi = temp.pi;
        this.entroty = temp.entroty;

        this.merged_index = temp.merged_index;

        for (int i = 0; i < attribute_arr.length; i++) {
            this.attribute_arr[i] = temp.attribute_arr[i];

        }
    }

    public String toString() {
        String str = new String();
        str = "PI: " + this.pi + "|";

        for (int i = 0; i < Info.DOMINATE_RELATED_ATTRIBUTE; i++)
            str += this.attribute_arr[i] + "|";
        str += "\r\n";

        return str;
    }

}
