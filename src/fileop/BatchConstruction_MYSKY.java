package fileop;

import algo.Base_MYSKY;
import algo.LESS;

public class BatchConstruction_MYSKY {
    public void common_operate() {
        //生成初始表
        System.out.println("=============生成初始表============");
        Generate_Initial_Table git = new Generate_Initial_Table();
        git.generate_table();

        //获取选择属性的最大最小值
        System.out.println("=============获取选择属性的最值============");
        Get_Select_Mm_Value gsmv = new Get_Select_Mm_Value();
        gsmv.read_to_get_select_Mm_value();

        //生成predicate
        System.out.println("=============生成选择谓词============");
        Generate_Predicate gi = new Generate_Predicate();
        gi.generate();
    }

    public void MySkyline_operate() {
//		//扫描大表，生成度量属性的列文件
//		System.out.println("=============生成度量属性列============");
//		Generate_Measure_Column gmc = new Generate_Measure_Column();
//		gmc.generate_column();

////		Sort_Column sc = new Sort_Column();
////		sc.sort_mesaure_value();

//		//--------------------------列文件排序-------------------------------//
//		//先按属性值排序后，
//		//用当前位置索引代替属性值，按原表索引排序后写回
//		System.out.println("=============对列文件排序============");
//		Sort_Measure_Column smc = new Sort_Measure_Column();		
//		int sub_table_number = smc.is_division();			
//		if (sub_table_number == 1) 		
//			smc.sort_mesaure_value();		
//		else 
//		{
//			int[] sub_tuple_number_arr = 
//					smc.sub_table_lenth(sub_table_number);
//			
//			smc.generate_sub_table(sub_table_number,sub_tuple_number_arr);
//			smc.merge_sub_table(sub_table_number,sub_tuple_number_arr);
//			
//			Sort_Column_PI scp = new Sort_Column_PI();			
//			int sub_table_num = scp.is_division();				
//			if (sub_table_num != 1) 
//			{
//				int[] sub_tuple_num_arr = 
//						scp.sub_table_lenth(sub_table_num);
//				
//				scp.generate_sub_table(sub_table_num,sub_tuple_num_arr);
//				scp.merge_sub_table(sub_table_num,sub_tuple_num_arr);
//			}
//		}
//		////////////////////////////-----------------------------//////////////

//		//轮询扫描列文件，生成MPI值
//		System.out.println("=============生成MPI值============");
//		Generate_MPI gm = new Generate_MPI();
//		gm.generete_min_pi();
//		
//		//合并各个表，生成扫描表，用时350s
//		System.out.println("=============生成扫描表============");
//		Merge_Scan_Table mst = new Merge_Scan_Table();
//		mst.merge_table();
//		
//		//将scan_table按MPI排序
//		System.out.println("=============将扫描表按MPI排序============");
//		Generate_Sorted_Scan_Table gsst = new Generate_Sorted_Scan_Table();
//		gsst.generate_sub_table();//生成排序子表并写入文件
//		gsst.merge_sub_table();//将子表归并为有序大表
//		
//		//baseline获得skyline
//		Base_MYSKY bal = new Base_MYSKY();
//		bal.generate_skyline();

        //建立选择属性位图
        Generate_Select_Bitmap gbit = new Generate_Select_Bitmap();
        gbit.generate_sel_bitmap();

        //建立自顶向下的度量属性位图
        Generate_Top_Down_Measure_Bitmap gmbit =
                new Generate_Top_Down_Measure_Bitmap();
        gmbit.generate_bitmap();


    }

    public void Less_operate() {
        LESS le = new LESS();
        le.less_skyline();
    }

    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();

        BatchConstruction_MYSKY ms = new BatchConstruction_MYSKY();

//		ms.common_operate();

//		ms.Less_operate();

        ms.MySkyline_operate();

        long endTime = System.currentTimeMillis();

        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
    }

}
