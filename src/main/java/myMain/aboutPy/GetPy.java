package myMain.aboutPy;

import myMain.aboutHeat.HeatProcesser;
import myMain.databus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class GetPy {

    @Autowired
    HeatProcesser heatProcesser;
    public static String[] checkNumber(String[] words){
        List<String> ansList = new ArrayList<String>();
        for(int j=0;j<words.length;j++){
            int flag=0;
            char num[] = words[j].toCharArray();
            for(int i=0;i<words[j].length();i++){
                if (Character.isDigit(num[i])){//是数字返回True
                    flag++;
                }
            }
            if(flag<words[j].length()/2){
                ansList.add(words[j]);
            }
        }
        String[] strings = new String[ansList.size()];

        ansList.toArray(strings);
        return strings;
    }

    private static List<String> pyOut(String[] args){
        try {
            String line = null;
            Process process = Runtime.getRuntime().exec(args);
            for(String each:args){
                System.out.println("args:"+each);
            }
            System.out.println(process);
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream(),"GBK"));
            List<String> lines = new ArrayList<String>();
            while ((line = in.readLine()) != null) {
                //line中存储的便是cmd中每一行的输出
                System.out.println(line);
                lines.add(line.replace(" ",""));
            }
            in.close();
            process.waitFor();
            process.destroy();

            return lines;
        }catch(Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }
    //欧式距离
    public static String getRelativeDistance(String self_string,String other_string){
        String Path=System.getProperty("user.dir")+ databus.PYTHON_PATH+"/distanceJudge.py";
        String[] args = new String[]{"python",Path,self_string,other_string};
        for(String each:args){
            System.out.println("args:"+each);
        }
        String ans=pyOut(args).get(0);
        return ans;
    }
    //智能搜索
    public Object getSearchResult(String keySentence){
        String Path=System.getProperty("user.dir")+ databus.PYTHON_PATH+"/search.py";
        String[] args = new String[]{"python",Path,keySentence};
        List<String> ans = pyOut(args);

        String[] idArray = ans.get(1).substring(1,ans.get(1).length() - 1).split(",");
        String[] keyArray = ans.get(0).substring(1,ans.get(0).length()-1).split(",");
        for(int i=0;i<keyArray.length;i++){
            keyArray[i]=keyArray[i].substring(1,keyArray[i].length()-1);
        }
        heatProcesser.addHeatWordToDB(checkNumber(keyArray));
        if(ans.get(1).equals("[]")){
            return null;
        }
        return idArray;

    }
    //推荐组队
    public static Object getAlgorithmResult(String type, String id){
        String Path=System.getProperty("user.dir")+ databus.PYTHON_PATH+"/Algorithm.py";
        String[] args = new String[] { "python", Path,type,id};


        List<String> ans = pyOut(args);
        if(ans.get(0).equals("[]")){
            return null;
        }
        String[] idArray = ans.get(0).substring(1,ans.get(0).length() - 1).split(",");
        return idArray;

    }
}
