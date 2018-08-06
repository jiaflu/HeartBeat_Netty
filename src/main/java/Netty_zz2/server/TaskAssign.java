package Netty_zz2.server;



import Netty_zz2.message.AssignTaskMsg;
import Netty_zz2.message.StatusMsg;

import java.io.*;
import java.util.*;

/**
 * Created by lujiafeng on 2018/7/28.
 */
public class TaskAssign {
    private StatusMsg statusMsg;
    private AssignTaskMsg assignTaskMsg;
    private final int TASK_NUM;
    private final String CONFIG_FILE_PATH = "Task.ini";
    private HashMap<String, Set<Task>> map;   //存储每个客户端发送的信息中的任务数
    private int need;       //需要重新分配多少个任务
    private int first;
    private int nowTaskNum, maxTaskNum, assignTaskNum, optTaskNum;
    private int nowAllTaskNum;    //未分配任务数
    private Set<Task> nowAllTaskSet;    //未分配任务
    private Set<Task> assignTaskSet;    //分配的任务
    private Set<Task> nowTaskSet;     //已分配的任务
    private int sum;
    private Set<Task> sumSet;
    private String socket;

    public TaskAssign(){
        init();
        TASK_NUM = nowAllTaskSet.size();
        System.out.println(TASK_NUM);
    }

    /**
     * 初始化
     */
    public void init(){
        nowAllTaskSet = new HashSet<Task>();
        sumSet = new HashSet<Task>();
        map = new HashMap<String, Set<Task>>();
        first = 0;
        need = 0;
        sum = 0;
        readConfigFile(CONFIG_FILE_PATH);
    }

    /**
     * 配置任务，配置文件是Task.ini
     * @param path
     */
    public void readConfigFile(String path){
        String lineString = null;
        FileInputStream fileInputStream = null;
        int deviceNO = 0;
        String host = null;
        try {
            fileInputStream = new FileInputStream(path);
        }catch (FileNotFoundException e){
            System.err.println("找不到配置文件");
            System.exit(-1);
        }
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        try {
            while (null != (lineString = bufferedReader.readLine())) {
                String[] splitString = lineString.split("=");
                if(']' == (splitString[1].charAt(splitString[1].length()-1))) {
                    splitString[1] = splitString[1].substring(0,splitString[1].length()-1);
                    deviceNO = Integer.parseInt(splitString[1].trim());
                }
                else{
                    host = splitString[1].trim();
                    nowAllTaskSet.add(new Task(deviceNO,host));
                }
            }
        }catch (Exception e){
            System.err.println("读取配置文件失败，请检查配置文件");
            System.exit(-1);
        }
    }

    public void setStatusMsg(StatusMsg statusMsg){
        this.statusMsg = statusMsg;
    }

    /**
     * 任务分配与回收，recall==true表示回收，false表示分配
     * @param recall
     * @param nowSocket
     * @return
     */
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
        nowTaskSet = new HashSet<Task>();

        if (map.containsKey(nowSocket)) {
            nowTaskSet.addAll(map.get(nowSocket));
            nowTaskNum = nowTaskSet.size();
        } else {
            nowTaskSet.addAll(statusMsg.getNowTaskSet());
            nowTaskNum = nowTaskSet.size();
        }

        maxTaskNum = statusMsg.getMaxTaskNum();
        assignTaskNum = 0;
        assignTaskSet = new HashSet<Task>();

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
                    Iterator<Task> it = nowAllTaskSet.iterator();
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
