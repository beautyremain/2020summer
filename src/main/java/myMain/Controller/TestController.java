package myMain.Controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import myMain.aboutPy.GetPy;
import myMain.databus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")


//此控制器作为测试依赖用法与语法的模块，在实际发布时会删除
public class TestController {
private static String responseHeader="Access-Control-Allow-Origin";
    @Autowired
    JdbcTemplate jdbcTemplate;
    @CrossOrigin(origins = "*",allowCredentials = "true")
    @RequestMapping(value="/sql", method=RequestMethod.GET)
    public Object index(@RequestParam String sql) {
        List list=jdbcTemplate.queryForList(sql);
        System.out.println(list.get(0));
        System.out.println(list.get(0).getClass());
        LinkedCaseInsensitiveMap map=(LinkedCaseInsensitiveMap)list.get(0);
        System.out.println(map.get("id"));
        return databus.setResponse(0,list);
    }
    @RequestMapping("/oper")
    public Object operate(@RequestParam String sql){
        try {
            List list = jdbcTemplate.queryForList(sql);
            LinkedCaseInsensitiveMap map=(LinkedCaseInsensitiveMap)list.get(0);
            map.get("comment");
            try {
                String obj = (String) map.get("comment");;
                String[] objs=obj.split("&!space!&");
                String obj1=objs[0];
                String obj2=objs[1];
                try{
                    JSONObject jsonObject1=(JSONObject) JSON.parse(obj1);
                    JSONObject jsonObject2=(JSONObject) JSON.parse(obj2);
                    System.out.println(jsonObject1.toString());
                    System.out.println(jsonObject2.toString());
                    System.out.println(jsonObject1.getObject("reply",JSONObject.class).getString("sender"));
                    System.out.println(jsonObject2.getString("sender"));
                } catch (Exception e){
                    System.out.println("error location3:"+e.getMessage());
                    System.out.println(e.getCause());
                    return "error";
                }
            }catch (Exception e){
                System.out.println("error location1:"+e.getMessage());

                return null;
            }

        }catch (DataAccessException e){
            System.out.println("error location2:"+e.getMessage());
            return null;
        }

        return "complete";
    }

    @RequestMapping(value="/home", method=RequestMethod.GET)
    public void testGrammar(HttpServletResponse response){
        String sql = "Select name from userinfo where name=?";
        response.setHeader(responseHeader,"*");
        String s=System.getProperty("user.dir");
        System.out.println(s);
        File fileDir = new File(new String("src/main/resources/imgIcon" ));// String fileDirPath = new String("src/main/resources/" + IMG_PATH_PREFIX);
        // 输出文件夹绝对路径  -- 这里的绝对路径是相当于当前项目的路径而不是“容器”路径
        System.out.println(fileDir.getAbsolutePath());

    }
    @RequestMapping("/test/{id}")
    public List<Map<String, Object>> seId(@PathVariable int id,HttpServletResponse response){
        response.setHeader(responseHeader,"*");
        String sql = "select * from label_info where id = "+id;
        return jdbcTemplate.queryForList(sql);
    }
    //对json的处理方法,字符串
    @CrossOrigin(origins = "http://127.0.0.1:8020")
    @RequestMapping(value="/jsonPost",method={RequestMethod.POST,RequestMethod.GET})
    public Object setMore(@RequestParam String A, HttpServletResponse response){
//        response.setHeader("Access-Control-Allow-Origin","http://127.0.0.1:8020");
//        response.setHeader("Access-Control-Allow-Methods","POST,GET,PATCH");
//        response.setHeader("Access-Control-Allow-Headers","Origin,X-Requested-Witch,Content-Type,Accept");

        JSONObject jsonObject = JSON.parseObject(A);
        String cjk = jsonObject.getString("cjk");
        String x=jsonObject.toJSONString();
        System.out.println(cjk);

        return jsonObject;
    }

    @RequestMapping(value = "/out",method={RequestMethod.GET,RequestMethod.POST})
    public Object out(){
        String type="0";
        String id="1";
        String[] id_list=(String[]) GetPy.getAlgorithmResult(type,id);

        String condition="";
        for(String each : id_list){
            condition+=each+",";
        }


        condition=condition.substring(0,condition.length()-1);
        String sql = "select * from groupinfo where id in("+condition+")";System.out.println(sql);
        List result=jdbcTemplate.queryForList(sql);

        return result;
    }

}
