package Netty_zz.message;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author zz
 * @date 2018/7/19 11:19
 */
public class StatusMsg implements Serializable {

    private int maxTaskNum;
    private Set<Integer> nowTaskSet;

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
