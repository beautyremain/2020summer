package myMain.Controller;

import myMain.databus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/inform")
public class InformController {

    @Autowired
    JdbcTemplate jdbcTemplate;


    //goal : getNew:0,getHistory:1
    private  static final String getNew="0";
    private  static final  String getHistory="1";
    //type:0为系统通知,1为点赞通知,2为比赛通知,3为评论通知
    @RequestMapping("/all/{type}")
    //7.评论的通知 组队信息通知队长 暂时不适用实时信息传输
    public Object inform(@RequestParam String email, @RequestParam String newest_id, @RequestParam String oldest_id, @RequestParam String goal, @PathVariable String type){
        try {
            if (email == null || oldest_id == null || newest_id == null || goal == null || type == null) {
                return databus.setResponse(401, "参数不全");
            }
            if(!goal.equals(getNew)&&!goal.equals(getHistory)) {
                return databus.setResponse(406,"目标有误:"+goal);
            }
            String g = goal.equals(getNew)? ">":"<";
            String i = goal.equals(getNew)? newest_id:oldest_id;
            String sql = null;
            List result = null;
            if(type.equals("3")) {
                sql = " select * from news_info_stream where type='3' and id" + g + "? and response_id in (select id from news_info_stream where sender = ? or sender like '%\"cap\":\"" + email + "\"%' ) order by id desc limit ?";
                result = jdbcTemplate.queryForList(sql, new Object[]{i, email, databus.RESPONSE_MAX_DYNAMICS_NUMBER});
            }
            else{
                sql = "select * from inform_table where type=? and id"+g+"? and (response_id in (select id from news_info_stream where sender = ? or sender like '%\"cap\":\"" + email + "\"%' ) or response_id='0') order by id desc limit ?";
                result =jdbcTemplate.queryForList(sql,new Object[]{type,i,email,databus.RESPONSE_MAX_DYNAMICS_NUMBER});
            }
            System.out.println(sql);
            if(result.isEmpty()){
                return databus.setResponse("没有新消息");
            }

            return databus.setResponse(result);




        }
        catch (EmptyResultDataAccessException e){
            return databus.setResponse(405,"出现empty set");
        }
        catch (DataAccessException e){
            System.out.println(e.getMessage());
            return databus.setResponse(501,"信息处理失败");
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return databus.setResponse(402,"未知错误："+e.getMessage());
        }
    }

}
