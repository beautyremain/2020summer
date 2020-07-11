package myMain.Controller;

import com.alibaba.fastjson.JSONObject;
import myMain.databus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
public class LoginController {
    @Autowired

    JavaMailSenderImpl mailSender;

    @Autowired
    JdbcTemplate jdbcTemplate;
    //登录的验证 CrossOrigin解决在本地直接用浏览器进行访问时的跨域问题，allowCredentials保证跨域时session不刷新
    @CrossOrigin(origins = "http://127.0.0.1:8020",allowCredentials = "true")
    @RequestMapping(value="/login",method = {RequestMethod.GET,RequestMethod.POST})
    public Object login(@RequestParam String userEmail, @RequestParam String password, HttpSession httpSession, HttpServletResponse response){
        JSONObject jsonObject=new JSONObject();
        if(userEmail==null||password==null){
            jsonObject.put("statusCode",406);
            jsonObject.put("messageDetail","登录信息不全");
            return jsonObject;
        }
        if(databus.stringIllegal(userEmail)||databus.stringIllegal(password)){
            jsonObject.put("statusCode",403);
            jsonObject.put("messageDetail","登录信息含有非法字符");
            return jsonObject;
        }
        String sql_check="select name from userinfo where email=?";

        List result=jdbcTemplate.queryForList(sql_check,new Object[]{userEmail});
        if(result.isEmpty()){
            jsonObject.put("statusCode",401);
            jsonObject.put("messageDetail","用户不存在");
            return jsonObject;
        }
        String sql_get="select name,nickname from userinfo where email=? and password=?";
        List result2=jdbcTemplate.queryForList(sql_get,new Object[]{userEmail,password});
        if(result2.isEmpty()){
            jsonObject.put("statusCode",405);
            jsonObject.put("messageDetail","密码错误");
        }
        else {
            Object data = result2.get(0);
            jsonObject.put("statusCode",0);
            jsonObject.put("messageDetail",data);
            httpSession.setAttribute("LoginUser", userEmail);
            httpSession.setAttribute("LoginPwd", password);
        }
        return jsonObject;

    }
    @CrossOrigin(origins = "http://127.0.0.1:8020",allowCredentials = "true")
    @RequestMapping(value = "/session/test",method={RequestMethod.GET,RequestMethod.POST})
    public Object test(HttpSession session,HttpServletResponse response){

        Object usr = session.getAttribute("LoginPwd");
        if(usr == null){
            return session.toString();
        }
        else {
            return "已登录";
        }
    }



}
