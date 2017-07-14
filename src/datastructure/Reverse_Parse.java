package datastructure;

import global.Info;

public class Reverse_Parse {
    public byte[] rev_parse_colume(Column temp) {
        byte[] buf = new byte[Info.COLUNME_BYTE_LENGTH];

        buf[0] = (byte) (0xff & (temp.table_position >> 56));
        buf[1] = (byte) (0xff & (temp.table_position >> 48));
        buf[2] = (byte) (0xff & (temp.table_position >> 40));
        buf[3] = (byte) (0xff & (temp.table_position >> 32));
        buf[4] = (byte) (0xff & (temp.table_position >> 24));
        buf[5] = (byte) (0xff & (temp.table_position >> 16));
        buf[6] = (byte) (0xff & (temp.table_position >> 8));
        buf[7] = (byte) (0xff & temp.table_position);

        buf[8 + 0] = (byte) (0xff & (temp.column_value >> 56));
        buf[8 + 1] = (byte) (0xff & (temp.column_value >> 48));
        buf[8 + 2] = (byte) (0xff & (temp.column_value >> 40));
        buf[8 + 3] = (byte) (0xff & (temp.column_value >> 32));
        buf[8 + 4] = (byte) (0xff & (temp.column_value >> 24));
        buf[8 + 5] = (byte) (0xff & (temp.column_value >> 16));
        buf[8 + 6] = (byte) (0xff & (temp.column_value >> 8));
        buf[8 + 7] = (byte) (0xff & temp.column_value);

        return buf;
    }

    public byte[] rev_parse_tuple(Tuple temp) {
        byte[] buf = new byte[Info.TUPLE_BYTES_LENGTH];

        buf[0] = (byte) (0xff & (temp.position_index >> 56));
        buf[1] = (byte) (0xff & (temp.position_index >> 48));
        buf[2] = (byte) (0xff & (temp.position_index >> 40));
        buf[3] = (byte) (0xff & (temp.position_index >> 32));
        buf[4] = (byte) (0xff & (temp.position_index >> 24));
        buf[5] = (byte) (0xff & (temp.position_index >> 16));
        buf[6] = (byte) (0xff & (temp.position_index >> 8));
        buf[7] = (byte) (0xff & temp.position_index);

        buf[8 + 0] = (byte) (0xff & (temp.MPI >> 56));
        buf[8 + 1] = (byte) (0xff & (temp.MPI >> 48));
        buf[8 + 2] = (byte) (0xff & (temp.MPI >> 40));
        buf[8 + 3] = (byte) (0xff & (temp.MPI >> 32));
        buf[8 + 4] = (byte) (0xff & (temp.MPI >> 24));
        buf[8 + 5] = (byte) (0xff & (temp.MPI >> 16));
        buf[8 + 6] = (byte) (0xff & (temp.MPI >> 8));
        buf[8 + 7] = (byte) (0xff & temp.MPI);


        int attribute_start = Info.POSISION_INDEX + Info.MPI_INDEX;
        for (int i = 0; i < Info.MEASURE_ATTRIBUTE_NUMBER; i++) {

            buf[8 * (attribute_start + i) + 0] = (byte) (0xff & (temp.measure_arr[i] >> 56));
            buf[8 * (attribute_start + i) + 1] = (byte) (0xff & (temp.measure_arr[i] >> 48));
            buf[8 * (attribute_start + i) + 2] = (byte) (0xff & (temp.measure_arr[i] >> 40));
            buf[8 * (attribute_start + i) + 3] = (byte) (0xff & (temp.measure_arr[i] >> 32));
            buf[8 * (attribute_start + i) + 4] = (byte) (0xff & (temp.measure_arr[i] >> 24));
            buf[8 * (attribute_start + i) + 5] = (byte) (0xff & (temp.measure_arr[i] >> 16));
            buf[8 * (attribute_start + i) + 6] = (byte) (0xff & (temp.measure_arr[i] >> 8));
            buf[8 * (attribute_start + i) + 7] = (byte) (0xff & temp.measure_arr[i]);
        }

        attribute_start = Info.POSISION_INDEX + Info.MPI_INDEX
                + Info.MEASURE_ATTRIBUTE_NUMBER;
        for (int i = 0; i < Info.SELECT_ATTRIBUTE_NUMBER; i++) {

            buf[8 * (attribute_start + i) + 0] = (byte) (0xff & (temp.select_arr[i] >> 56));
            buf[8 * (attribute_start + i) + 1] = (byte) (0xff & (temp.select_arr[i] >> 48));
            buf[8 * (attribute_start + i) + 2] = (byte) (0xff & (temp.select_arr[i] >> 40));
            buf[8 * (attribute_start + i) + 3] = (byte) (0xff & (temp.select_arr[i] >> 32));
            buf[8 * (attribute_start + i) + 4] = (byte) (0xff & (temp.select_arr[i] >> 24));
            buf[8 * (attribute_start + i) + 5] = (byte) (0xff & (temp.select_arr[i] >> 16));
            buf[8 * (attribute_start + i) + 6] = (byte) (0xff & (temp.select_arr[i] >> 8));
            buf[8 * (attribute_start + i) + 7] = (byte) (0xff & temp.select_arr[i]);
        }

        return buf;
    }

    public byte[] rev_parse_select_min_max(Select_Min_Max_Value temp) {
        byte[] buf = new byte[Info.COLUMN_SUB_BYTE_LENGTH];

        buf[0] = (byte) (0xff & (temp.min_value >> 56));
        buf[1] = (byte) (0xff & (temp.min_value >> 48));
        buf[2] = (byte) (0xff & (temp.min_value >> 40));
        buf[3] = (byte) (0xff & (temp.min_value >> 32));
        buf[4] = (byte) (0xff & (temp.min_value >> 24));
        buf[5] = (byte) (0xff & (temp.min_value >> 16));
        buf[6] = (byte) (0xff & (temp.min_value >> 8));
        buf[7] = (byte) (0xff & temp.min_value);

        buf[8 + 0] = (byte) (0xff & (temp.max_value >> 56));
        buf[8 + 1] = (byte) (0xff & (temp.max_value >> 48));
        buf[8 + 2] = (byte) (0xff & (temp.max_value >> 40));
        buf[8 + 3] = (byte) (0xff & (temp.max_value >> 32));
        buf[8 + 4] = (byte) (0xff & (temp.max_value >> 24));
        buf[8 + 5] = (byte) (0xff & (temp.max_value >> 16));
        buf[8 + 6] = (byte) (0xff & (temp.max_value >> 8));
        buf[8 + 7] = (byte) (0xff & temp.max_value);

        buf[8 * 2 + 0] = (byte) (0xff & (temp.number_per_range >> 56));
        buf[8 * 2 + 1] = (byte) (0xff & (temp.number_per_range >> 48));
        buf[8 * 2 + 2] = (byte) (0xff & (temp.number_per_range >> 40));
        buf[8 * 2 + 3] = (byte) (0xff & (temp.number_per_range >> 32));
        buf[8 * 2 + 4] = (byte) (0xff & (temp.number_per_range >> 24));
        buf[8 * 2 + 5] = (byte) (0xff & (temp.number_per_range >> 16));
        buf[8 * 2 + 6] = (byte) (0xff & (temp.number_per_range >> 8));
        buf[8 * 2 + 7] = (byte) (0xff & temp.number_per_range);

        return buf;
    }

    public byte[] rev_parse_LESStuple(LESS_Tuple temp) {
        byte[] buf = new byte[Info.TUPLE_SUB_BYTES_LENGTH];

        buf[0] = (byte) (0xff & (temp.pi >> 56));
        buf[1] = (byte) (0xff & (temp.pi >> 48));
        buf[2] = (byte) (0xff & (temp.pi >> 40));
        buf[3] = (byte) (0xff & (temp.pi >> 32));
        buf[4] = (byte) (0xff & (temp.pi >> 24));
        buf[5] = (byte) (0xff & (temp.pi >> 16));
        buf[6] = (byte) (0xff & (temp.pi >> 8));
        buf[7] = (byte) (0xff & temp.pi);

        long byteentropy = Double.doubleToRawLongBits(temp.entroty);
        buf[8 + 0] = (byte) (0xff & (byteentropy >> 56));
        buf[8 + 1] = (byte) (0xff & (byteentropy >> 48));
        buf[8 + 2] = (byte) (0xff & (byteentropy >> 40));
        buf[8 + 3] = (byte) (0xff & (byteentropy >> 32));
        buf[8 + 4] = (byte) (0xff & (byteentropy >> 24));
        buf[8 + 5] = (byte) (0xff & (byteentropy >> 16));
        buf[8 + 6] = (byte) (0xff & (byteentropy >> 8));
        buf[8 + 7] = (byte) (0xff & byteentropy);

        long sub_pi = temp.merged_index;
        buf[8 * 2 + 0] = (byte) (0xff & (sub_pi >> 56));
        buf[8 * 2 + 1] = (byte) (0xff & (sub_pi >> 48));
        buf[8 * 2 + 2] = (byte) (0xff & (sub_pi >> 40));
        buf[8 * 2 + 3] = (byte) (0xff & (sub_pi >> 32));
        buf[8 * 2 + 4] = (byte) (0xff & (sub_pi >> 24));
        buf[8 * 2 + 5] = (byte) (0xff & (sub_pi >> 16));
        buf[8 * 2 + 6] = (byte) (0xff & (sub_pi >> 8));
        buf[8 * 2 + 7] = (byte) (0xff & sub_pi);

        int attribute_start = Info.POSISION_INDEX + Info.ENTROPY_SCORES_NUMBER
                + Info.SUB_INDEX;
        for (int i = 0; i < Info.ATTRIBUTE_NUMBER; i++) {

            buf[8 * (attribute_start + i) + 0] = (byte) (0xff & (temp.attribute_arr[i] >> 56));
            buf[8 * (attribute_start + i) + 1] = (byte) (0xff & (temp.attribute_arr[i] >> 48));
            buf[8 * (attribute_start + i) + 2] = (byte) (0xff & (temp.attribute_arr[i] >> 40));
            buf[8 * (attribute_start + i) + 3] = (byte) (0xff & (temp.attribute_arr[i] >> 32));
            buf[8 * (attribute_start + i) + 4] = (byte) (0xff & (temp.attribute_arr[i] >> 24));
            buf[8 * (attribute_start + i) + 5] = (byte) (0xff & (temp.attribute_arr[i] >> 16));
            buf[8 * (attribute_start + i) + 6] = (byte) (0xff & (temp.attribute_arr[i] >> 8));
            buf[8 * (attribute_start + i) + 7] = (byte) (0xff & temp.attribute_arr[i]);
        }

        return buf;
    }

}
