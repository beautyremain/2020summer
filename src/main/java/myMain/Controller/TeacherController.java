package myMain.Controller;/*
    author: BeautyRemain(程季康)
    create time: 2020/7/17 14:34
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

@RestController
@RequestMapping("/teacher")
public class TeacherController {
    @Autowired
    JdbcTemplate jdbcTemplate;
    //登陆检查
    @RequestMapping("/checkId")
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
    //注册时加入老师表
    @RequestMapping("/addTeacher")
    public Object addToTeacher(@RequestParam String userId){
        try{
            String sql="insert into teacher_list(user_id) values(?)";
            jdbcTemplate.update(sql,userId);
            return databus.setResponse("成功");
        }
        catch (EmptyResultDataAccessException e){
            return databus.setResponse(405,"empty set error");
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
    //查找老师信息，使用userid
    @RequestMapping("/search/teacher")
    public Object searchTeacher(@RequestParam String userId){
        try {
            String sql="select * from teacher_list where user_id=?";
            List list = jdbcTemplate.queryForList(sql,userId);
            if(list.isEmpty()){
                return databus.setResponse("不存在该教师用户");
            }
            return databus.setResponse(list);
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
    //老师发送比赛通知
    @RequestMapping("/send/inform")
    public Object sendCompetitionInform(@RequestParam String comp_name,@RequestParam String message){
        try{
            if(message == "" || comp_name == ""){
                return databus.setResponse(401,"信息不全");
            }
            String sql="insert into inform_table(sender,message,type) values(?,?,2)";
            jdbcTemplate.update(sql,comp_name,message);
            return databus.setResponse("发布成功");
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
    //老师修改比赛信息
    @RequestMapping("/edit/comp")
    public Object editCompetitionInfo(@RequestParam String comp_name,@RequestParam String competstate,@RequestParam String detail){
        try{
            String sql="update competitioninfo set competstate=?,detail=? where competname=?";
            jdbcTemplate.update(sql,new Object[]{competstate,detail,comp_name});
            //使已参加比赛的小组变成进行中，没报名的作废
            if(competstate.equals("2")){
                String sql_updateGroupFlag="update groupinfo set sign_state=2 where sign_state=1 and intend_comp=?";
                jdbcTemplate.update(sql_updateGroupFlag,comp_name);
                sql_updateGroupFlag="update groupinfo set sign_state=-1 where sign_state=0 and intend_comp=?";
                jdbcTemplate.update(sql_updateGroupFlag,comp_name);
            }
            //使进行中的变成结束
            if(competstate.equals("3")){
                String sql_updateGroupFlag="update groupinfo set sign_state=3 where sign_state=2 and intend_comp=?";
                jdbcTemplate.update(sql_updateGroupFlag,comp_name);
            }
            return databus.setResponse("修改成功");
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
    //学生邀请老师
    @RequestMapping("/send/invite")
    public Object sendInvitationToTeacher(@RequestParam String teacher_id,@RequestParam String group_id,@RequestParam String message){
        try{
         String sql="insert into invite_stream(group_id,teacher_id,message) values(?,?,?)";
         jdbcTemplate.update(sql,new Object[]{group_id,teacher_id,message});
         return databus.setResponse("邀请成功");

        }        catch (DataAccessException e){
            System.out.println(e.getMessage());
            return databus.setResponse(501,"信息处理失败");
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return databus.setResponse(402,"未知错误："+e.getMessage());
        }
    }
    //老师查看邀请,学生查看回执放在了inform里


    //老师写回执 status:0为未处理，2为拒绝，1为同意
    @RequestMapping("/reply/invite")
    public Object replyInvitation(@RequestParam String teacher_id,@RequestParam String id,@RequestParam String message,@RequestParam String state){
        try{
            String sql_updateStream="update invite_stream set reply=?,state=? where id=?";
            jdbcTemplate.update(sql_updateStream,new Object[]{message,state,id});
            String sql_updateGroupInfo="update groupinfo set teacher=? where id in (select group_id from invite_stream where id=?)";
            jdbcTemplate.update(sql_updateGroupInfo,new Object[]{teacher_id,id});
            String group_id=jdbcTemplate.queryForObject("select group_id from invite_stream where id=?",String.class,id);
            String oldGroupIds="";
            String newGroupIds="";
            try {
                oldGroupIds = jdbcTemplate.queryForObject("select group_ids from teacher_list where user_id=?", String.class, teacher_id);
                newGroupIds=databus.updateKeyword(oldGroupIds,group_id);
            }catch (EmptyResultDataAccessException e){
                newGroupIds=group_id;
                System.out.println(e.getMessage());
            }
            String sql_updateTeacherList= "update teacher_list set group_ids=? where user_id=?";
            jdbcTemplate.update(sql_updateTeacherList,new Object[]{newGroupIds,teacher_id});
            return databus.setResponse("回复成功");
        }catch (DataAccessException e){
            System.out.println(e.getMessage());
            return databus.setResponse(501,"信息处理失败");
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return databus.setResponse(402,"未知错误："+e.getMessage());
        }
    }
    //老师申请成为负责老师
    @RequestMapping("/apply/comp")
    public Object applyCompetition(@RequestParam String userId,@RequestParam String comp_name){
        try{
            String sql_getOld="select comp_names from teacher_list where user_id=?";
            String old="";
            String newWord="";
            try {
                 old = jdbcTemplate.queryForObject(sql_getOld, String.class, userId);
                 newWord=databus.updateKeyword(old,comp_name);
            }catch (EmptyResultDataAccessException e) {
                newWord = comp_name;
            }
            String sql="update teacher_list set comp_names=? where user_id=?";
            jdbcTemplate.update(sql,new Object[]{newWord,userId});
            return databus.setResponse("申请成功");
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
