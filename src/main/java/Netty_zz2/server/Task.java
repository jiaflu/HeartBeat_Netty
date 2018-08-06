package Netty_zz2.server;

import java.io.Serializable;

/**
 * Created by lujiafeng on 2018/7/28.
 */
public class Task implements Serializable{
    private int deviceNO;
    private String host;

    public Task(int deviceNO,String host){
        this.host = host;
        this.deviceNO = deviceNO;
    }

    @Override
    public int hashCode() {
        return deviceNO;
    }

    @Override
    public boolean equals(Object obj) {
        return obj.hashCode() == this.hashCode();
    }

    @Override
    public String toString() {
        return "("+deviceNO+","+host+")";
    }
}
