package fileop;

import global.Info;
import datastructure.Tuple;

import java.io.*;

public class Generate_Measure_Column {
    //生成列文件
    public void generate_column() {
        try {
            //读大表
            BufferedInputStream reader = new BufferedInputStream(new FileInputStream(
                    Info.ROOT_PATH + Info.GENERATE_TABLE));

            //写度量属性
            BufferedOutputStream[] writer_measure =
                    new BufferedOutputStream[Info.MEASURE_ATTRIBUTE_NUMBER];
            for (int i = 0; i < writer_measure.length; i++) {
                writer_measure[i] = new BufferedOutputStream(
                        new FileOutputStream(Info.ROOT_PATH + Info.MEASURE_COLUMN_ROOT +
                                Info.MEASURE_COLUNME_PATH + i + Info.extension));
            }


            byte[] buf = new byte[Info.TUPLE_INITIAL_BYTES_LENGTH];

            int bytetoread = buf.length;
            int byteread = 0;

            byte[] mybuf = new byte[Info.ATTRIBUTE_LENGTH];

            Tuple tp = new Tuple();

            System.out.println("生成列文件");

            for (int i = 0; i < Info.TUPLE_NUMBER; i++) {
                if (i % 1000000 == 0) {
                    System.out.println(i + ":" + Info.TUPLE_NUMBER);
                }
                while (byteread < bytetoread)
                    byteread += reader.read(buf, byteread, bytetoread - byteread);
                byteread = 0;

                tp.parse_initial(buf);

                Tuple tp_write = new Tuple();
                tp_write.copyfrom(tp);

                //写度量属性列
                for (int j = 0; j < Info.MEASURE_ATTRIBUTE_NUMBER; j++) {
                    mybuf[0] = (byte) (0xff & (tp_write.position_index >> 56));
                    mybuf[1] = (byte) (0xff & (tp_write.position_index >> 48));
                    mybuf[2] = (byte) (0xff & (tp_write.position_index >> 40));
                    mybuf[3] = (byte) (0xff & (tp_write.position_index >> 32));
                    mybuf[4] = (byte) (0xff & (tp_write.position_index >> 24));
                    mybuf[5] = (byte) (0xff & (tp_write.position_index >> 16));
                    mybuf[6] = (byte) (0xff & (tp_write.position_index >> 8));
                    mybuf[7] = (byte) (0xff & tp_write.position_index);

                    writer_measure[j].write(mybuf);

                    mybuf[0] = (byte) (0xff & (tp_write.measure_arr[j] >> 56));
                    mybuf[1] = (byte) (0xff & (tp_write.measure_arr[j] >> 48));
                    mybuf[2] = (byte) (0xff & (tp_write.measure_arr[j] >> 40));
                    mybuf[3] = (byte) (0xff & (tp_write.measure_arr[j] >> 32));
                    mybuf[4] = (byte) (0xff & (tp_write.measure_arr[j] >> 24));
                    mybuf[5] = (byte) (0xff & (tp_write.measure_arr[j] >> 16));
                    mybuf[6] = (byte) (0xff & (tp_write.measure_arr[j] >> 8));
                    mybuf[7] = (byte) (0xff & tp_write.measure_arr[j]);

                    writer_measure[j].write(mybuf);
                }

            }

            for (int i = 0; i < Info.MEASURE_ATTRIBUTE_NUMBER; i++) {
                writer_measure[i].flush();
                writer_measure[i].close();
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
