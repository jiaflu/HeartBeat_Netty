package Netty5.message;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by lujiafeng on 2018/7/31.
 */

/**
 * 客户端发送给服务器的状态信息
 */
public class StatusMsg implements Serializable {

    private int maxTaskNum;  //采集客户端可容纳的最大任务数
    private Set<Integer> nowTaskSet;   //当前所容纳的任务集合（任务有标号）

    public StatusMsg(int maxTaskNum){
        this.maxTaskNum = maxTaskNum;
        nowTaskSet = new HashSet<Integer>();
    }

    public int getNowTaskNum(){
        return nowTaskSet.size();
    }

    public int getMaxTaskNum(){
        return maxTaskNum;
    }

    public Set<Integer> getNowTaskSet(){
        return nowTaskSet;
    }

    public void addNowTaskSet(Set<Integer> assignTaskNum){
        nowTaskSet.addAll(assignTaskNum);
    }

    public void removeNowTaskSet(Set<Integer> assignTaskSet){
        nowTaskSet.removeAll(assignTaskSet);
    }

    @Override
    public String toString() {
        return "{\"nowTaskSet\":"+nowTaskSet+",\"maxTaskNum\":"+maxTaskNum+"}";
    }
}
