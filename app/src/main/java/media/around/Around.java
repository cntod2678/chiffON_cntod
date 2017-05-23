package media.around;

import android.app.Application;


public class Around extends Application {
    // 통신
    //public static String url = "http://192.168.255.89:8080/chiffON";
    public static String url = "http://10.10.3.27:8080/chiffON";

    //무녕 핫스팟
    //public static String url = "http://172.20.10.6:8080/chiffON";

    private String myBeaconId;
    private String myId;

    public String getMyBeaconId() {
        return myBeaconId;
    }

    public String getMyId() { return myId;}

    public void setMyBeaconId(String id) {
        this.myBeaconId = id;
    }

    public void setMyId(String id) { this.myId = id;}
}
