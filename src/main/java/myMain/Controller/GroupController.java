package myMain.Controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import myMain.aboutSearch.SingleSearch;
import myMain.databus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


//组队有关的接口

@RestController
@RequestMapping("/group")
public class GroupController {
    @Autowired
    JdbcTemplate jdbcTemplate;
    //1.创建队伍
    @RequestMapping("/setgroup")
    public Object setGroup(@RequestParam String email,@RequestParam String info) {
        if (info.isEmpty()) {
            return databus.setResponse(401, "没有信息");
        }
        try {
            String sql = "insert into groupinfo(group_name,size,intend_comp,cap_email,member_emails) values(?,?,?,?,?)";
            JSONObject jsonObject = JSON.parseObject(info);
            String group_name = jsonObject.getString("group_name");
            String size = jsonObject.getString("size");
            String intend_comp = jsonObject.getString("intend_comp");

            if (group_name == null || size == null || intend_comp == null) {
                return databus.setResponse(406, "参数不全");
            }
            if (
                    databus.stringIllegal(group_name)
                            || databus.stringIllegal(size)
                            || databus.stringIllegal(intend_comp)) {
                return databus.setResponse(403, "参数非法");
            }
            try{
                List res = jdbcTemplate.queryForList("select * from groupinfo where group_name=? and intend_comp=?",new Object[]{group_name,intend_comp});
                if(!res.isEmpty()){
                    return databus.setResponse(401,"该意向比赛中已经存在此名字的队伍");
                }

                jdbcTemplate.update(sql,new Object[]{group_name,size,intend_comp,email,email});

            } catch (DataAccessException e){
                System.out.println(e.getMessage());
                return  databus.setResponse(500,"信息存入失败");
            }
            return databus.setResponse("创建队伍成功");
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return  databus.setResponse(402,"未知错误");
        }
    }

    @Autowired
    SingleSearch singleSearch;
    //2.按队伍id查找队伍
   @RequestMapping("/search/byid")
   public Object getGroupById(@RequestParam String group_id){
        try{
            String sql = "select * from groupinfo where id=?";
            return singleSearch.searchForList(sql,group_id);
        }        catch (Exception e){
            System.out.println(e.getMessage());
            return  databus.setResponse(402,"未知错误");
        }
   }
    //3.按队伍名查找队伍
    @RequestMapping("/search/byname")
    public Object getGroupsByName(@RequestParam String group_name){
        try{
            String sql = "select * from groupinfo where group_name=?";
            return singleSearch.searchForList(sql,group_name);
        }        catch (Exception e){
            System.out.println(e.getMessage());
            return  databus.setResponse(402,"未知错误");
        }
    }

    //4.获得队伍信息,貌似重复了
    @RequestMapping("/getInfo")
    public Object getGroupInfo(@RequestParam String group_id){
        try{
            String sql = "select * from groupinfo where id=?";
            return singleSearch.searchForList(sql,group_id);
        }   catch (Exception e){
            System.out.println(e.getMessage());
            return  databus.setResponse(402,"未知错误");
        }
    }



    //5.加入队伍
    @RequestMapping("/joinTeam")
    public Object joinTeam(@RequestParam String group_id,@RequestParam String email){
        try {
            String sql_get_old_id = "select group_id from userinfo where email=?";
            String sql_get_old_emails = "select member_emails from groupinfo where id=? and size>member_num";
            try {

                //先拿到目前最大id，方便之后的插入时直接将id值填入main和response属性
                String old_id = jdbcTemplate.queryForObject(sql_get_old_id, String.class, email);
                String new_id =  old_id==""?group_id:old_id+","+group_id;
                String old_member_emails = jdbcTemplate.queryForObject(sql_get_old_emails,String.class,group_id);


                String new_member_emails = old_member_emails+","+email;
                String sql_update_id = "update userinfo set group_id=? where email=?";
                String sql_update_emails = "update groupinfo set member_emails=?,member_num=member_num+1 where id=?";
                jdbcTemplate.update(sql_update_id,new Object[]{new_id,email});
                jdbcTemplate.update(sql_update_emails,new Object[]{new_member_emails,group_id});
            } catch (DataAccessException e) {
                System.out.println(e.getMessage());
                return databus.setResponse(500, "信息存入失败,检查数据是否有误");
            }
            return databus.setResponse("加入成功");
        } catch (Exception e){
            System.out.println(e.getMessage());
            return  databus.setResponse(402,"未知错误");
        }

    }


}
