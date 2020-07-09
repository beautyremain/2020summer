package myMain.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/news")
@RestController
public class NewsController {
    @Autowired
    JdbcTemplate jdbcTemplate;

    //1.普通动态的发布
    @RequestMapping("/send/Ordi")
    public Object sendOrdinary(@RequestParam String email,@ RequestParam String info){
        return null;
    }
    //2.招募信息的发布
    //3.答疑信息的发布
    //4.评论信息的发布
    //5.关注用户的动态展示 此处内容需要逐步展示，，按照时间倒序排列好后 每次返回n个动态内容，并记录最后一个返回的动态的id，前端再次发布信息时
    //6.全部动态的展示 细节同上
    //7.评论的通知
    //8.评论的回复
    //9.评论的发布
    @RequestMapping("/send/Comment")
    public Object sendComment(@RequestParam String email,@ RequestParam String info){
        return null;
    }


}
