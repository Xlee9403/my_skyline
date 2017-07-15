package src.global;

/**
 * Created by Xue on 2017/7/14.
 */
public class Info
{
    public static String ROOT_PATH =
            "D:/mydatabase/Skyline for Incomplete Data/";
    public static String INITIAL_TABLE_PATH = "initial_table.tbl";

    public static String COPY_INITIAL_TABLE_PATH = "copy_initial_table.tbl";

    public static String CANDIDATE_TABLE_PATH = "candidate_table";

    public static String TABLE_WITH_BUCKET_NUM_PATH =
            "table_with_bucket_num.tbl";

    public static String TABLE_COMPLETE_ATTRIBUTE_COUNT_PATH =
            "table_with_complete_attribute_count.tbl";

    public static String TABLE_AVERAGE_VALUE_PATH =
            "table_with_average_value_.tbl";

    public static String SORTED_AVERAGE_TABLE_PATH =
            "sorted_table_with_average_value.tbl";

    public static String TABLE_WITH_SORTED_PI_PATH =
            "table_with_sorted_pi.tbl";

    public static String SUB_AVERAGE_PATH = "sub_average_";

    //桶号和位图映射表
    public static String BUCKET_NUM_TO_BITMAP =
            "bucket_number_to_bitmap.tbl";

    public static String SORTED_BUCKET_NUM_TABLE_PATH =
            "sorted_bucket_num.tbl";
    public static String SUB_TABLE_PATH = "sub_table";

    public static String INTERVAL = "_";
    public static String extension = ".tbl";

    public static String SUB_PATH = "Sub";

    public static String COLUMN_ROOT = "column/";
    public static String COLUMN_PATH = "column";

    public static String COLUMN_SORT_PATH = "column_sort";

    public static String COLUMN_COUNT_PATH = "column_count.tbl";

    public static double incompleteness_ratio = 0.2;

    public static int TUPLE_NUMBER = 100000;
    public static int ATTRIBUTE_NUMBER = 20;
    public static int POSISION_INDEX_NUMBER = 1;
    public static int BUCKET_INDEX_NUMBER = 1;
    public static int COMPLETE_COUNT_NUMBER = 1;
    public static int SUB_NUM_NUMBER = 1;
    public static int AVERATGE_VALUE_NUMBER = 1;
    public static int RELATED_ATTRIBUTES_NUMBER = 12;

    public static int ATTRIBUTE_BYTES_LENGTH = 8;

    public static int COLUMN_BYTES_LENGTH =
            2 * Info.ATTRIBUTE_BYTES_LENGTH;
    public static int SUB_COLUMN_BYTES_LENGTH =
            3 * Info.ATTRIBUTE_BYTES_LENGTH;

    public static int TUPLE_INITIAL_BYTES_LENGTH = (Info.ATTRIBUTE_NUMBER
            + Info.POSISION_INDEX_NUMBER) * Info.ATTRIBUTE_BYTES_LENGTH;

    public static int TUPLE_BUCKET_BYTES_LENGTH = (Info.ATTRIBUTE_NUMBER +
            Info.POSISION_INDEX_NUMBER + Info.BUCKET_INDEX_NUMBER)
            * Info.ATTRIBUTE_BYTES_LENGTH;

    public static int TUPLE_COMPLETE_COUNT_BYTES_LENGTH =
            (Info.ATTRIBUTE_NUMBER + Info.POSISION_INDEX_NUMBER
                    + Info.COMPLETE_COUNT_NUMBER) * Info.ATTRIBUTE_BYTES_LENGTH;

    public static int TUPLE_AVERAGE_BYTES_LENGTH = (Info.ATTRIBUTE_NUMBER
            + Info.POSISION_INDEX_NUMBER + Info.AVERATGE_VALUE_NUMBER)
            * Info.ATTRIBUTE_BYTES_LENGTH;

    public static int TUPLE_AVERAGE_SUB_BYTES_LENGTH =
            (Info.ATTRIBUTE_NUMBER + Info.SUB_NUM_NUMBER
                    + Info.POSISION_INDEX_NUMBER + Info.AVERATGE_VALUE_NUMBER)
                    * Info.ATTRIBUTE_BYTES_LENGTH;

    public static int ALLOCATED_MEMEORY_SIZE = 256*1024*1024;

    public static int BLOCK_SIZE = 10000;
}
