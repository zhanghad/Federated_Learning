package com.fed.client;

import java.util.ArrayList;
import java.util.Date;


//声誉值初始为0.6,取值区间(0，1)
public class Reputation {


    private ArrayList<Date> positive=new ArrayList<Date>();
    private ArrayList<Date> negative=new ArrayList<Date>();
    private ArrayList<Date> uncertain=new ArrayList<Date>();

    private static final Date startTime=new Date();//用户加入时间
    public static final double REPU_LIMIT=0.5;//声誉最低值

    public static double positiveWeight =0.4;
    public static double negativeWeight =0.6;
    public static double fadeWeight =0.8;


    public Reputation(){
        positive.add(startTime);
        negative.add(startTime);
        uncertain.add(startTime);
    }

    //判断声誉值是否满足最低要求
    public boolean repuIsValid(){
        if(calculateReputation()>=REPU_LIMIT)
            return true;
        else
            return false;
    }


    //声誉得分
    public double calculateReputation(){
        double positiveEffect=0;
        double negitiveEffect=0;
        double uncertainEffect=0;
        double belief;


        for (Date date : positive) {
            positiveEffect += timeEffect(date);
        }
        for (Date date : negative) {
            negitiveEffect += timeEffect(date);
        }
        for (Date date : uncertain) {
            uncertainEffect += timeEffect(date);
        }

        belief=positiveWeight*positiveEffect/(positiveWeight*positiveEffect+negativeWeight*negitiveEffect);


        return successProbability()*belief+(1-successProbability())*uncertainEffect;
    }

    private double successProbability(){
        return (double) (positive.size()+negative.size())/(double) (positive.size()+negative.size()+uncertain.size());
    }


    private double timeEffect(Date para){
        double diff=(para.getTime()-startTime.getTime())/(1000.0*60);
        return Math.pow(fadeWeight,diff);
    }



    public ArrayList<Date> getNegative() {
        return negative;
    }

    public ArrayList<Date> getPositive() {
        return positive;
    }

    public ArrayList<Date> getUncertain() {
        return uncertain;
    }

    public void addNegative(Date para) {
         negative.add(para);
    }

    public void addPositive(Date para) {
         positive.add(para);
    }

    public void addUncertain(Date para) {
         uncertain.add(para);
    }


    //测试
/*    public static void main(String[] args) {

        Reputation reputation=new Reputation();

        System.out.println(reputation.calculateReputation());

        for (int i=0;i<100000;i++){
            reputation.addPositive(new Date());
        }
        System.out.println(reputation.calculateReputation());
    }*/

}
