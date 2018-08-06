package Netty_zz2.message;



import Netty_zz2.server.Task;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by lujiafeng on 2018/7/28.
 */
public class StatusMsg implements Serializable {

    private int maxTaskNum;
    private Set<Task> nowTaskSet;

    public StatusMsg(int maxTaskNum){
        this.maxTaskNum = maxTaskNum;
        nowTaskSet = new HashSet<Task>();
    }

    public int getNowTaskNum(){
        return nowTaskSet.size();
    }

    public int getMaxTaskNum(){
        return maxTaskNum;
    }

    public Set<Task> getNowTaskSet(){
        return nowTaskSet;
    }

    /**
     * 添加任务
     * @param assignTaskNum
     */
    public void addNowTaskSet(Set<Task> assignTaskNum){
        nowTaskSet.addAll(assignTaskNum);
    }

    /**
     * 剥夺任务
     * @param assignTaskSet
     */
    public void removeNowTaskSet(Set<Task> assignTaskSet){
        nowTaskSet.removeAll(assignTaskSet);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder( "{\"nowTaskSet\":");
        for(Task task : nowTaskSet){
            stringBuilder.append(task);
            stringBuilder.append(";");
        }
        stringBuilder.append(",\"maxTaskNum\":"+maxTaskNum+"}");
        return stringBuilder.toString();
    }
}
