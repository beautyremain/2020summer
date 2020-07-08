package myMain;

import com.alibaba.fastjson.JSONObject;

public class databus {
    public static String IMG_BASIC_PATH = "F:\\iconImg";
    public static Object setResponse(int statusCode,String messageDetail){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("statusCode",statusCode);
        jsonObject.put("messageDetail",messageDetail);
        return jsonObject;
    }
    public static Object setResponse(String messageDetail){
        return setResponse(0,messageDetail);
    }
}
