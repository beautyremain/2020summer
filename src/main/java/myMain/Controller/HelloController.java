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
public class HelloController {
private static String responseHeader="Access-Control-Allow-Origin";
    @Autowired
    JdbcTemplate jdbcTemplate;
    @RequestMapping(value="/hello", method=RequestMethod.GET)
    public List<Map<String, Object>> index(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin","*");
        String sql = "SELECT * from user_info";

        // 通过jdbcTemplate查询数据库


      // return      head+"("+jdbcTemplate.queryForList(sql).toString()+")";
        return      jdbcTemplate.queryForList(sql);
    }
    @RequestMapping(value="/home", method=RequestMethod.GET)
    public List<Map<String, Object>> getHome(HttpServletResponse response){
        String sql = "Select name,address from user_info where name='new name'";
        response.setHeader(responseHeader,"*");
        return jdbcTemplate.queryForList(sql);
    }
    @RequestMapping("/test/{id}")
    public List<Map<String, Object>> seId(@PathVariable int id,HttpServletResponse response){
        response.setHeader(responseHeader,"*");
        String sql = "select name,address from user_info where id = "+id;
        return jdbcTemplate.queryForList(sql);
    }
    //对json的处理方法,字符串
    @CrossOrigin(origins = "http://127.0.0.1:8020")
    @RequestMapping(value="/jsonPost",method={RequestMethod.POST,RequestMethod.GET})
    public String setMore(@RequestParam String A, HttpServletResponse response){
//        response.setHeader("Access-Control-Allow-Origin","http://127.0.0.1:8020");
//        response.setHeader("Access-Control-Allow-Methods","POST,GET,PATCH");
//        response.setHeader("Access-Control-Allow-Headers","Origin,X-Requested-Witch,Content-Type,Accept");

        JSONObject jsonObject = JSON.parseObject(A);

        String cjk = jsonObject.getString("cjk");

        System.out.println(cjk);

        return A;
    }
}
