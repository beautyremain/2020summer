package myMain.Controller;/*
    author: BeautyRemain(程季康)
    create time: 2020/7/16 21:19
*/

import myMain.databus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/heat")
public class HeatController {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @RequestMapping("/keyword")
    public Object getHeatWord(){
        try {
            String sql = "select keyword from search_key_stream group by keyword order by COUNT(keyword) desc,sum(time)/COUNT(keyword) DESC limit 5";
            List list=jdbcTemplate.queryForList(sql);
            return databus.setResponse(list);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return databus.setResponse("402","出现错误");

        }
    }
    @RequestMapping("/comp")
    public  Object getHeatComp(){
        try {
        String sql = "select competname from competitioninfo where competstate!='结束' order by heat_number desc limit 3";
        List list=jdbcTemplate.queryForList(sql);
        return databus.setResponse(list);
    }catch (Exception e){
            System.out.println(e.getMessage());
            return databus.setResponse("402","出现错误");

        }
    }
}
