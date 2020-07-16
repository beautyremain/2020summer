package myMain.aboutHeat;/*
    author: BeautyRemain(程季康)
    create time: 2020/7/16 19:01
*/

import myMain.databus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class HeatProcesser {
    @Autowired
    JdbcTemplate jdbcTemplate;
    public void addCompetitionHeatByName(String compname){
        String sql = "update competitioninfo set heat_number=heat_number+"+databus.HEAT_PER_PRESON+" where competname=?";
        jdbcTemplate.update(sql,compname);
    }
    public void addCompetitionHeatById(String id){
        String sql = "update competitioninfo set heat_number=heat_number+"+databus.HEAT_PER_PRESON+" where id=?";
        jdbcTemplate.update(sql,id);
    }
    public void addHeatWordToDB(String[] words){
        String values="";
        String sql = "insert into search_key_stream(keyword) values ";
        for(String word : words){
            values += "('"+word+"'),";
            //System.out.println("word:"+word);
        }
        values = values.substring(0,values.length()-1);
        sql += values;
        try {
            jdbcTemplate.update(sql);
        }catch (Exception e){
            System.out.println(sql);
            System.out.println(e.getMessage());
        }
    }


}
