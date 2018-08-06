package Netty5.Server;

import Netty5.message.AssignTaskMsg;
import Netty5.message.StatusMsg;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by lujiafeng on 2018/8/2.
 */
public class Task {

    private StatusMsg statusMsg;
    private AssignTaskMsg assignTaskMsg;
    private final int TASK_NUM = 25;
    private HashMap<String, Set<Integer>> map;   //存储每个客户端发送的信息中的任务数
    private int need;       //需要重新分配多少个任务
    private int first;
    private int sum;
    private int nowTaskNum, maxTaskNum, assignTaskNum, optTaskNum;
    private int nowAllTaskNum;    //现有总任务数
    private Set<Integer> nowAllTaskSet;
    private Set<Integer> assignTaskSet,nowTaskSet;
    private Set<Integer> sumSet;
    private String socket;

    public Task(){
        init();
    }

    public void init(){
        nowAllTaskSet = new HashSet<Integer>();
        sumSet = new HashSet<Integer>();
        map = new HashMap<String, Set<Integer>>();
        first = 0;
        need = 0;
        sum = 0;
        for(int i=0;i<TASK_NUM;i++){
            nowAllTaskSet.add(i);
        }
    }

    public void setStatusMsg(StatusMsg statusMsg){
        this.statusMsg = statusMsg;
    }

    public synchronized AssignTaskMsg assignTask(boolean recall,String nowSocket){
        if(recall){
            if(map.containsKey(nowSocket)){
                nowAllTaskSet.addAll(map.get(nowSocket));
                nowAllTaskNum = nowAllTaskSet.size();
                map.remove(nowSocket);
            }
            return null;
        }
        assignTaskMsg = new AssignTaskMsg();
        nowTaskSet = new HashSet<Integer>();

        if (map.containsKey(nowSocket)) {
            nowTaskSet.addAll(map.get(nowSocket));
            nowTaskNum = nowTaskSet.size();
        } else {
            nowTaskSet.addAll(statusMsg.getNowTaskSet());
            nowTaskNum = nowTaskSet.size();
        }

        maxTaskNum = statusMsg.getMaxTaskNum();
        assignTaskNum = 0;
        assignTaskSet = new HashSet<Integer>();

        map.put(nowSocket, nowTaskSet);

        optTaskNum = TASK_NUM / map.size() > maxTaskNum ?
                maxTaskNum : TASK_NUM / map.size();

        if (0 == first) {
            first = 1;
            socket = nowSocket;
            sum += statusMsg.getNowTaskNum();
            sumSet.addAll(statusMsg.getNowTaskSet());
        } else if (1 == first) {
            if (socket.equals(nowSocket)) {
                nowAllTaskNum = TASK_NUM - sum;
                nowAllTaskSet.removeAll(sumSet);
                System.out.println("总和：\t" + sum);
                System.out.println("现有：\t" + nowAllTaskNum);
                System.out.println("连接：\t" + map.size());
                first = 2;
            }
            sum += statusMsg.getNowTaskNum();
            sumSet.addAll(statusMsg.getNowTaskSet());
        }else {
            if (optTaskNum > nowTaskNum) {
                if (nowAllTaskNum >= optTaskNum - nowTaskNum) {
                    assignTaskNum = optTaskNum - nowTaskNum;
                    Iterator<Integer> it = nowAllTaskSet.iterator();
                    for(int i=0;i<assignTaskNum;i++){
                        if(it.hasNext()){
                            assignTaskSet.add(it.next());
                        }
                    }
                    nowAllTaskNum -= assignTaskNum;
                    nowAllTaskSet.removeAll(assignTaskSet);
                } else {
                    need += optTaskNum - nowTaskNum - nowAllTaskNum;
                    assignTaskNum = nowAllTaskNum;
                    assignTaskSet.addAll(nowAllTaskSet);
                    nowAllTaskNum -= assignTaskNum;
                    nowAllTaskSet.removeAll(assignTaskSet);
                }
            } else if (nowTaskNum > optTaskNum) {
                if (nowTaskNum > optTaskNum + 1) {
                    assignTaskNum = -1;
                    need += assignTaskNum;
                    nowAllTaskNum -= assignTaskNum;
                    assignTaskSet.add(map.get(nowSocket).iterator().next());
                    nowAllTaskSet.addAll(assignTaskSet);
                } else if (0 < need) {
                    assignTaskNum = -1;
                    need += assignTaskNum;
                    nowAllTaskNum -= assignTaskNum;
                    assignTaskSet.add(map.get(nowSocket).iterator().next());
                    nowAllTaskSet.addAll(assignTaskSet);
                }
            } else {
                if (maxTaskNum > nowTaskNum && nowAllTaskNum > 0) {
                    assignTaskNum = 1;
                    assignTaskSet.add(nowAllTaskSet.iterator().next());
                    nowAllTaskNum -= assignTaskNum;
                    nowAllTaskSet.removeAll(assignTaskSet);
                }
            }
        }
        assignTaskMsg.setAssignTaskSet(assignTaskNum,assignTaskSet);

        System.out.println("分配：\t" + assignTaskNum);
        System.out.println("剩余：\t" + nowAllTaskNum);
        System.out.println("平均：\t" + optTaskNum);
        System.out.println("连接：\t" + map.size());
        System.out.println("***********************");

        if(assignTaskNum > 0)
            nowTaskSet.addAll(assignTaskSet);
        else{
            nowTaskSet.removeAll(assignTaskSet);
        }
        map.put(nowSocket,nowTaskSet);

        return assignTaskMsg;
    }

}
