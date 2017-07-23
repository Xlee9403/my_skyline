package src.algrithm;

import  src.algrithm.ISSA;
import  src.algrithm.SIDS;
import src.fileop.*;

/**
 * Created by Xue on 2017/7/15.
 */
public class BatchConstruction
{
    //共同的操作，生成初始表
    public void common_construction()
    {
        Generate_Initial_Table git = new Generate_Initial_Table();
        git.generate();
    }

    //SIDS的处理流程
    public void batch_SIDS()
    {
        //----------------------预处理阶段----------------------//
        //计算每个元组的完整维度数
        Generate_PT_with_Complete_Count gcc =
                new Generate_PT_with_Complete_Count();
        gcc.genetate();

        //生成列文件
        Generate_Column gc = new Generate_Column();
        gc.generate();

        //对列文件排序
        Sort_Column sc = new Sort_Column();
        sc.sort();

        //生成候选集
        Generate_Candidate_Set gcs = new Generate_Candidate_Set();
        gcs.generate();
        /////////////////--------------------------////////////////

        //计算skyline的结果
		SIDS ss ;
        ss = new SIDS();
		ss.genetate_skyline();

    }

    //ISSA的算法执行流程
    public void batch_ISSA()
    {
        //计算每个元组所有完整维度的平均值
        Generate_PT_With_ACD gacd = new Generate_PT_With_ACD();
        gacd.generate();

        //按照平均值对PT排序
       Sort_PT_with_ACD sa = new Sort_PT_with_ACD();
        sa.sort();

		ISSA is = new ISSA();
		is.generate_skyline();
    }

    //MY_RPID的算法执行流程
    public void batch_MY_RPID()
    {
        //计算每个元组所有完整维度的平均值
        Generate_PT_With_ACD gacd = new Generate_PT_With_ACD();
        gacd.generate();

        Sort_PT_with_ACD sa = new Sort_PT_with_ACD();
        sa.sort();

        Generate_Sort_Initial_Table gsit = new Generate_Sort_Initial_Table();
        gsit.generate();

        MY_RPID mpr = new MY_RPID();
        mpr.generate_skyline();
    }

    public static void main(String[] args)
    {
        long startTime = System.currentTimeMillis();

        BatchConstruction bc = new BatchConstruction();
//		bc.common_construction();

        //SIDS的算法流程
//		bc.batch_SIDS();

      bc.batch_ISSA();

//        bc.batch_MY_RPID();

        long endTime = System.currentTimeMillis();

        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");

    }
}
