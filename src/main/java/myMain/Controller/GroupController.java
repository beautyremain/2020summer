package myMain.Controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import myMain.aboutPy.GetPy;
import myMain.aboutSearch.SingleSearch;
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


//组队有关的接口

@RestController
@RequestMapping("/group")
public class GroupController {
    @Autowired
    JdbcTemplate jdbcTemplate;

    //推荐队伍/个人
    @RequestMapping("/getRecommand/{type}")
    public Object getRecommand(@RequestParam String id, @PathVariable String type){
        String[] id_list=(String[]) GetPy.getAlgorithmResult(type,id);
        if(id_list == null){
            return  databus.setResponse("没有推荐结果");
        }
        String condition="";
        for(String each : id_list){
            condition+=each+",";
        }
        condition=condition.substring(0,condition.length()-1);
        String sql = "select * from groupinfo where id in("+condition+")";System.out.println(sql);
        List result=jdbcTemplate.queryForList(sql);
        return databus.setResponse(result,id_list);
    }


    //1.创建队伍
    @RequestMapping("/setgroup")
    public Object setGroup(@RequestParam String email,@RequestParam String info) {
        if (info.isEmpty()) {
            return databus.setResponse(401, "没有信息");
        }
        try {
            String sql = "insert into groupinfo(group_name,size,intend_comp,cap_email,member_emails,chara_point,ori_point) values(?,?,?,?,?,?,?)";
            JSONObject jsonObject = JSON.parseObject(info);
            String group_name = jsonObject.getString("group_name");
            String size = jsonObject.getString("size");
            String intend_comp = jsonObject.getString("intend_comp");
            String chara_point = jsonObject.getString("chara_point");

            if (group_name == null || size == null || intend_comp == null || chara_point == null) {
                return databus.setResponse(406, "参数不全");
            }
            if (
                    databus.stringIllegal(group_name)
                            || databus.stringIllegal(size)
                            || databus.stringIllegal(intend_comp)) {
                return databus.setResponse(403, "参数非法");
            }
            try{
                List res = jdbcTemplate.queryForList("select id from groupinfo where group_name=? and intend_comp=?",new Object[]{group_name,intend_comp});
                if(!res.isEmpty()){
                    return databus.setResponse(401,"该意向比赛中已经存在此名字的队伍");
                }

                jdbcTemplate.update(sql,new Object[]{group_name,size,intend_comp,email,email,chara_point,chara_point});

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
    //4.按队伍名+比赛查找队伍
    @RequestMapping("/search/bynm")
    public Object getGroupsByNM(@RequestParam String group_name,@RequestParam String comp){
       try{
           String sql = "select * from groupinfo where group_name=? and intend_comp=?";
           List list=jdbcTemplate.queryForList(sql,group_name,comp);
           return databus.setResponse(list);
       }        catch (EmptyResultDataAccessException e){
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
    //5.获得队伍信息,貌似重复了
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





}
