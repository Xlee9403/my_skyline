package theocomp;

import global.Info;

public class Scandepth_earlytermination {
    public void compute_scandepth() {
        //满足条件的元组个数
        double range_tuple_number = Info.TUPLE_NUMBER * Info.selectivity;

        double a = 1.0 / range_tuple_number;
        double b = 1.0 / Info.DOMINATE_RELATED_ATTRIBUTE;

//		System.out.println("a=" + a);
//		System.out.println("b=" + b);
//		System.out.println(Math.pow(a, b));

        //每列的扫描深度
        double gdep = range_tuple_number * Math.pow(a, b);

//		System.out.println("gdep: " + gdep);

        //整个表内不用扫描的元组数的概率
//		double non_scan = Math.pow((1-gdep/range_tuple_number)
//				,Info.DOMINATE_RELATED_ATTRIBUTE);

        //符合选择条件的元组扫描深度
//		double range_depth = range_tuple_number*(1 - non_scan);

        double range_depth = Info.MEASURE_ATTRIBUTE_NUMBER * gdep;

        //整个表的扫描深度
        double depth = range_depth / Info.selectivity;

        System.out.println("理论的扫描深度为：");

        System.out.println(depth);
    }

    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();

        Scandepth_earlytermination sde = new Scandepth_earlytermination();
        sde.compute_scandepth();

        long endTime = System.currentTimeMillis();

        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
    }
}
