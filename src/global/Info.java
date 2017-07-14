package global;

public class Info {
//    public static String ROOT_PATH = "D:/mydatabase/Range_Skyline/";
    public static String ROOT_PATH = "./test_data/";
    public static String GENERATE_TABLE = "initial_table.tbl";

    public static String MEASURE_COLUMN_ROOT = "measure_colume/";
    public static String PREDICATE_ROOT = "predicata_path/";
    public static String SUB_SCAN_ROOT = "sub_scan_table/";
//	public static String SUB_COLUMN_ROOT = "sub_column/";

    //位图索引
    public static String SELECT_BIRMAP_ROOT = "select_attribute_bitmap/";
    public static String MEASURE_BITMAP_ROOT = "measure_attribute_bitmap/";
    public static String BITMAP_PATH = "_bitmap_";

    public static String SUB_COLUMN_LENGTH = "sub_column_length";

    public static String SUB_PATH = "sub";

    public static String MY_PREFIX = "mysky";
    public static String LESS_PREFIX = "LESSsky";

    //less溢出文件
    public static String OVERFLOW_FILE = "overflow_file.tbl";

    public static String PREDICATE_PATH = "predicata_path_";

    public static String MEASURE_COLUNME_PATH = "measure_colume";
    public static String SELECT_COLUNME_PATH = "select_colume";

    public static String SORTED_MEASURE_COLUMN_PATH = "sorted_measure_colume";
    public static String TEMP_MEASURE_COLUMN_PATH = "temp_measure_column";

    //用位置索引替换属性值后的列
    public static String COLUMN_POSITION_PATH = "cpi_column";

    public static String extension = ".tbl";
    public static String intervel = "_";

    public static String MIN_POSITION_INDEX_PATH = "min_position_index.tbl";

    public static String INITIAL_SCAN_TABLE_PATH = "initial_scan_table.tbl";
    public static String SUB_SCAN_TABLE_PATH = "sub_scan_table";
    public static String SUB_SCAN_TABLE_LENGTH_PATH = "sub_scan_table_length.tbl";
    public static String SORTED_SCAN_TABLE = "sorted_scan_table.tbl";

    public static String SELECT_MIN_MAX_PATH = "select_min_max.tbl";

//    public static int TUPLE_NUMBER = 20000000;
    public static int TUPLE_NUMBER = 10000;
    public static int ATTRIBUTE_NUMBER = 20;
    public static int POSISION_INDEX = 1;
    public static int MPI_INDEX = 1;
    public static int SUB_INDEX = 1;

    //less
    public static int ENTROPY_SCORES_NUMBER = 1;

    public static int MEASURE_ATTRIBUTE_NUMBER = 10;
    public static int SELECT_ATTRIBUTE_NUMBER = 10;

    public static int ATTRIBUTE_LENGTH = 8;
    public static int COLUNME_BYTE_LENGTH = (Info.POSISION_INDEX + 1)
            * Info.ATTRIBUTE_LENGTH;

    public static int COLUMN_SUB_BYTE_LENGTH =
            (Info.POSISION_INDEX + 1 + Info.SUB_INDEX) * Info.ATTRIBUTE_LENGTH;

    public static int TUPLE_INITIAL_LENGTH = Info.ATTRIBUTE_NUMBER
            + Info.POSISION_INDEX;
    public static int TUPLE_LENGTH = Info.TUPLE_INITIAL_LENGTH + Info.MPI_INDEX;
    public static int TUPLE_SUB_LENGTH = Info.TUPLE_LENGTH + Info.SUB_INDEX;

    public static int TUPLE_INITIAL_BYTES_LENGTH =
            Info.TUPLE_INITIAL_LENGTH * Info.ATTRIBUTE_LENGTH;
    public static int TUPLE_BYTES_LENGTH =
            Info.TUPLE_LENGTH * Info.ATTRIBUTE_LENGTH;
    public static int TUPLE_SUB_BYTES_LENGTH =
            Info.TUPLE_SUB_LENGTH * Info.ATTRIBUTE_LENGTH;

    public static int DOMINATE_RELATED_ATTRIBUTE = 3;
    public static int RANGE_RELATED_ATTRIBUTE = 4;

    public static double selectivity = 0.9;

    public static int ALLOCATED_MEMEORY_SIZE = 256 * 1024 * 1024;

    public static int SELECT_BITMAP_NUM = 128;

    public static int BLOCK_SIZE = 4 * 1024;

    public static double LOGARITHMIC_BASE = 2.0;

    public static int DOMINATE_DETECTION_TUPLE_NUMBER = 4;

    //窗口大小,less
    public static int ENTROPY_ELIMINATION_WINDOW = 20;
    public static int SKYLINE_FILTER_WINDOW = 150;
    public static int BUFFERED_TUPLE_NUMBER = 20000;
}
