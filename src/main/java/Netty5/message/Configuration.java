package Netty5.message;

import java.io.Serializable;

/**
 * Created by lujiafeng on 2018/8/5.
 */
public class Configuration implements Serializable {
    private int deviceNO;
    private String host;

    public Configuration(int deviceNO,String host){
        this.host = host;
        this.deviceNO = deviceNO;
    }

    @Override
    public String toString() {
        return "("+deviceNO+","+host+")";
    }
}
