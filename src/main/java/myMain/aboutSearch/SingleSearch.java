package myMain.aboutSearch;

import myMain.databus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;


//对单个关键字的sql简单检索进行抽象，简化代码
@Component
public class SingleSearch {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public Object searchForList(String sql,String value){
        try{
            List result = jdbcTemplate.queryForList(sql,value);
            if(result.isEmpty()){
                return databus.setResponse(401,"不存在");
            }
            return databus.setResponse(result);
        }catch (DataAccessException e){
            System.out.println(e.getMessage());
            return databus.setResponse(500,"后台运行异常");
        }
    }
    public Object searchForList(String sql){
        try{
            List result = jdbcTemplate.queryForList(sql);
            if(result.isEmpty()){
                return databus.setResponse(401,"不存在");
            }
            return databus.setResponse(result);
        }catch (DataAccessException e){
            System.out.println(e.getMessage());
            return databus.setResponse(500,"后台运行异常");
        }
    }

}
