package myMain.Controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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

@RequestMapping("/news")
@RestController
public class NewsController {
    @Autowired
    JdbcTemplate jdbcTemplate;

    //1.普通动态的发布 0
    //2.招募信息的发布 1
    //3.答疑信息的发布 2
    @RequestMapping("/sendnews/{type}")
    public Object sendDynamic(@RequestParam String sender,@ RequestParam String message,@PathVariable String type){
        try {

            if(sender ==null || type == null){
                return databus.setResponse(400, "缺少动态类型信息");
            }
            if (message == null) {
                return databus.setResponse(401, "没有输入信息");
            }
            if(message == null || sender == null){
                return databus.setResponse(405,"输入信息不全");
            }
            if(databus.stringIllegal(message)){
                return databus.setResponse(403,"输入信息非法");
            }
            String sql_insert = "insert into news_info_stream(main_id,response_id,message,sender,type) values(?,?,?,?,?)";
            String sql_getId = "select max(id) from news_info_stream";//取出最大id
            try{
                int old_id=jdbcTemplate.queryForObject(sql_getId,Integer.class);
                String new_id= Integer.toString(old_id+1);
                jdbcTemplate.update(sql_insert,new Object[]{new_id,new_id,message,sender,type});
                return databus.setResponse("发布成功,id为:"+new_id);
            } catch (DataAccessException e){
                System.out.println(e.getMessage());
                return databus.setResponse(501,"信息处理失败");
            }


        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return databus.setResponse(402,"未知错误："+e.getMessage());
        }
    }



    //5.关注用户的动态展示 此处内容需要逐步展示，，按照时间倒序排列好后 每次返回n个动态内容，并记录最后一个返回的动态的id  goal : getNew:0,getHistory:1
    private  static final String getNew="0";
    private  static final  String getHistory="1";


    @RequestMapping("/getattention")
    public Object getAttentionDynamics(@RequestParam String sender_email,@RequestParam String newest_id,@RequestParam String oldest_id,@RequestParam String goal){
        try{
            if(goal == null || sender_email ==null){
                return databus.setResponse(400, "缺少动态类型信息");
            }
            if(newest_id == null || oldest_id == null ){
                return databus.setResponse(401, "输入信息不全");
            }
            //取出该用户关注名单
                String sql_getAttentions = "select attention from userinfo where email=?";
                String Attentions = jdbcTemplate.queryForObject(sql_getAttentions, String.class, sender_email);
                String[] AttenList=Attentions.toString().split(",");

                //拼接关注名单作为之后的检索条件
                String conditionStr="(";
                for(int i=0;i<AttenList.length-1;i++){
                    conditionStr += " sender='"+AttenList[i]+"' or";
                }
                conditionStr += " sender='"+AttenList[AttenList.length-1]+"' )";
                //分类查询
                if(goal.equals(getNew)) {
                    String sql_getDynamics = "select * from news_info_stream where id>? and type='0' and "+conditionStr+"order by id desc limit 10";
                    System.out.println(sql_getDynamics);
                    List result = jdbcTemplate.queryForList(sql_getDynamics,newest_id);
                    return databus.setResponse(result);
                }
                else if(goal.equals(getHistory)){
                    String sql_getDynamics = "select * from news_info_stream where id<? and type='0' and "+conditionStr+"order by id desc limit 10";
                    System.out.println(sql_getDynamics);
                    List result = jdbcTemplate.queryForList(sql_getDynamics,oldest_id);
                    return databus.setResponse(result);
                }
                else {
                    return databus.setResponse(401, "目标有误");
                }
        }
        catch (EmptyResultDataAccessException e){
            return databus.setResponse(405,"该用户没有关注");
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

    //6.全部动态的展示 细节同上 goal : getNew:0,getHistory:1

    @RequestMapping("/getnews/{type}")
    //1.普通动态 0
    //2.招募动态 1
    //3.答疑动态 2
    public Object getDynamics(@RequestParam String sender_email,@RequestParam String newest_id,@RequestParam String oldest_id,@RequestParam String goal,@PathVariable String type){
        try{
            if(sender_email ==null || type == null || goal == null){
                return databus.setResponse(400, "缺少动态类型信息");
            }
            if (newest_id == null || oldest_id == null ) {
                return databus.setResponse(401, "输入信息不全");
            }
            try {
                //分类检索
                if (goal.equals(getNew)) {
                    String sql = "select * from news_info_stream where id>? and type=? order by id desc limit ?";
                    List result = jdbcTemplate.queryForList(sql,new Object[]{newest_id,type,databus.RESPONSE_MAX_DYNAMICS_NUMBER});
                    return databus.setResponse(result);
                }
                else if(goal.equals(getHistory)){
                    String sql = "select * from news_info_stream where id<? and type=? order by id desc limit ?";
                    List result = jdbcTemplate.queryForList(sql,new Object[]{oldest_id,type,databus.RESPONSE_MAX_DYNAMICS_NUMBER});
                    return databus.setResponse(result);
                }
                else{
                    return databus.setResponse(401,"目标有误:"+goal);
                }
            }catch (DataAccessException e){
                System.out.println(e.getMessage());
                return databus.setResponse(501,"信息处理失败");
            }

        }        catch (Exception e){
            System.out.println(e.getMessage());
            return databus.setResponse(402,"未知错误："+e.getMessage());
        }

    }



    //8.评论的回复
    //9.评论的发布
    //评论作为一种特殊类型的动态


    @RequestMapping("/send/comment")
    public Object sendComment(@RequestParam String sender_email,@ RequestParam String info){
        try {
            JSONObject jsonObject = JSON.parseObject(info);
            if (sender_email == null || info == null) {
                return  databus.setResponse(401,"参数不全");
            }
            String main_id = jsonObject.getString("main_id");
            String response_id = jsonObject.getString("response_id");
            String message = jsonObject.getString("message");
            if(main_id == null || response_id == null || message == null){
                return databus.setResponse(401, "输入信息不全");
            }
            String sql = "insert into news_info_stream(main_id,response_id,message,sender,type) values(?,?,?,?,?)";
            jdbcTemplate.update(sql,new Object[]{main_id,response_id,message,sender_email,"3"});
            return databus.setResponse("上传成功");
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
    //4.动态展示页到评论 && 单条评论到动态
    @RequestMapping("/get/comment")
    public Object getComment(@RequestParam String main_id){
        try{
            if(main_id == null){
                return  databus.setResponse(401,"参数不全");
            }
            String sql = "select * from news_info_stream where main_id=?";
            List result = jdbcTemplate.queryForList(sql,main_id);
            return result;
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

    //8.评论的删除
    //9.动态的删除 type: comment/dynamic
    @RequestMapping("/delete/{type}")
    public Object deleteNews(@PathVariable String type,@RequestParam String id){
        try{
            if(type == null || id == null){
                return databus.setResponse(401, "参数不全");
            }
            if(type == "comment"){
                String sql = "update news_info_stream set isdeleted=1 where id=?";
                jdbcTemplate.update(sql,id);
                return databus.setResponse("删除成功");
            }
            else if(type == "dynamic"){
                String sql = "delete from news_info_stream where main_id=?";
                jdbcTemplate.update(sql,id);
                return databus.setResponse("删除成功");
            }
            else{
                return databus.setResponse("参数有误");
            }
        }
        catch (EmptyResultDataAccessException e){
            return databus.setResponse(405,"不存在该信息id");
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
    //动态点赞 1为点赞通知
    @RequestMapping("/send/like")
    public Object sendLike(@RequestParam String sender_email,@RequestParam String response_id,@RequestParam String brief_msg){
        try{
            if(sender_email == null || response_id == null || brief_msg == null) {
                return databus.setResponse(401, "参数不全");
            }
            String sql_updateNews = "update news_info_stream set likes=likes+1 where id =?";
            jdbcTemplate.update(sql_updateNews,response_id);
            String sql_insertStream = "insert into inform_table(response_id,sender,message,type) values(?,?,?,?)";
            jdbcTemplate.update(sql_insertStream,new Object[]{response_id,sender_email,brief_msg,"1"});
            return databus.setResponse("点赞成功");
        }
        catch (EmptyResultDataAccessException e){
            return databus.setResponse(405,"不存在该信息id");
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
    /*
    author: 郑文宏
    create time: 2020/7/16 15:50
    */
    //取消点赞
    @RequestMapping("/send/dislike")
    public Object dislike(@RequestParam String sender_email,@RequestParam String response_id){
        try{
            if(sender_email == null || response_id == null) {
                return databus.setResponse(401, "参数不全");
            }
            String sql_updateNews = "update news_info_stream set likes=likes-1 where id =?";
            jdbcTemplate.update(sql_updateNews,response_id);
            String sql_insertStream = "delete from inform_table where response_id=? and type=? and sender=?";
            jdbcTemplate.update(sql_insertStream,new Object[]{response_id,"1",sender_email});
            return databus.setResponse("取消点赞成功");
        }
        catch (EmptyResultDataAccessException e){
            return databus.setResponse(405,"不存在该信息id");
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
