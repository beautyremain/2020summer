package myMain.aboutPy;

import myMain.databus;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class getPy {

    private static List<String> pyOut(String[] args){
        try {
            String line = null;
            Process process = Runtime.getRuntime().exec(args);
            System.out.println(process);
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            List<String> lines = new ArrayList<String>();
            while ((line = in.readLine()) != null) {
                //line中存储的便是cmd中每一行的输出
                System.out.println(line);
                lines.add(line);
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
    //智能搜索
    public static Object getSearchResult(String keySentence){
        String Path=System.getProperty("user.dir")+ databus.PYTHON_PATH+"/search.py";
        String[] args = new String[]{"python",Path,keySentence};
        List<String> ans = pyOut(args);
        String[] idArray = ans.get(0).substring(1,ans.get(0).length() - 1).split(",");
        return idArray;

    }
    //推荐组队
    public static Object getAlgorithmResult(String type, String id){
        String Path=System.getProperty("user.dir")+ databus.PYTHON_PATH+"/Algorithm.py";
        String[] args = new String[] { "python", Path,type,id};


        List<String> ans = pyOut(args);
        String[] idArray = ans.get(0).substring(1,ans.get(0).length() - 1).split(",");
        return idArray;

    }
}
