package Netty_zz2.message;



import Netty_zz2.server.Task;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author zz
 * @date 2018/7/19 11:35
 */
public class AssignTaskMsg implements Serializable {
    private static final long serialVersionUID = 3L;
    private int assignTaskNum;
    private Set<Task> assignTaskSet;

    public AssignTaskMsg(){
        assignTaskSet = new HashSet<Task>();
    }

    public int getAssignTaskNum(){
        return assignTaskNum;
    }

    public Set<Task> getAssignTaskSet() {
        return assignTaskSet;
    }

    public void setAssignTaskSet(int assignTaskNum, Set<Task> assignTaskSet){
        this.assignTaskNum = assignTaskNum;
        this.assignTaskSet.addAll(assignTaskSet);
    }

    @Override
    public String toString() {
        return "{\"assignTaskSet\":"+assignTaskSet+",\"assignTaskNum\":"+assignTaskNum+"}";
    }
}
