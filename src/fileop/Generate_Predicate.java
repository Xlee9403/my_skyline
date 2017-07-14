package fileop;

import global.Info;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;

import datastructure.Interval;

public class Generate_Predicate {
    public void generate() {
        try {
            //每列的选择度
            double delta = Math.pow(Info.selectivity, 1.0 / Info.RANGE_RELATED_ATTRIBUTE);

            System.out.println(delta);

            BufferedWriter writer = new BufferedWriter(new FileWriter(
                    Info.ROOT_PATH + Info.PREDICATE_ROOT + Info.PREDICATE_PATH + Info.TUPLE_NUMBER
                            + "_" + Info.selectivity + Info.extension));

            Random rnd = new Random();

            long delta_length = (long) (Long.MAX_VALUE * delta);

            for (int i = 0; i < Info.RANGE_RELATED_ATTRIBUTE; i++) {
                long start = Math.abs(rnd.nextLong() % (Long.MAX_VALUE - delta_length));
                long end = start + delta_length;

                System.out.println(start + ":" + end);

                writer.write(start + ":" + end + "\r\n");
            }
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    ///////////==================获取选择属性的上下界==============//////////////////

    public Interval[] get_bound() {
        Interval[] predicate = new Interval[Info.RANGE_RELATED_ATTRIBUTE];
        try {
            BufferedReader reader = new BufferedReader(new FileReader(
                    Info.ROOT_PATH + Info.PREDICATE_ROOT + Info.PREDICATE_PATH + Info.TUPLE_NUMBER
                            + "_" + Info.selectivity + Info.extension));

            String arr = new String();

            for (int i = 0; i < predicate.length; i++) {
                arr = reader.readLine();

                String[] ar = arr.split(":");

                predicate[i] = new Interval();
                predicate[i].lowerbound = Long.parseLong(ar[0]);
                predicate[i].upperbound = Long.parseLong(ar[1]);

                //System.out.println(predicate[i].lowerbound + ":" + predicate[i].upperbound);

            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return predicate;
    }

    public static void main(String[] args) {
        Generate_Predicate gi = new Generate_Predicate();
        gi.generate();
    }
}
