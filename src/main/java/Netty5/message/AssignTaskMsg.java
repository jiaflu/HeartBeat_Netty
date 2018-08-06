package Netty5.message;

import java.io.Serializable;
import java.util.Set;
import java.util.HashSet;
/**
 * Created by lujiafeng on 2018/7/31.
 */

/**
 * 服务器发送给客户端的任务分配信息
 */
public class AssignTaskMsg implements Serializable {
    //serialVersionUID 用来表明类的不同版本间的兼容性
    private static final long serialVersionUID = 3L;
    private int assignTaskNum;  //任务分配数量

    private Set<Integer> assignTaskSet;  //任务分配集合

    //private Set<Configuration> assignTaskSet;

    public AssignTaskMsg() {
        assignTaskSet = new HashSet<Integer>();
    }
    /*
    public AssignTaskMsg(int assignTaskNum) {
        this.assignTaskNum = assignTaskNum;
        assignTaskSet = new HashSet<Configuration>();
        Configuration c1 = new Configuration(1, "127.0.0.1");
        Configuration c2 = new Configuration(2, "127.0.0.1");
        assignTaskSet.add(c1);
        assignTaskSet.add(c2);
    }
    */

    public int getAssignTaskNum(){
        return assignTaskNum;
    }

    public Set<Integer> getAssignTaskSet() {
        return assignTaskSet;
    }

    public void setAssignTaskSet(int assignTaskNum, Set<Integer> assignTaskSet){
        this.assignTaskNum = assignTaskNum;
        this.assignTaskSet.addAll(assignTaskSet);
    }

    @Override
    public String toString() {
        return "{\"assignTaskSet\":"+assignTaskSet+",\"assignTaskNum\":"+assignTaskNum+"}";
    }
}
