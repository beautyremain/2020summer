package myMain.Controller;

import com.alibaba.fastjson.JSONObject;
import myMain.aboutMail.VerifyCodeMailSender;
import myMain.aboutPy.getPy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
public class LoginController {
    @Autowired
    JavaMailSenderImpl mailSender;

    @Autowired
    JdbcTemplate jdbcTemplate;
    public static boolean stringIllegal(String str){
        return(str.contains("\'")||str.contains("\"")||str.contains(";"));
    }
    //登录的验证
    @CrossOrigin(origins = "http://127.0.0.1:8020",allowCredentials = "true")
    @RequestMapping(value="/login",method = {RequestMethod.GET,RequestMethod.POST})
    public Object login(@RequestParam String userEmail, @RequestParam String password, HttpSession httpSession, HttpServletResponse response){
        JSONObject jsonObject=new JSONObject();
        if(userEmail==null||password==null){
            jsonObject.put("statusCode",403);
            jsonObject.put("messageDetail","登录信息不全");
            return jsonObject;
        }
        if(stringIllegal(userEmail)||stringIllegal(password)){
            jsonObject.put("statusCode",403);
            jsonObject.put("messageDetail","登录信息含有非法字符");
            return jsonObject;
        }
        String sql_check="select name from userinfo where email=?";

        List result=jdbcTemplate.queryForList(sql_check,new Object[]{userEmail});
        if(result.isEmpty()){
            jsonObject.put("statusCode",105);
            jsonObject.put("messageDetail","用户不存在");
            return jsonObject;
        }
        String sql_get="select name,nickname from userinfo where email=? and password=?";
        List result2=jdbcTemplate.queryForList(sql_get,new Object[]{userEmail,password});
        if(result2.isEmpty()){
            jsonObject.put("statusCode",104);
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
    @RequestMapping(value = "/out",method={RequestMethod.GET,RequestMethod.POST})
    public Object out(HttpSession session){
        getPy.get();
        Object usr = session.getAttribute("loginUser");
        if(usr == null){
            return session.toString();
        }
        else {
            return "已登录";
        }
    }
    @Autowired
    VerifyCodeMailSender mailTest;

    @RequestMapping(value = "/verification",method = {RequestMethod.GET,RequestMethod.POST})
    public String sendEmail(@RequestParam String email){
        String receiver = "jikang_cheng@qq.com";
        mailTest.sendCodeToMail(receiver);
        return "accept";
    }



}
