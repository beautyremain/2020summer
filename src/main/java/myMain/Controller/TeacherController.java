package myMain.Controller;/*
    author: BeautyRemain(程季康)
    create time: 2020/7/17 14:34
*/

import myMain.databus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/teacher")
public class TeacherController {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @RequestMapping("/check")
    public Object checkId(@RequestParam String id){
        try{
            String sql = "select * from teacher_list where user_id=?";
            List list = jdbcTemplate.queryForList(sql,id);
            if(list.isEmpty()){
                return databus.setResponse("身份有误");
            }
            else{
                return databus.setResponse("验证成功");
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            return databus.setResponse(402,"未知错误");
        }
    }

}
