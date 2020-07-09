package myMain.Controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import myMain.databus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RequestMapping("/userInfo")
@RestController
public class UserInfoController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @RequestMapping("/getLabels")
    public Object getLabels(){
        JSONObject jsonObject = new JSONObject();
        try {
            String sql = "select label_name,field from label_info";
            List result = jdbcTemplate.queryForList(sql);
            return databus.setResponse(result);
        } catch (Exception e){
            return databus.setResponse(402,"未知错误");
        }
    }

    @RequestMapping("/setLabels")
    public Object setLabels(@RequestParam String email,@RequestParam String labels){
        try {
            String sql = "update userinfo set label=? where email=?";
            try {
                jdbcTemplate.update(sql,new Object[]{labels,email});
                return databus.setResponse("标签上传成功");

            } catch (DataAccessException e) {
                System.out.println(e.getMessage());
                return databus.setResponse(500, "数据上传失败");
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return  databus.setResponse(402,"未知错误");
        }

    }
    @RequestMapping("/setinfo")
    public Object setInfoDetail(@RequestParam String email,@RequestParam String info){
        if(info.isEmpty()){
            return databus.setResponse(401,"没有信息");
        }
        try {
            String sql = "update userinfo set school=?,grade=?,career=?,sno=? where email=?";
            JSONObject jsonObject = JSON.parseObject(info);
            String school = jsonObject.getString("school");
            String grade = jsonObject.getString("grade");
            String career = jsonObject.getString("career");
            String sno = jsonObject.getString("sno");
            if(school == null || grade == null || career == null || sno == null ){
                return databus.setResponse(406,"参数不全");
            }
            if(
                    LoginController.stringIllegal(school)
                            ||LoginController.stringIllegal(grade)
                            ||LoginController.stringIllegal(career)
                            ||LoginController.stringIllegal(sno)){
                return databus.setResponse(403,"参数非法");
            }
            try {
                jdbcTemplate.update(sql, new Object[]{school,grade,career,sno,email});
                return  databus.setResponse("上传成功");
            }   catch(DataAccessException e){
                System.out.println(e.getMessage());
                return databus.setResponse(500,"信息存入失败");
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return  databus.setResponse(402,"未知错误");
        }

    }

    @RequestMapping("/search/bylabels")
    public Object searchUserByLabel(@RequestParam String labels) {
        try {
            String[] labelList = labels.split(",");
            int len = labelList.length;
            String sql = "select * from userinfo where ";
            for (int i = 0; i < len - 1; i++) {
                sql += "label like '%" + labelList[i] + "%' and ";
            }
            sql += "label like '%" + labelList[len - 1] + "%'";
            try{
                List list=jdbcTemplate.queryForList(sql);
                return databus.setResponse(0,list);
            }catch (DataAccessException e){
                System.out.println(e.getMessage());
                return databus.setResponse(500,"后台运行异常");
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
            return databus.setResponse(402,"未知错误");
        }

    }

    @RequestMapping("/search/byemail")
    public Object searchUserByEmail(@RequestParam String email){
        try{
            String  sql = "select * from userinfo where email=?";
            try{
                List result = jdbcTemplate.queryForList(sql,email);
                return databus.setResponse(result);
            }catch (DataAccessException e){
                System.out.println(e.getMessage());
                return databus.setResponse(500,"后台运行异常");
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            return databus.setResponse(402,"未知错误");
        }
    }
}
