package myMain.Controller;
/*
    author: 郑文鸿
    create time: 2020/7/16 15:38
*/

import myMain.databus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comp")
public class CompetitionController {

    @Autowired
    JdbcTemplate jdbcTemplate;
    //搜索比赛
    @RequestMapping("/getcompetition")
    public Object getcompetition(@RequestParam String competname , @RequestParam String id) {
        if (id.isEmpty() && competname.isEmpty()) {
            return databus.setResponse(401, "没有参数");
        }
        try {
            String sql = "select * from `competitioninfo` where competname=? or id=?";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql,competname,id);
            if (list.isEmpty()) {
                return databus.setResponse("搜索不到此结果");
            } else {
                return databus.setResponse(list);
            }
        }catch (DataAccessException e){
            System.out.println(e.getMessage());
            return databus.setResponse(501,"信息处理失败");
        }
        catch (Exception e) {
            return databus.setResponse(402, "未知错误");
        }
    }
    @RequestMapping("/markcomp")
    public Object markCompetition(@RequestParam String email,@RequestParam String compname){
        try{
            if(email==null||compname==null){
                return databus.setResponse(401, "缺少参数");
            }
            String sql_getOld="select mark_comp from userinfo where email=?";
            String sql_updateNew="update userinfo set mark_comp=? where email=?";
            String newNames=compname;
            String old=jdbcTemplate.queryForObject(sql_getOld,String.class,email);
            if(old!=null)
                newNames=old+","+newNames;
            jdbcTemplate.update(sql_updateNew,new Object[]{newNames,email});
            return databus.setResponse("感兴趣成功");
        }
        catch (DataAccessException e){
            System.out.println(e.getMessage());
            return databus.setResponse(501,"信息处理失败");
        }
        catch (Exception e) {
            return databus.setResponse(402, "未知错误");
        }
    }
}
