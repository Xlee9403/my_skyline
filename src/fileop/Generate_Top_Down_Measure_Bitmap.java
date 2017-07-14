package fileop;

import global.Info;

import java.io.*;

import datastructure.BMBuffer;
import datastructure.Tuple;

public class Generate_Top_Down_Measure_Bitmap {
    //自顶向下建立位图
    public void generate_bitmap() {
        try {
            //------------------------读已排序表-----------------------------//
            BufferedInputStream pt_reader = new BufferedInputStream(new
                    FileInputStream(Info.ROOT_PATH + Info.SORTED_SCAN_TABLE));

            byte[] tuple_buf = new byte[Info.TUPLE_BYTES_LENGTH];
            int tupleToRead = tuple_buf.length;
            int tupleRead = 0;
            /////////////------------------------------------------/////////

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

            //-------------------------写位图------------------------------//
            BufferedOutputStream[][] bit_writer = new BufferedOutputStream
                    [Info.MEASURE_ATTRIBUTE_NUMBER][bitmap_count];

            for (int i = 0; i < Info.MEASURE_ATTRIBUTE_NUMBER; i++) {
                for (int j = 0; j < bitmap_count; j++) {
                    bit_writer[i][j] = new BufferedOutputStream(
                            new FileOutputStream(Info.ROOT_PATH +
                                    Info.MEASURE_BITMAP_ROOT + Info.MEASURE_COLUNME_PATH
                                    + Info.BITMAP_PATH + i + Info.intervel
                                    + j + Info.extension));
                }
            }
            //每块包含的元组数
            int tuple_num_per_block = Info.BLOCK_SIZE * 8;
            ////////////////-------------------------------------////////////////

            //未读取的元组数
            int unread_tuple_number = Info.TUPLE_NUMBER;

            //已读取的块数
            int block_count = 0;

            //最后一个位图的byte数
            int byte_count = 0;

            while (unread_tuple_number != 0) {
                //位图 缓冲区
                BMBuffer[][] bitmap = new BMBuffer
                        [Info.MEASURE_ATTRIBUTE_NUMBER][bitmap_count];
                for (int i = 0; i < Info.MEASURE_ATTRIBUTE_NUMBER; i++) {
                    for (int j = 0; j < bitmap_count; j++) {
                        bitmap[i][j] = new BMBuffer();
                        for (int k = 0; k < bitmap[i][j].buffew.length; k++) {
                            bitmap[i][j].buffew[k] = 0;
                        }
                    }
                }

                System.out.println(block_count + ":" + unread_tuple_number
                        + ":" + Info.TUPLE_NUMBER);

                block_count++;

                //读一块包含的元组数
                for (int i = 0; i < tuple_num_per_block; i++) {
                    //读出一个元组并解析
                    while (tupleRead < tupleToRead)
                        tupleRead += pt_reader.read(tuple_buf,
                                tupleRead, tupleToRead - tupleRead);
                    tupleRead = 0;

                    Tuple temp = new Tuple();
                    temp.parse_tuple(tuple_buf);

                    unread_tuple_number--;

                    for (int j = 0; j < Info.MEASURE_ATTRIBUTE_NUMBER; j++) {
                        int temp_index = (int) (temp.measure_arr[j]);

                        for (int k = 0; k < bitmap_count; k++) {
                            if (temp_index <= Math.pow(Info.LOGARITHMIC_BASE, k + 1)) {
                                bitmap[j][k].buffew[i / 8]
                                        |= 1 << (8 - 1 - i % 8);
                            }

                        }
                    }

                    if (unread_tuple_number == 0) {
                        byte_count = (int) Math.ceil(1.0 * i / Info.ATTRIBUTE_LENGTH);
                        break;
                    }
                }
                //写入
                if (unread_tuple_number != 0) {
                    for (int i = 0; i < Info.MEASURE_ATTRIBUTE_NUMBER; i++) {
                        for (int j = 0; j < bitmap_count; j++) {
                            bit_writer[i][j].write(bitmap[i][j].buffew);
                        }
                    }
                } else {
                    System.out.println(block_count);

                    for (int i = 0; i < Info.MEASURE_ATTRIBUTE_NUMBER; i++) {
                        for (int j = 0; j < bitmap_count; j++) {
                            bit_writer[i][j].write(bitmap[i][j].buffew, 0, byte_count);
                        }
                    }
                }

            }

            pt_reader.close();

            for (int i = 0; i < Info.MEASURE_ATTRIBUTE_NUMBER; i++) {
                for (int j = 0; j < bitmap_count; j++) {
                    bit_writer[i][j].flush();
                    bit_writer[i][j].close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Generate_Top_Down_Measure_Bitmap gmea
                = new Generate_Top_Down_Measure_Bitmap();

        gmea.generate_bitmap();

    }
}
