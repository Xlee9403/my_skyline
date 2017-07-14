package fileop;

import global.Info;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Arrays;

import datastructure.Column;
import datastructure.Compare_Column_Value;
import datastructure.Tuple;

public class tesr {
    //打印mpi文件前10个
    public void read_mpi() {
        try {
            BufferedInputStream reader = new BufferedInputStream(new FileInputStream(
                    Info.ROOT_PATH + Info.MIN_POSITION_INDEX_PATH));

            byte[] buf = new byte[Info.COLUNME_BYTE_LENGTH];

            int bytestoread = buf.length;
            int bytesread = 0;

            for (int i = 0; i < 30; i++) {
                while (bytesread < bytestoread)
                    bytesread += reader.read(buf, bytesread, bytestoread - bytesread);
                bytesread = 0;

                Column temp = new Column();
                temp.parse(buf);

                System.out.println(temp);

            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //打印某度量属性列前10个
    public void read_column() {
        try {
            BufferedInputStream reader = new BufferedInputStream(
                    new FileInputStream(Info.ROOT_PATH +
                            Info.MEASURE_COLUMN_ROOT +
                            Info.SUB_PATH + Info.COLUMN_POSITION_PATH
                            + 4 + Info.intervel + 0 + Info.extension));

            byte[] buf = new byte[Info.COLUMN_SUB_BYTE_LENGTH];

            int bytestoread = buf.length;
            int bytesread = 0;

            for (int i = 0; i < 10; i++) {
                while (bytesread < bytestoread)
                    bytesread += reader.read(buf, bytesread,
                            bytestoread - bytesread);
                bytesread = 0;

                Column temp = new Column();
                temp.parse_sub_column(buf);

                System.out.println(temp);

            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //打印原始表前10个
    public void read_table() {
        try {
            BufferedInputStream reader = new BufferedInputStream(new FileInputStream(
                    Info.ROOT_PATH + Info.GENERATE_TABLE));

            byte[] buf = new byte[Info.TUPLE_INITIAL_BYTES_LENGTH];

            int bytestoread = buf.length;
            int bytesread = 0;

            for (int i = 0; i < 10; i++) {
                while (bytesread < bytestoread)
                    bytesread += reader.read(buf, bytesread, bytestoread - bytesread);
                bytesread = 0;

                Tuple temp = new Tuple();
                temp.parse_initial(buf);

                System.out.println(temp);

            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //打印初始扫描表中MPI值错的
    public void read_scan_table() {
        try {
            BufferedInputStream reader = new BufferedInputStream(new FileInputStream(
                    Info.ROOT_PATH + Info.INITIAL_SCAN_TABLE_PATH));

            byte[] buf = new byte[Info.TUPLE_BYTES_LENGTH];

            int bytestoread = buf.length;
            int bytesread = 0;

            for (int i = 0; i < Info.TUPLE_NUMBER; i++) {
                while (bytesread < bytestoread)
                    bytesread += reader.read(buf, bytesread, bytestoread - bytesread);
                bytesread = 0;

                Tuple temp = new Tuple();
                temp.parse_tuple(buf);

                if (temp.MPI > Info.TUPLE_NUMBER) {
                    System.out.println(i + ":" + temp.MPI + ":" + Info.TUPLE_NUMBER);

                }

                if ((i > 890678) && (i < 900000)) {
                    System.out.print(temp);
                }

//				System.out.println(temp);

            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //打印有序的scan_table的前10个
    public void read_sorted_scan_table() {
        try {
            BufferedInputStream reader = new BufferedInputStream(new FileInputStream(
                    Info.ROOT_PATH + Info.SORTED_SCAN_TABLE));

            byte[] buf = new byte[Info.TUPLE_BYTES_LENGTH];

            int bytestoread = buf.length;
            int bytesread = 0;

            for (int i = 0; i < Info.TUPLE_NUMBER; i++) {
                while (bytesread < bytestoread)
                    bytesread += reader.read(buf, bytesread, bytestoread - bytesread);
                bytesread = 0;

                Tuple temp = new Tuple();
                temp.parse_tuple(buf);

                System.out.println(temp);

                if (temp.MPI > Info.TUPLE_NUMBER) {
                    System.out.println(i + ":" + temp.MPI + ":" + Info.TUPLE_NUMBER);

                }


            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //打印有序的scan_sub_table的前10个
    public void read_sorted_sub_table() {
        try {
            BufferedInputStream reader = new BufferedInputStream(new FileInputStream(
                    Info.ROOT_PATH + Info.SUB_SCAN_TABLE_PATH + 0 + Info.extension));

            byte[] buf = new byte[Info.TUPLE_SUB_BYTES_LENGTH];

            int bytestoread = buf.length;
            int bytesread = 0;

            for (int i = 0; i < 10000; i++) {
                while (bytesread < bytestoread)
                    bytesread += reader.read(buf, bytesread, bytestoread - bytesread);
                bytesread = 0;

                Tuple temp = new Tuple();
                temp.parse_sub_tuple(buf);

                if (i < 10) {
                    System.out.print(temp);
                }

            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void correct_mpi() {
        try {
            BufferedInputStream reader = new BufferedInputStream(new FileInputStream(
                    Info.ROOT_PATH + Info.MIN_POSITION_INDEX_PATH));

            byte[] buf = new byte[Info.COLUNME_BYTE_LENGTH];

            int bytestoread = buf.length;
            int bytesread = 0;

            Column[] arr = new Column[Info.TUPLE_NUMBER];
            for (int i = 0; i < arr.length; i++) {
                while (bytesread < bytestoread)
                    bytesread += reader.read(buf, bytesread, bytestoread - bytesread);
                bytesread = 0;

                Column temp = new Column();
                temp.parse(buf);

                arr[i] = new Column();
                arr[i].copyfrom(temp);
            }

            Compare_Column_Value ccv = new Compare_Column_Value();
            Arrays.sort(arr, ccv);

            for (int i = 0; i < 1000; i++) {
                System.out.print(arr[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void correct_sub_measure() {
        try {
            BufferedInputStream reader = new BufferedInputStream(new FileInputStream(
                    Info.ROOT_PATH + Info.MEASURE_COLUMN_ROOT +
                            Info.SUB_PATH + Info.MEASURE_COLUNME_PATH + 1 + "_" + 0 + Info.extension));

            byte[] buf = new byte[Info.COLUMN_SUB_BYTE_LENGTH];

            int bytestoread = buf.length;
            int bytesread = 0;

            for (int i = 0; i < Info.TUPLE_NUMBER; i++) {
                while (bytesread < bytestoread)
                    bytesread += reader.read(buf, bytesread, bytestoread - bytesread);
                bytesread = 0;

                Column temp = new Column();
                temp.parse_sub_column(buf);

                System.out.println(temp.sub_index);
            }


            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void read_select_bitmap() {
        try {
            BufferedInputStream reader = new BufferedInputStream(new FileInputStream(
                    Info.ROOT_PATH + Info.SELECT_BIRMAP_ROOT +
                            Info.SELECT_COLUNME_PATH + Info.BITMAP_PATH +
                            0 + Info.intervel + 3 + Info.extension));

            byte[] buf = new byte[2];
            int bytesToRead = buf.length;
            int bytesRead = 0;

            while (bytesRead < bytesToRead)
                bytesRead += reader.read(buf, bytesRead, bytesToRead - bytesRead);
            bytesRead = 0;

            System.out.println(buf[0]);


            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void read_measure_bitmap() {
        try {
//			BufferedInputStream reader = new BufferedInputStream(new FileInputStream(
//					Info.ROOT_PATH + Info.SORTED_SCAN_TABLE));
//			
//			byte[] buf = new byte[Info.TUPLE_BYTES_LENGTH];
//			int bytesToRead = buf.length;
//			int bytesRead = 0;
//			
//			for (int i = 0; i < Info.TUPLE_NUMBER; i++) 
//			{
//				while (bytesRead < bytesToRead) 			
//					bytesRead += reader.read(buf, bytesRead, bytesToRead - bytesRead);
//				bytesRead = 0;
//				
//				Tuple temp = new Tuple();
//				temp.parse_tuple(buf);
//				
//				
//				System.out.print(temp);
//			}

            BufferedInputStream[] measure_reader = new BufferedInputStream[4];
            for (int i = 0; i < measure_reader.length; i++) {
                measure_reader[i] = new BufferedInputStream(new FileInputStream(
                        Info.ROOT_PATH + Info.MEASURE_BITMAP_ROOT + Info.MEASURE_COLUNME_PATH
                                + Info.BITMAP_PATH + 0 + Info.intervel + (i + 1) + Info.extension));

            }

            byte[] bitbuf = new byte[2];
            int bitbuftoread = bitbuf.length;
            int bitbufread = 0;

            for (int i = 0; i < measure_reader.length; i++) {
                while (bitbufread < bitbuftoread) {
                    bitbufread += measure_reader[i].read(bitbuf,
                            bitbufread, bitbuftoread - bitbufread);
                }
                bitbufread = 0;

                for (int j = 0; j < bitbuf.length; j++) {
                    System.out.println(i + ":" + bitbuf[j]);
                }
            }

//			reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        tesr t = new tesr();

        //------验证合成大表是否正确-----//
//		t.read_mpi();
        t.read_column();
//		t.read_table();
//		t.read_scan_table();
        ////////---------------///////

//		t.read_sorted_sub_table();

//		t.read_sorted_scan_table();

//		t.correct_mpi();
//		t.correct_sub_measure();
//		t.read_select_bitmap();

//		t.read_measure_bitmap();

    }
}


