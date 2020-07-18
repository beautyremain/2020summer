package myMain.Controller;/*
    author: BeautyRemain(程季康)
    create time: 2020/7/18 18:23
*/

import myMain.aboutPy.GetPy;
import myMain.databus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/judge")
public class JudgeController {
    @Autowired
    JdbcTemplate jdbcTemplate;


    private List<String> calculateTrustAndJudgePoint(String new_radar_point,String old_radar_point,int now_sum,String self_radar_point){
        List<String> list=new ArrayList<>();
        String[] strings=old_radar_point.split(",");
        int[] old_number_array= Arrays.asList(strings).stream().mapToInt(Integer::parseInt).toArray();
        strings=new_radar_point.split(",");
        int[] new_number_array=Arrays.asList(strings).stream().mapToInt(Integer::parseInt).toArray();
        for(int i=0;i<new_number_array.length;i++){
            if(new_number_array[i]!=0)
                new_number_array[i]=(old_number_array[i]*(now_sum-1)+new_number_array[i])/now_sum;
            else
                new_number_array[i]=old_number_array[i];
        }
        String other_radar_point=Arrays.toString(new_number_array).substring(1,Arrays.toString(new_number_array).length()-1);
        list.add(other_radar_point);
        String ans=GetPy.getRelativeDistance(self_radar_point,other_radar_point);
        list.add(ans);
        return list;
    }


    @RequestMapping("/loadin")
    //载入流，并计算trust，judgepoint
    //group_id	sender_id	receiver_id	radar_point
    public Object judgementLoadIn(@RequestParam String group_id,@RequestParam String sender_id,@RequestParam String receiver_id,@RequestParam String radar_point){
        try{
            String sql_checkHistory="select * from judge_stream where group_id=? and sender_id=? and receiver_id=?";
            List check=jdbcTemplate.queryForList(sql_checkHistory,new Object[]{group_id,sender_id,receiver_id});
            if(!check.isEmpty()){
                return databus.setResponse(401,"已经评价过了");
            }
            String sql_insertStream="insert into judge_stream(group_id,sender_id,receiver_id,radar_point) values(?,?,?,?)";
            jdbcTemplate.update(sql_insertStream,new Object[]{group_id,sender_id,receiver_id,radar_point});
            String sql_getJudgeCount = "select radar_point,judge_point,judge_count from userinfo where id=?";
            List list=jdbcTemplate.queryForList(sql_getJudgeCount,receiver_id);
            LinkedCaseInsensitiveMap map=(LinkedCaseInsensitiveMap)list.get(0);
            if(map.get("judge_point")==null||map.get("judge_point")==""){
                String ans=GetPy.getRelativeDistance((String) map.get("radar_point"),radar_point);
                String sql_updateUserInfo="update userinfo set judge_point=?,judge_count=judge_count+1,trust=? where id=?";
                jdbcTemplate.update(sql_updateUserInfo,new Object[]{radar_point,ans,receiver_id});
            }
            else{
                List<String> ans=calculateTrustAndJudgePoint(radar_point,(String)map.get("judge_point"),(Integer)map.get("judge_count")+1,(String)map.get("radar_point"));
                String judge_point=ans.get(0);
                String trust=ans.get(1);
                String sql_updateUserInfo="update userinfo set judge_point=?,judge_count=judge_count+1,trust=? where id=?";
                jdbcTemplate.update(sql_updateUserInfo,new Object[]{judge_point,trust,receiver_id});
            }
            return databus.setResponse("上传成功");

        }catch(Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
            return databus.setResponse(402,"未知错误");
        }
    }

    //查看流，
    @RequestMapping("/getpoints")
    public Object getPoints(@RequestParam String id){
        try{
            String sql="select radar_point from judge_stream where receiver_id=? order by id desc limit 5";
            List list=jdbcTemplate.queryForList(sql,id);
            return databus.setResponse(list);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
            return databus.setResponse(402,"未知错误");
        }
    }
}
