package myMain.Controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {
private static String responseHeader="Access-Control-Allow-Origin";
    @Autowired
    JdbcTemplate jdbcTemplate;
    @RequestMapping(value="/sql", method=RequestMethod.GET)
    public List<Map<String, Object>> index(HttpServletResponse response,@RequestParam String sql) {
        response.setHeader("Access-Control-Allow-Origin","*");
        return      jdbcTemplate.queryForList(sql);
    }
    @RequestMapping(value="/home", method=RequestMethod.GET)
    public List<Map<String, Object>> getHome(HttpServletResponse response){
        String sql = "Select name from userinfo where name=?";
        response.setHeader(responseHeader,"*");
        return jdbcTemplate.queryForList(sql,new Object[]{"daisy"});
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
}
