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
    //type:0为系统通知,1为点赞通知,2为比赛通知,3为评论通知,4为点赞通知,5为老师收到邀请，6为学生查看回执
    @RequestMapping("/all/{type}")
    //7.评论的通知 组队信息通知队长 暂时不考虑基于websocket的全双工实时信息传输，依然使用http模式
    public Object inform(@RequestParam String email, @RequestParam String newest_id, @RequestParam String oldest_id, @RequestParam String goal, @PathVariable String type){
        try {
            if (email == null || oldest_id == null || newest_id == null || goal == null || type == null) {
                return databus.setResponse(401, "参数不全");
            }
            if(!goal.equals(getNew)&&!goal.equals(getHistory)) {
                return databus.setResponse(406,"目标有误:"+goal);
            }

            //创建在不同目标类型下sql语句的差异处
            String g = goal.equals(getNew)? ">":"<";
            String i = goal.equals(getNew)? newest_id:oldest_id;


            String sql = null;
            List result = null;
            if(type.equals("3")) {
                sql = " select * from news_info_stream where type='3' and id" + g + "? and response_id in (select id from news_info_stream where sender = ? or sender like '%\"cap\":\"" + email + "\"%' ) order by id desc limit ?";
                result = jdbcTemplate.queryForList(sql, new Object[]{i, email, databus.RESPONSE_MAX_DYNAMICS_NUMBER});
            }
            else if(type.equals("2")){
                String sql_getUserMarkGroup = "select mark_comp from userinfo where email=?";
                try {
                    String MarkComp=jdbcTemplate.queryForObject(sql_getUserMarkGroup,String.class,email);
                    String[] Comps=MarkComp.split(",");
                    String condition="";
                    for(String each:Comps){
                        condition+="'"+each+"',";
                    }
                    condition=condition.substring(0,condition.length()-1);
                    sql="select * from inform_table where type=2 and id"+g+i+" and sender in ("+condition+") order by id desc limit "+databus.RESPONSE_MAX_DYNAMICS_NUMBER;
                    System.out.println("edit sql:"+sql);
                    result=jdbcTemplate.queryForList(sql);
                } catch (EmptyResultDataAccessException e){
                    return databus.setResponse("没有感兴趣的比赛");
                }
            }
            else if(type.equals("4")){
                sql="select * from inform_table where type=4 and id"+g+i+" and response_id in (select id from userinfo where email=?) order by id desc limit "+databus.RESPONSE_MAX_DYNAMICS_NUMBER;
                result=jdbcTemplate.queryForList(sql,email);
            }
            else if(type.equals("5")){
                String teacher_id = jdbcTemplate.queryForObject("select id from userinfo where email=?",String.class,email);
                sql="select group_id,message,state from invite_stream where id"+g+i+" and teacher_id=? order by state,id desc limit "+databus.RESPONSE_MAX_DYNAMICS_NUMBER;
                result=jdbcTemplate.queryForList(sql,teacher_id);

            }
            else if(type.equals("6")){

                sql="select teacher_id,group_id,reply,state from invite_stream where id"+g+i+" and group_id in (select id from groupinfo where cap_email=?) order by id desc limit "+databus.RESPONSE_MAX_DYNAMICS_NUMBER;
                result=jdbcTemplate.queryForList(sql,email);
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
