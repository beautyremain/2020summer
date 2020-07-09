package myMain.Controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import myMain.aboutMail.VerifyCodeMailSender;
import myMain.databus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping(value="/register")
public class RegisterController {
    @Autowired
    VerifyCodeMailSender mailTest;

    @Autowired
    JdbcTemplate jdbcTemplate;

    //2.然后发送验证码
    @CrossOrigin(origins = "*",allowCredentials = "true")
    @RequestMapping(value = "/send_code",method = {RequestMethod.GET,RequestMethod.POST})
    public Object sendEmail(@RequestParam String email, HttpSession session){
        try{
            String receiver = email;
            if(email == null){
                return databus.setResponse(406,"没有参数");
            }
            String code=mailTest.sendCodeToMail(receiver);
            session.removeAttribute("verCode");
            session.setAttribute("verCode",code);
            return databus.setResponse(0,"邮件发送成功");
        } catch (Exception e){
            System.out.println(e.getMessage());
            return databus.setResponse(402,"未知错误");
        }
    }

    //1.先检测邮箱是否已经被使用
    @CrossOrigin(origins = "*",allowCredentials = "true")
    @RequestMapping(value = "/check_email",method = {RequestMethod.GET,RequestMethod.POST})
    public Object checkEmail(@RequestParam String email){
        try{
            String sql="select name from userinfo where email=?";

            List result = jdbcTemplate.queryForList(sql,email);
            if(result.isEmpty()){
                return databus.setResponse(0,"未重复，可以使用");
            }
            return databus.setResponse(406,"重复，重新选择");
        } catch (Exception e){
            System.out.println(e.getMessage());
            return databus.setResponse(402,"未知错误");
        }
    }

    //3.验证验证码是否正确
    @CrossOrigin(origins = "*",allowCredentials = "true")
    @RequestMapping(value = "/check_code",method = {RequestMethod.GET,RequestMethod.POST})
    public Object checkCode(@RequestParam String code,HttpSession session){

        try{
            Object verCode = session.getAttribute("verCode");
            if(verCode == null || code == null){
                return databus.setResponse(403,"验证超时或验证信息不全");
            }
            String verCodeStr = verCode.toString();
            if(verCodeStr.equals(code)){
                return databus.setResponse(0,"验证成功");
            }
            else {
                return databus.setResponse(406,"认证失败");
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
            return databus.setResponse(402,"未知错误");
        }
    }
    //4.注册成功，存入数据库
    @CrossOrigin(origins = "*",allowCredentials = "true")
    @RequestMapping(value = "/signup",method = {RequestMethod.GET,RequestMethod.POST})
    public Object signUp(@RequestParam String info){
        try{
            JSONObject jsonObject = JSON.parseObject(info);
            String email = jsonObject.getString("email");
            String sex = jsonObject.getString("sex");
            String password = jsonObject.getString("password");
            String name = jsonObject.getString("name");
            String nickname = jsonObject.getString("nickname");
            if(email == null || sex == null || password == null || name == null || nickname == null){
                return databus.setResponse(406,"参数不齐");
            }
            if(
                      LoginController.stringIllegal(email)
                    ||LoginController.stringIllegal(sex)
                    ||LoginController.stringIllegal(password)
                    ||LoginController.stringIllegal(name)
                    ||LoginController.stringIllegal(nickname)){
                return databus.setResponse(403,"参数非法");
            }
            String sql = "insert into userinfo(name,sex,nickname,password,email) values(?,?,?,?,?)";
            try {

                jdbcTemplate.update(sql, new Object[]{name, sex, nickname, password, email});
            }   catch(DataAccessException e){
                System.out.println(e.getMessage());
                return databus.setResponse(500,"信息存入失败");
            }
            return databus.setResponse(0,"注册成功");
        } catch (Exception e){
            System.out.println(e.getMessage());
            return databus.setResponse(402,"未知错误");
        }

    }

}
