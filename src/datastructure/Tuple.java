package datastructure;

import global.Info;

public class Tuple {
    public long position_index;
    public long MPI;
    public long sub_index;

    public long[] measure_arr = new long[Info.MEASURE_ATTRIBUTE_NUMBER];
    public long[] select_arr = new long[Info.SELECT_ATTRIBUTE_NUMBER];


    public void parse_initial(byte[] buf) {
        if (buf.length != Info.TUPLE_INITIAL_BYTES_LENGTH) {
            System.out.println("error");
            return;
        } else {
            position_index =
                    (((long) (buf[0] & 0xff) << 56) |
                            ((long) (buf[1] & 0xff) << 48) |
                            ((long) (buf[2] & 0xff) << 40) |
                            ((long) (buf[3] & 0xff) << 32) |
                            ((long) (buf[4] & 0xff) << 24) |
                            ((long) (buf[5] & 0xff) << 16) |
                            ((long) (buf[6] & 0xff) << 8) |
                            ((long) (buf[7] & 0xff)));

            MPI = Long.MAX_VALUE;

            for (int i = 0; i < Info.MEASURE_ATTRIBUTE_NUMBER; i++) {
                int j = i + Info.POSISION_INDEX;

                measure_arr[i] =
                        (((long) (buf[8 * j + 0] & 0xff) << 56) |
                                ((long) (buf[8 * j + 1] & 0xff) << 48) |
                                ((long) (buf[8 * j + 2] & 0xff) << 40) |
                                ((long) (buf[8 * j + 3] & 0xff) << 32) |
                                ((long) (buf[8 * j + 4] & 0xff) << 24) |
                                ((long) (buf[8 * j + 5] & 0xff) << 16) |
                                ((long) (buf[8 * j + 6] & 0xff) << 8) |
                                ((long) (buf[8 * j + 7] & 0xff)));
            }
            for (int i = 0; i < Info.SELECT_ATTRIBUTE_NUMBER; i++) {
                int j = i + Info.POSISION_INDEX + Info.MEASURE_ATTRIBUTE_NUMBER;

                select_arr[i] =
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

    public void parse_tuple(byte[] buf) {
        if (buf.length != Info.TUPLE_BYTES_LENGTH) {
            System.out.println("error");
            return;
        } else {
            position_index =
                    (((long) (buf[0] & 0xff) << 56) |
                            ((long) (buf[1] & 0xff) << 48) |
                            ((long) (buf[2] & 0xff) << 40) |
                            ((long) (buf[3] & 0xff) << 32) |
                            ((long) (buf[4] & 0xff) << 24) |
                            ((long) (buf[5] & 0xff) << 16) |
                            ((long) (buf[6] & 0xff) << 8) |
                            ((long) (buf[7] & 0xff)));

            MPI =
                    (((long) (buf[8 + 0] & 0xff) << 56) |
                            ((long) (buf[8 + 1] & 0xff) << 48) |
                            ((long) (buf[8 + 2] & 0xff) << 40) |
                            ((long) (buf[8 + 3] & 0xff) << 32) |
                            ((long) (buf[8 + 4] & 0xff) << 24) |
                            ((long) (buf[8 + 5] & 0xff) << 16) |
                            ((long) (buf[8 + 6] & 0xff) << 8) |
                            ((long) (buf[8 + 7] & 0xff)));

            for (int i = 0; i < Info.MEASURE_ATTRIBUTE_NUMBER; i++) {
                int j = i + Info.POSISION_INDEX + Info.MPI_INDEX;

                measure_arr[i] =
                        (((long) (buf[8 * j + 0] & 0xff) << 56) |
                                ((long) (buf[8 * j + 1] & 0xff) << 48) |
                                ((long) (buf[8 * j + 2] & 0xff) << 40) |
                                ((long) (buf[8 * j + 3] & 0xff) << 32) |
                                ((long) (buf[8 * j + 4] & 0xff) << 24) |
                                ((long) (buf[8 * j + 5] & 0xff) << 16) |
                                ((long) (buf[8 * j + 6] & 0xff) << 8) |
                                ((long) (buf[8 * j + 7] & 0xff)));
            }
            for (int i = 0; i < Info.SELECT_ATTRIBUTE_NUMBER; i++) {
                int j = i + Info.POSISION_INDEX + Info.MPI_INDEX +
                        Info.MEASURE_ATTRIBUTE_NUMBER;

                select_arr[i] =
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

    public void parse_sub_tuple(byte[] buf) {
        if (buf.length != Info.TUPLE_SUB_BYTES_LENGTH) {
            System.out.println("error");
            return;
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

            position_index =
                    (((long) (buf[8 + 0] & 0xff) << 56) |
                            ((long) (buf[8 + 1] & 0xff) << 48) |
                            ((long) (buf[8 + 2] & 0xff) << 40) |
                            ((long) (buf[8 + 3] & 0xff) << 32) |
                            ((long) (buf[8 + 4] & 0xff) << 24) |
                            ((long) (buf[8 + 5] & 0xff) << 16) |
                            ((long) (buf[8 + 6] & 0xff) << 8) |
                            ((long) (buf[8 + 7] & 0xff)));

            MPI =
                    (((long) (buf[8 * 2 + 0] & 0xff) << 56) |
                            ((long) (buf[8 * 2 + 1] & 0xff) << 48) |
                            ((long) (buf[8 * 2 + 2] & 0xff) << 40) |
                            ((long) (buf[8 * 2 + 3] & 0xff) << 32) |
                            ((long) (buf[8 * 2 + 4] & 0xff) << 24) |
                            ((long) (buf[8 * 2 + 5] & 0xff) << 16) |
                            ((long) (buf[8 * 2 + 6] & 0xff) << 8) |
                            ((long) (buf[8 * 2 + 7] & 0xff)));

            for (int i = 0; i < Info.MEASURE_ATTRIBUTE_NUMBER; i++) {
                int j = i + Info.POSISION_INDEX
                        + Info.MPI_INDEX + Info.SUB_INDEX;

                measure_arr[i] =
                        (((long) (buf[8 * j + 0] & 0xff) << 56) |
                                ((long) (buf[8 * j + 1] & 0xff) << 48) |
                                ((long) (buf[8 * j + 2] & 0xff) << 40) |
                                ((long) (buf[8 * j + 3] & 0xff) << 32) |
                                ((long) (buf[8 * j + 4] & 0xff) << 24) |
                                ((long) (buf[8 * j + 5] & 0xff) << 16) |
                                ((long) (buf[8 * j + 6] & 0xff) << 8) |
                                ((long) (buf[8 * j + 7] & 0xff)));
            }
            for (int i = 0; i < Info.SELECT_ATTRIBUTE_NUMBER; i++) {
                int j = i + Info.POSISION_INDEX + Info.MPI_INDEX +
                        +Info.SUB_INDEX + Info.MEASURE_ATTRIBUTE_NUMBER;

                select_arr[i] =
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

    public void copyfrom(Tuple temp) {
        this.sub_index = temp.sub_index;
        this.position_index = temp.position_index;
        this.MPI = temp.MPI;

        for (int i = 0; i < measure_arr.length; i++)
            this.measure_arr[i] = temp.measure_arr[i];

        for (int i = 0; i < select_arr.length; i++)
            this.select_arr[i] = temp.select_arr[i];

    }

    public String toString() {
        String str = new String();
        str = "spi: " + this.sub_index + "|";

        str += "PI: " + this.position_index + "|";

        str += "MPI: " + this.MPI + "|";

        for (int i = 0; i < Info.DOMINATE_RELATED_ATTRIBUTE; i++)
            str += this.measure_arr[i] + "|";

        str += "-|";

        for (int i = 0; i < Info.RANGE_RELATED_ATTRIBUTE; i++)
            str += this.select_arr[i] + "|";
        str += "\r\n";

        return str;
    }

}
