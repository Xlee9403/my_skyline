package fileop;

import global.Info;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Merge_Scan_Table {
    //读MPI表，度量属性列，大表，合并为scantable
    public void merge_table() {
        try {
            //读mpi缓冲区
            BufferedInputStream mpi_reader = new BufferedInputStream(
                    new FileInputStream(Info.ROOT_PATH +
                            Info.MIN_POSITION_INDEX_PATH));

            //读度量属性列文件缓冲区
            BufferedInputStream[] measure_reader =
                    new BufferedInputStream[Info.MEASURE_ATTRIBUTE_NUMBER];
            for (int i = 0; i < measure_reader.length; i++)
                measure_reader[i] = new BufferedInputStream(
                        new FileInputStream(Info.ROOT_PATH
                                + Info.MEASURE_COLUMN_ROOT +
                                Info.SORTED_MEASURE_COLUMN_PATH + i + Info.extension));

            //mpi和度量属性列共用一个byte[]
            byte[] column_buf = new byte[Info.COLUNME_BYTE_LENGTH];

            int columntoread = column_buf.length;
            int columnread = 0;

            //读原表缓冲区
            BufferedInputStream table_reader = new BufferedInputStream(
                    new FileInputStream(Info.ROOT_PATH + Info.GENERATE_TABLE));

            byte[] table_buf = new byte[Info.TUPLE_INITIAL_BYTES_LENGTH];

            int tabletoread = table_buf.length;
            int tableread = 0;

            //写scan_table缓冲区
            BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(
                    Info.ROOT_PATH + Info.INITIAL_SCAN_TABLE_PATH));

            System.out.println("合并形成大表：");

            for (int i = 0; i < Info.TUPLE_NUMBER; i++) {
                if (i % 1000000 == 0)
                    System.out.println(i + ":" + Info.TUPLE_NUMBER);

                //读MPI，将table_position和mpi值写入
                while (columnread < columntoread)
                    columnread += mpi_reader.read(
                            column_buf, columnread, columntoread - columnread);
                columnread = 0;

                writer.write(column_buf);

                //读度量属性列,将column_value写入文件
                for (int j = 0; j < Info.MEASURE_ATTRIBUTE_NUMBER; j++) {
                    while (columnread < columntoread)
                        columnread += measure_reader[j].read(
                                column_buf, columnread, columntoread - columnread);
                    columnread = 0;

                    byte[] measure_attribute = new byte[Info.ATTRIBUTE_LENGTH];
                    for (int k = 0; k < measure_attribute.length; k++)
                        measure_attribute[k] = column_buf[Info.ATTRIBUTE_LENGTH + k];

                    writer.write(measure_attribute);
                }

                /////-----------------------------------//////////////////////
                //读大表，将选择属性写入大表
                while (tableread < tabletoread)
                    tableread += table_reader.read(
                            table_buf, tableread, tabletoread - tableread);
                tableread = 0;

                //存放选择属性的byte[]
                byte[] select = new byte[Info.SELECT_ATTRIBUTE_NUMBER * Info.ATTRIBUTE_LENGTH];

                //选择属性的起始byte位
                int start = (Info.POSISION_INDEX + Info.MEASURE_ATTRIBUTE_NUMBER)
                        * Info.ATTRIBUTE_LENGTH;

                for (int j = 0; j < select.length; j++)
                    select[j] = table_buf[start + j];

                writer.write(select);
                /////-----------------------------------///////////////////////
            }

            writer.flush();
            writer.close();

            mpi_reader.close();

            for (int i = 0; i < Info.MEASURE_ATTRIBUTE_NUMBER; i++)
                measure_reader[i].close();

            table_reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
