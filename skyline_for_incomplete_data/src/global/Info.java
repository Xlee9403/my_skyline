package src.global;

import org.omg.CORBA.INV_FLAG;

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

    public static String SCAN_TABLE_PATH = "scan_table.tbl";
    public static String SCAN_AVERAGE_BUCKET_TABLE_PATH =
            "scan_average_bucket_table.tbl";

    public static String SCAN_COMPARE_TABLE_PATH =
            "scan_compare_table.tbl";

    public static String SCAN_CANDIDATE_TABLE_PATH =
            "scan_candidate_table.tbl";

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

    public static String COLUMN_SORT_PI_PATH = "column_sort_pi";

    public static String COLUMN_SORT_PATH = "column_sort";

    public static String COLUMN_COUNT_PATH = "column_count.tbl";

    public static String BUCKET_ROOT = "bucket/";
    public static String BUCKET_PATH = "bucket_";
    public static String BUCKET_SKYLINE_PATH = "bucket_skyline_";

    public static String SHADOW_ROOT = "shadow/";
    public static String SHADOW_PATH = "shadow";

    public static String SHADOW_SKYLINE_PATH = "shadow_skyline.tbl";

    public static String PRUNE_BLOCK_PATH = "prune_block.tbl";

    public static String sort_prefix = "sorted";

    public static String BITMAP_ROOT = "bitmap/";
    public static String BITMAP_PATH = "bitmap";


    public static double incompleteness_ratio = 0.1;

    public static int TUPLE_NUMBER = 100000;
    public static int ATTRIBUTE_NUMBER = 6;
    public static int POSISION_INDEX_NUMBER = 1;
    public static int BUCKET_INDEX_NUMBER = 1;
    public static int COMPLETE_COUNT_NUMBER = 1;
    public static int SUB_NUM_NUMBER = 1;
    public static int AVERATGE_VALUE_NUMBER = 1;
    public static int RELATED_ATTRIBUTES_NUMBER = 6;

    public static int ATTRIBUTE_BYTES_LENGTH = 8;

    public static int PRUNE_NUM = 10;

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

    public static int TUPLE_AVERAGE_BUCKET_BYTES_LENGTH =
            (Info.ATTRIBUTE_NUMBER + Info.AVERATGE_VALUE_NUMBER
                    + Info.BUCKET_INDEX_NUMBER + Info.POSISION_INDEX_NUMBER)
                    * Info.ATTRIBUTE_BYTES_LENGTH;

    public static int TUPLE_SUB_AVERAGE_BUCKET_BYTES_LENGTH =
            (Info.ATTRIBUTE_NUMBER + Info.AVERATGE_VALUE_NUMBER
             + Info.BUCKET_INDEX_NUMBER + Info.POSISION_INDEX_NUMBER
             + Info.SUB_NUM_NUMBER) * Info.ATTRIBUTE_BYTES_LENGTH;

    public static int BIT_BLOCK_SIZE = 4*1024;

    public static double LOGARITHMIC_BASE = 2.0;

    public static int ALLOCATED_MEMEORY_SIZE = 256*1024*1024;

    public static int BLOCK_SIZE = 10000;

    public static int BIT_READ_SIZE = Info.BLOCK_SIZE / 8;
}
