package Netty_zz2.message;



import Netty_zz2.server.Task;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author zz
 * @date 2018/7/19 11:19
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

    public void addNowTaskSet(Set<Task> assignTaskNum){
        nowTaskSet.addAll(assignTaskNum);
    }

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
//        return stringBuilder.toString();
        return String.valueOf(nowTaskSet.size());
    }
}
