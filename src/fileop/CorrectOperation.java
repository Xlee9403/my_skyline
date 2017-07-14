package fileop;

import java.io.*;
import java.util.*;

import datastructure.Column;
import datastructure.Select_Min_Max_Value;
import datastructure.Tuple;
import global.*;

public class CorrectOperation {
    //判断选择属性位图的正确性
    public void correct_range_bitmap_sorted_list() {
        Select_Min_Max_Value smmv = new Select_Min_Max_Value();
        Get_Select_Mm_Value gsmmv = new Get_Select_Mm_Value();
        smmv = gsmmv.get_Mm_value();
        try {
            //读位图
            BufferedInputStream[][] select_bit_reader = new BufferedInputStream
                    [Info.SELECT_ATTRIBUTE_NUMBER][Info.SELECT_BITMAP_NUM];
            for (int i = 0; i < Info.SELECT_ATTRIBUTE_NUMBER; i++) {
                for (int j = 0; j < Info.SELECT_BITMAP_NUM; j++) {
                    select_bit_reader[i][j] = new BufferedInputStream(
                            new FileInputStream(Info.ROOT_PATH +
                                    Info.SELECT_BIRMAP_ROOT + Info.SELECT_COLUNME_PATH
                                    + Info.BITMAP_PATH + i + Info.intervel
                                    + j + Info.extension));
                }
            }

            byte[] bit_buf;
            if (Info.TUPLE_NUMBER % 8 == 0)
                bit_buf = new byte[Info.TUPLE_NUMBER / 8];
            else
                bit_buf = new byte[Info.TUPLE_NUMBER / 8 + 1];

            int bitsToRead = bit_buf.length;
            int bitsRead = 0;

            boolean flag = true;
            //存放某属性的所有位图
            byte[][] bitmap = new byte[Info.SELECT_BITMAP_NUM][bit_buf.length];

            for (int i = 0; i < Info.SELECT_ATTRIBUTE_NUMBER; i++) {
                //-------------读出某个属性的所有位图，放在bitmap里------------------//
                for (int j = 0; j < Info.SELECT_BITMAP_NUM; j++) {
                    while (bitsRead < bitsToRead)
                        bitsRead += select_bit_reader[i][j].read(bit_buf,
                                bitsRead, bitsToRead - bitsRead);
                    bitsRead = 0;

                    for (int k = 0; k < bit_buf.length; k++) {
                        bitmap[j][k] = bit_buf[k];
                    }
                }
                ////////////////////-----------------------------/////////////////
                //读已排序的大表,读Info.SELECT_ATTRIBUTE_NUMBER遍
                BufferedInputStream pt_reader = new BufferedInputStream(
                        new FileInputStream(Info.ROOT_PATH + Info.SORTED_SCAN_TABLE));

                byte[] buf = new byte[Info.TUPLE_BYTES_LENGTH];
                int bytesToRead = buf.length;
                int byteRead = 0;

                for (int j = 0; j < Info.TUPLE_NUMBER; j++) {
                    if (i % 100000 == 0)
                        System.out.println(j + ":" + i + ":" + Info.TUPLE_NUMBER);

                    while (byteRead < bytesToRead)
                        byteRead += pt_reader.read(buf, byteRead, bytesToRead - byteRead);
                    byteRead = 0;

                    Tuple temp = new Tuple();
                    temp.parse_tuple(buf);

                    int bit_index = (int) ((temp.select_arr[i]
                            - smmv.min_value) / smmv.number_per_range);

                    flag = true;

                    for (int k = 0; k < Info.SELECT_BITMAP_NUM; k++) {
                        if (k < bit_index) {
                            if ((bitmap[k][j / 8] & (1 << (8 - 1 - j % 8))) != 0)
                                flag = false;

                            if (flag == false) {
                                System.out.println(bitmap[k][i / 8]);
                                System.out.println("0写成了非零" + ":" + j + ":"
                                        + k + ":" + (j / 8));
                                System.out.print(temp);
                                break;
                            }


                        } else {
                            if ((bitmap[k][j / 8] & (1 << (8 - 1 - j % 8))) == 0)
                                flag = false;

                            if (flag == false) {
                                System.out.println("非0写成了零" + ":" + j + ":"
                                        + k + ":" + (j / 8));
                                System.out.print(temp);
                                break;
                            }

                        }
                    }
                    if (flag == false) {
                        System.out.println("something has wrong");
                        break;
                    }

                }
                pt_reader.close();

                if (flag == false) {
                    System.out.println("something has wrong");
                    break;
                }
            }
            if (flag)
                System.out.println("nothing has wrong");


            for (int i = 0; i < Info.SELECT_ATTRIBUTE_NUMBER; i++) {
                for (int j = 0; j < Info.SELECT_BITMAP_NUM; j++) {
                    select_bit_reader[i][j].close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //判断度量属性位图的正确性
    public void correct_measure_bitmap() {
        try {
//			//每列位图的总数量，自下而上建立位图，只需取整
//			int bitmap_count = (int)(Math.log((double)Info.TUPLE_NUMBER)/
//					Math.log(Info.LOGARITHMIC_BASE));

            //-----------------每列位图的总数量，自顶向下建立位图-------------------//
            int bitmap_count;
            if ((Math.log((double) Info.TUPLE_NUMBER) /
                    Math.log(Info.LOGARITHMIC_BASE)) == 0) {
                bitmap_count = (int) (Math.log((double) Info.TUPLE_NUMBER) /
                        Math.log(Info.LOGARITHMIC_BASE));
            } else
                bitmap_count = (int) (Math.log((double) Info.TUPLE_NUMBER) /
                        Math.log(Info.LOGARITHMIC_BASE)) + 1;
            /////////////////----------------------------------///////////////

            //读位图
            BufferedInputStream[][] measure_bit_reader = new BufferedInputStream
                    [Info.MEASURE_ATTRIBUTE_NUMBER][bitmap_count];
            for (int i = 0; i < Info.MEASURE_ATTRIBUTE_NUMBER; i++) {
                for (int j = 0; j < bitmap_count; j++) {
                    measure_bit_reader[i][j] = new BufferedInputStream(
                            new FileInputStream(Info.ROOT_PATH +
                                    Info.MEASURE_BITMAP_ROOT + Info.MEASURE_COLUNME_PATH
                                    + Info.BITMAP_PATH + i + Info.intervel
                                    + j + Info.extension));
                }
            }

            byte[] bit_buf;
            if (Info.TUPLE_NUMBER % 8 == 0)
                bit_buf = new byte[Info.TUPLE_NUMBER / 8];
            else
                bit_buf = new byte[Info.TUPLE_NUMBER / 8 + 1];

            int bitsToRead = bit_buf.length;
            int bitsRead = 0;

            boolean flag = true;
            //存放某属性的所有位图
            byte[][] bitmap = new byte[bitmap_count][bit_buf.length];

            for (int i = 0; i < Info.MEASURE_ATTRIBUTE_NUMBER; i++) {
                //-------------读出某个属性的所有位图，放在bitmap里------------------//
                for (int j = 0; j < bitmap_count; j++) {
                    while (bitsRead < bitsToRead)
                        bitsRead += measure_bit_reader[i][j].read(bit_buf,
                                bitsRead, bitsToRead - bitsRead);
                    bitsRead = 0;

                    for (int k = 0; k < bit_buf.length; k++) {
                        bitmap[j][k] = bit_buf[k];
                    }
                }
                ////////////////////-----------------------------/////////////////
                //读已排序的大表,读Info.SELECT_ATTRIBUTE_NUMBER遍
                BufferedInputStream pt_reader = new BufferedInputStream(
                        new FileInputStream(Info.ROOT_PATH + Info.SORTED_SCAN_TABLE));

                byte[] buf = new byte[Info.TUPLE_BYTES_LENGTH];
                int bytesToRead = buf.length;
                int byteRead = 0;

                for (int j = 0; j < Info.TUPLE_NUMBER; j++) {
                    if (j % 100000 == 0)
                        System.out.println(j + ":" + i + ":" + Info.TUPLE_NUMBER);

//					System.out.println("===============");

                    while (byteRead < bytesToRead)
                        byteRead += pt_reader.read(buf, byteRead, bytesToRead - byteRead);
                    byteRead = 0;

                    Tuple temp = new Tuple();
                    temp.parse_tuple(buf);

                    int bit_index = (int) (temp.measure_arr[i]);

                    flag = true;

                    for (int k = 0; k < bitmap_count; k++) {
                        if (bit_index > Math.pow(Info.LOGARITHMIC_BASE, k + 1)) {
                            if ((bitmap[k][j / 8] & (1 << (8 - 1 - j % 8))) != 0)
                                flag = false;

                            if (flag == false) {
                                System.out.println(bitmap[k][i / 8]);
                                System.out.println("0写成了非零" + ":" + j + ":"
                                        + k + ":" + (j / 8));
                                System.out.print(temp);
                                break;
                            }


                        } else {
                            if ((bitmap[k][j / 8] & (1 << (8 - 1 - j % 8))) == 0)
                                flag = false;

                            if (flag == false) {
                                System.out.println("非0写成了零" + ":" + j + ":"
                                        + k + ":" + (j / 8));
                                System.out.print(temp);
                                System.out.println();
                                break;
                            }

                        }
                    }
                    if (flag == false) {
                        System.out.println("something has wrong");
                        break;
                    }

                }
                pt_reader.close();

                if (flag == false) {
                    System.out.println("something has wrong");
                    break;
                }
            }
            if (flag)
                System.out.println("nothing has wrong");


            for (int i = 0; i < Info.MEASURE_ATTRIBUTE_NUMBER; i++) {
                for (int j = 0; j < bitmap_count; j++) {
                    measure_bit_reader[i][j].close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //判断排序大表是否有误
    public void process_sorted_list_and_pt() {
        try {
            boolean flag = true;
            Random rnd = new Random();

            BufferedInputStream pt_reader = new BufferedInputStream(
                    new FileInputStream(Info.ROOT_PATH + Info.SORTED_SCAN_TABLE));

            Tuple tup = new Tuple();
            byte[] buf = new byte[Info.TUPLE_BYTES_LENGTH];
            int bytesRead = 0;
            int bytesToRead = buf.length;

            RandomAccessFile[] column_reader = new RandomAccessFile[Info.MEASURE_ATTRIBUTE_NUMBER];
            for (int k = 0; k < column_reader.length; k++) {
                column_reader[k] = new RandomAccessFile(
                        Info.ROOT_PATH + Info.MEASURE_COLUMN_ROOT
                                + Info.SORTED_MEASURE_COLUMN_PATH
                                + k + Info.extension, "r");
            }

            Column col = new Column();
            byte[] col_buf = new byte[Info.COLUNME_BYTE_LENGTH];
            int colToread = col_buf.length;
            int colRead = 0;

            RandomAccessFile original_table_raf = new RandomAccessFile(
                    Info.ROOT_PATH + Info.GENERATE_TABLE, "r");
            Tuple original_row = new Tuple();

            byte[] original_row_buf = new byte[Info.TUPLE_INITIAL_BYTES_LENGTH];
            int orbToRead = Info.TUPLE_INITIAL_BYTES_LENGTH;
            int orbRead = 0;

            for (long i = 0; i < Info.TUPLE_NUMBER; i++) {
                while (bytesRead < bytesToRead)
                    bytesRead += pt_reader.read(buf, bytesRead,
                            bytesToRead - bytesRead);

                bytesRead = 0;

                tup.parse_tuple(buf);

//				//-----------------------验证MPI值---------------------------//
                if (tup.MPI > Info.TUPLE_NUMBER)
                    System.out.println(tup.MPI + ":" + Long.MAX_VALUE);

                //////////////////////////////////////////
                if (rnd.nextDouble() >= 0.001)
                    continue;
                //////////////////////////////////////////

                long mpi = tup.MPI;

                int count = 0;

                for (int j = 0; j < tup.measure_arr.length; j++) {
                    if (mpi > tup.measure_arr[j]) {
                        flag = false;
                        System.out.println("greatness is wrong");

                        System.out.println(i + ":" + mpi + ":" + tup.measure_arr[j]);

                        pt_reader.close();

                        return;
                    }

                    if (mpi == tup.measure_arr[j])
                        count += 1;
                }

//				System.out.println(i + ":" + tup.position_index + ":" + count);

                if (count != 1) {
                    flag = false;
                    System.out.println("count is wrong");

                }
                ///////////////------------------------------////////////////////

                //---------------------验证pi值---------------------//
//				BufferedInputStream[] column_reader = new BufferedInputStream[Info.MEASURE_ATTRIBUTE_NUMBER];
//				for (int k = 0; k < column_reader.length; k++) 
//				{
//					column_reader[k] = new BufferedInputStream(new FileInputStream(
//							Info.ROOT_PATH + Info.MEASURE_COLUNME_PATH + k + Info.extension));
//				}
//				
//				Column col = new Column();
//				byte[] col_buf = new byte[Info.COLUNME_BYTE_LENGTH];
//				int colToread = col_buf.length;
//				int colRead = 0;	

                if (rnd.nextDouble() >= 0.0001)
                    continue;

                long pi_t = tup.position_index;

                for (int j = 0; j < column_reader.length; j++) {
                    column_reader[j].seek(pi_t * Info.COLUNME_BYTE_LENGTH);

                    while (colRead < colToread)
                        colRead += column_reader[j].read(col_buf, colRead,
                                colToread - colRead);
                    colRead = 0;

                    col.parse(col_buf);

                    if (tup.measure_arr[j] != col.column_value) {
                        System.out.println("PI_column has something wrong.");

                        pt_reader.close();

                        for (int k = 0; k < column_reader.length; k++)
                            column_reader[k].close();

                        return;
                    }
                }

                original_table_raf.seek(pi_t * Info.TUPLE_INITIAL_BYTES_LENGTH);

                while (orbRead < orbToRead)
                    orbRead += original_table_raf.read(original_row_buf,
                            orbRead, orbToRead - orbRead);
                orbRead = 0;

                original_row.parse_initial(original_row_buf);

                for (int j = 0; j < Info.SELECT_ATTRIBUTE_NUMBER; j++) {
                    if (tup.select_arr[j] != original_row.select_arr[j]) {
                        System.out.println("selection attribute has something wrong.");

                        pt_reader.close();

                        original_table_raf.close();

                        return;
                    }
                }
                //////////////----------------------------------------------////////////////////////
                if (flag) {
                    System.out.println(i + ":" + "nothing has problem");
                }

            }
            if (flag) {
                System.out.println("nothing has problem");
            }
            pt_reader.close();

            original_table_raf.close();

            for (int k = 0; k < column_reader.length; k++)
                column_reader[k].close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        // TODO Auto-generated method stub
        CorrectOperation co_app = new CorrectOperation();

        co_app.correct_measure_bitmap();

//		co_app.process_sorted_list_and_pt();

//		co_app.correct_range_bitmap_sorted_list();
    }

}
