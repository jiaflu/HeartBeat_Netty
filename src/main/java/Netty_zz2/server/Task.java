package Netty_zz2.server;

import java.io.Serializable;

/**
 * @author zz
 * @date 2018/8/5 10:54
 */
public class Task implements Serializable {
    private int deviceNO;
    private String host;

    public Task(int deviceNO,String host){
        this.host = host;
        this.deviceNO = deviceNO;
    }

    @Override
    public String toString() {
        return "("+deviceNO+","+host+")";
    }
}
