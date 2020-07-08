package myMain.Controller;

import myMain.aboutPy.getPy;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
public class LoginController {
    @CrossOrigin(origins = "http://127.0.0.1:8020",allowCredentials = "true")
    @RequestMapping(value="/login",method = {RequestMethod.GET,RequestMethod.POST})
    public String login(@RequestParam String username, @RequestParam String password, HttpSession httpSession, HttpServletResponse response){

        httpSession.setAttribute("LoginUser",username);
        httpSession.setAttribute("LoginPwd",password);
        return httpSession.toString();

    }
    @CrossOrigin(origins = "http://127.0.0.1:8020",allowCredentials = "true")
    @RequestMapping(value = "/testlog",method={RequestMethod.GET,RequestMethod.POST})
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
}
