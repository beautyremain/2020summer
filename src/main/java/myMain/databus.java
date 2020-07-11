package myMain;

import com.alibaba.fastjson.JSONObject;

import java.util.regex.Pattern;

//全局变量与函数
public class databus {


    public static final String ICON_IMG_BASIC_PATH = "/src/main/resources/imgIcon/";
    public static final String DYNAMIC_IMG_BASIC_PATH = "/src/main/resources/DYNAMIC/";
    public static final int RESPONSE_MAX_DYNAMICS_NUMBER = 10;


    //生成响应包
    public static Object setResponse(int statusCode,Object messageDetail){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("statusCode",statusCode);
        jsonObject.put("messageDetail",messageDetail);
        return jsonObject;
    }
    public static Object setResponse(Object messageDetail){
        return setResponse(0,messageDetail);
    }

    //检查sql与js注入
    public static boolean stringIllegal(String str){
        return(str.contains("\'")||str.contains("\"")||str.contains(";")|| Pattern.matches("<\\s*script", str));
    }

}
