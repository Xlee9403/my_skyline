package theocomp;

import global.Info;

public class Cardinality {
    public void compute_cardinality() {
        double gamma = 0.57721;

        double cardinality;

        double range_tuple_num = (double) Info.TUPLE_NUMBER * Info.selectivity;

        cardinality = Math.abs(Math.pow(
                (Math.log(range_tuple_num) + gamma),
                Info.DOMINATE_RELATED_ATTRIBUTE - 1));

        double compute_k = 1;

        for (int i = 1; i <= Info.DOMINATE_RELATED_ATTRIBUTE - 1; i++)
            compute_k *= (double) i;

        cardinality = cardinality / compute_k;

        System.out.println("skyline元组的理论值为：");
        System.out.println((long) cardinality);
    }

    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();

        Cardinality cdl = new Cardinality();
        cdl.compute_cardinality();


        long endTime = System.currentTimeMillis();

        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
    }

}
