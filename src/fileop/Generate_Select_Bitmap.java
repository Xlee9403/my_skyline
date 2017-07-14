package fileop;

import global.Info;

import java.io.*;

import datastructure.BMBuffer;
import datastructure.Select_Min_Max_Value;
import datastructure.Tuple;

public class Generate_Select_Bitmap {
    //生成选择属性位图
    public void generate_sel_bitmap() {
        //-----------------先获取select的最大值和最小值----------------------------//

        Select_Min_Max_Value smmv = new Select_Min_Max_Value();
        Get_Select_Mm_Value gsmv = new Get_Select_Mm_Value();

        smmv = gsmv.get_Mm_value();

        /////////////////////---------------------------------//////////////////

        try {
            //------------------------读已排序表-----------------------------//
            BufferedInputStream pt_reader = new BufferedInputStream(new
                    FileInputStream(Info.ROOT_PATH + Info.SORTED_SCAN_TABLE));

            byte[] tuple_buf = new byte[Info.TUPLE_BYTES_LENGTH];
            int tupleToRead = tuple_buf.length;
            int tupleRead = 0;
            /////////////------------------------------------------/////////

            //-------------------------写位图------------------------------//
            BufferedOutputStream[][] bit_writer = new BufferedOutputStream
                    [Info.SELECT_ATTRIBUTE_NUMBER][Info.SELECT_BITMAP_NUM];

            for (int i = 0; i < Info.SELECT_ATTRIBUTE_NUMBER; i++) {
                for (int j = 0; j < Info.SELECT_BITMAP_NUM; j++) {
                    bit_writer[i][j] = new BufferedOutputStream(
                            new FileOutputStream(Info.ROOT_PATH +
                                    Info.SELECT_BIRMAP_ROOT +
                                    Info.SELECT_COLUNME_PATH + Info.BITMAP_PATH +
                                    i + Info.intervel + j + Info.extension));
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
                        [Info.SELECT_ATTRIBUTE_NUMBER][Info.SELECT_BITMAP_NUM];
                for (int i = 0; i < Info.SELECT_ATTRIBUTE_NUMBER; i++) {
                    for (int j = 0; j < Info.SELECT_BITMAP_NUM; j++) {
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

                    for (int j = 0; j < Info.SELECT_ATTRIBUTE_NUMBER; j++) {
                        int bit_start = (int) ((temp.select_arr[j]
                                - smmv.min_value) / smmv.number_per_range);

                        for (int k = bit_start; k < Info.SELECT_BITMAP_NUM; k++) {
                            bitmap[j][k].buffew[i / 8] |= 1 << (8 - 1 - i % 8);
                        }
                    }

                    if (unread_tuple_number == 0) {
                        byte_count = (int) Math.ceil(1.0 * i / Info.ATTRIBUTE_LENGTH);
                        break;
                    }

                }

                //写入
                if (unread_tuple_number != 0) {
                    for (int i = 0; i < Info.SELECT_ATTRIBUTE_NUMBER; i++) {
                        for (int j = 0; j < Info.SELECT_BITMAP_NUM; j++) {
                            bit_writer[i][j].write(bitmap[i][j].buffew);
                        }
                    }
                } else {
                    System.out.println(block_count);

                    for (int i = 0; i < Info.SELECT_ATTRIBUTE_NUMBER; i++) {
                        for (int j = 0; j < Info.SELECT_BITMAP_NUM; j++) {
                            bit_writer[i][j].write(bitmap[i][j].buffew, 0, byte_count);
                        }
                    }
                }

            }

            pt_reader.close();

            for (int i = 0; i < Info.SELECT_ATTRIBUTE_NUMBER; i++) {
                for (int j = 0; j < Info.SELECT_BITMAP_NUM; j++) {
                    bit_writer[i][j].flush();
                    bit_writer[i][j].close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Generate_Select_Bitmap gsb = new Generate_Select_Bitmap();

        gsb.generate_sel_bitmap();

    }

}
