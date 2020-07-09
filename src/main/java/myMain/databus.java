package myMain;

import com.alibaba.fastjson.JSONObject;

public class databus {


    public static String IMG_BASIC_PATH = "F:\\iconImg\\";




    public static Object setResponse(int statusCode,Object messageDetail){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("statusCode",statusCode);
        jsonObject.put("messageDetail",messageDetail);
        return jsonObject;
    }
    public static Object setResponse(Object messageDetail){
        return setResponse(0,messageDetail);
    }



}
