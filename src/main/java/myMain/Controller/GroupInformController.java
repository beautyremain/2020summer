
/*
    author: BeautyRemain(程季康)
    create time: 2020/7/12 13:41
*/
package myMain.Controller;

import myMain.databus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/group")
//这里处理的是当用户准备递交入队申请开始的有关信息，将GroupController中的加入队伍也转移到此控制器下
public class GroupInformController {
    @Autowired
    JdbcTemplate jdbcTemplate;

    private  static final String getNew="0";
    private  static final  String getHistory="1";


    //1.用户A申请入组,提交相关信息
    @RequestMapping("/applyGroup")
    public Object applyGroup(@RequestParam String sender_email,@RequestParam String group_id,@RequestParam String message){
        try{
            if(sender_email == null || group_id == null || message == null){
                return databus.setResponse(401, "参数不全");
            }
            if(databus.stringIllegal(message)){
                return  databus.setResponse(403,"参数非法");
            }
            String sql = "insert into apply_stream(group_id,response_id,message,sender,type) values(?,?,?,?,?)";
            jdbcTemplate.update(sql,new Object[]{group_id,group_id,message,sender_email,0});
            return databus.setResponse("提交成功");
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
    //select * from apply_stream where (response_id in (select id from apply_stream where sender="jikang_cheng@qq.com" or sender like '%"cap":"jikang_cheng@qq.com"%') or (type=0 and group_id in (select id from news_info_stream where sender="jikang_cheng@qq.com" or sender like '%"cap":"jikang_cheng@qq.com"%'))) and id>0 order by id desc limit 10
    //2.队长用户上线，在组队通知中查到所有组队申请，组队回执的确认和加入其他队伍的回执
    //4.用户A上线，在组队通知中查到所有组队申请，组队回执的确认和加入其他队伍的回执
    @RequestMapping("/getApplyMsg")
    public Object getApplyMsg(@RequestParam String sender_email,@RequestParam String newest_id,@RequestParam String oldest_id,@RequestParam String goal ){
        try {
            if(sender_email == null || newest_id == null || oldest_id == null || goal == null){
                return databus.setResponse(401, "参数不全");
            }
            String g = goal.equals(getNew)? ">":"<";
            String i = goal.equals(getNew)? newest_id:oldest_id;
            String sql="select * from apply_stream where (response_id in (select id from apply_stream where sender=\""+sender_email+"\" or sender like '%\"cap\":\""+sender_email+"\"%') or (type=0 and group_id in (select id from news_info_stream where sender=\""+sender_email+"\" or sender like '%\"cap\":\""+sender_email+"\"%'))) and id"+g+i+" order by id desc limit "+databus.RESPONSE_MAX_DYNAMICS_NUMBER;
            List result = jdbcTemplate.queryForList(sql);
            return  databus.setResponse(result);
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
    //3.队长用户提交回执  status=2为同意 1为拒绝
    @RequestMapping("/sendReply")
    public Object sendReply(@RequestParam String sender_email,@RequestParam String response_id,@RequestParam String group_id,@RequestParam String message,@RequestParam String status){
        try{
            if(sender_email == null || response_id ==null || group_id == null || message ==null){
                return databus.setResponse(401, "参数不全");
            }
            if(databus.stringIllegal(message)){
                return  databus.setResponse(403,"参数非法");
            }
            String sql_updateApplyStatus = "update apply_stream set status = ? where id=?";
            jdbcTemplate.update(sql_updateApplyStatus,new Object[]{status,response_id});

            String sql_insertReply = "insert into apply_stream(group_id,response_id,message,sender,type) values(?,?,?,?,?)";
            jdbcTemplate.update(sql_insertReply,new Object[]{group_id,response_id,message,sender_email,1});

            return databus.setResponse("提交成功");

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
    //5.用户A提交回执确认信息 status=2为同意 1为拒绝
    @RequestMapping("/joinGroup")
    public Object joinAndConfirmGroup(@RequestParam String group_id, @RequestParam String sender_email,@RequestParam String response_id,@RequestParam String status,@RequestParam String message){
        try {
            String sql_updateResponse="update apply_stream set status=? where id=?";
            jdbcTemplate.update(sql_updateResponse,new Object[]{status,response_id});
            if(status.equals("1")){
                return databus.setResponse("拒绝成功");
            }

            String sql_get_old_id = "select group_id from userinfo where email=?";//原先有的小组id
            String sql_get_old_emails = "select member_emails from groupinfo where id=? and size>member_num";//小组原先有的成员
            try {
                //先拿到目前最大id，方便之后的插入时直接将id值填入main和response属性
                String old_id = jdbcTemplate.queryForObject(sql_get_old_id, String.class, sender_email);
                String new_id =  old_id==""?group_id:old_id+","+group_id;//处理之前是否有小组的两种情况
                String old_member_emails=null;
                try {
                    old_member_emails = jdbcTemplate.queryForObject(sql_get_old_emails, String.class, group_id);
                }catch (EmptyResultDataAccessException e){
                    System.out.println(e.getMessage());
                    return databus.setResponse("太晚了，该小组已经满了");
                }


                String new_member_emails = old_member_emails+","+sender_email;
                String sql_update_id = "update userinfo set group_id=? where email=?";
                String sql_update_emails = "update groupinfo set member_emails=?,member_num=member_num+1 where id=?";
                jdbcTemplate.update(sql_update_id,new Object[]{new_id,sender_email});
                jdbcTemplate.update(sql_update_emails,new Object[]{new_member_emails,group_id});

                String sql_insertToStream = "insert into apply_stream(group_id,response_id,message,sender,type) values(?,?,?,?,?)";
                jdbcTemplate.update(sql_insertToStream,new Object[]{group_id,response_id,message,sender_email,2});

                return databus.setResponse("加入成功");

            } catch (DataAccessException e) {
                System.out.println(e.getMessage());
                return databus.setResponse(500, "信息存入失败,检查数据是否有误");
            }

        } catch (Exception e){
            System.out.println(e.getMessage());
            return  databus.setResponse(402,"未知错误");
        }

    }
}
