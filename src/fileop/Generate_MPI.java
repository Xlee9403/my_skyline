package fileop;

import global.Info;

import java.io.*;
import java.util.Arrays;

import datastructure.Column;
import datastructure.Compare_Column_Value;
import datastructure.Reverse_Parse;

public class Generate_MPI {
    public void generete_min_pi() {
        try {
            //读列文件缓冲区
            BufferedInputStream[] reader =
                    new BufferedInputStream[Info.MEASURE_ATTRIBUTE_NUMBER];
            for (int i = 0; i < reader.length; i++)
                reader[i] = new BufferedInputStream(new FileInputStream(
                        Info.ROOT_PATH + Info.MEASURE_COLUMN_ROOT
                                + Info.SORTED_MEASURE_COLUMN_PATH + i + Info.extension));

            //写MPI缓冲区
            BufferedOutputStream writer =
                    new BufferedOutputStream(new FileOutputStream(
                            Info.ROOT_PATH + Info.MIN_POSITION_INDEX_PATH));

            byte[] buf = new byte[Info.COLUNME_BYTE_LENGTH];

            int bytestoread = buf.length;
            int bytesread = 0;

            //存放每个列文件读出的，当前元组的各个度量属性值
            Column[] arr = new Column[Info.MEASURE_ATTRIBUTE_NUMBER];
            for (int i = 0; i < arr.length; i++)
                arr[i] = new Column();

            System.out.println("计算MPI值：");
            for (int i = 0; i < Info.TUPLE_NUMBER; i++) {
                if (i % 1000000 == 0)
                    System.out.println(i + ":" + Info.TUPLE_NUMBER);

                for (int j = 0; j < arr.length; j++) {
                    while (bytesread < bytestoread)
                        bytesread += reader[j].read(buf, bytesread, bytestoread - bytesread);
                    bytesread = 0;

                    Column temp = new Column();
                    temp.parse(buf);

                    arr[j].copyfrom(temp);
                }

                //按列索引排序
                Compare_Column_Value ccv = new Compare_Column_Value();
                Arrays.sort(arr, ccv);

                byte[] write_buf = new byte[Info.COLUNME_BYTE_LENGTH];

                //将找到的MPI值和原表索引写回
                Reverse_Parse rp = new Reverse_Parse();
                write_buf = rp.rev_parse_colume(arr[0]);
                writer.write(write_buf);
            }

            writer.flush();
            writer.close();

            for (int i = 0; i < reader.length; i++)
                reader[i].close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
