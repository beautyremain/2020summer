package myMain.aboutPy;

import myMain.databus;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Component
public class getPy {

    private static String[] pyOut(String[] args){
        try {
            String line = null;
            Process process = Runtime.getRuntime().exec(args);
            System.out.println(process);
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String[] lines = null;
            while ((line = in.readLine()) != null) {
                //line中存储的便是cmd中每一行的输出
                System.out.println(line);
                line = line.substring(1, line.length() - 1);
                lines = line.split(",");

                //System.out.println(typeof());
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
    private static Object getSearchResult(String keySentence){
        String Path=System.getProperty("user.dir")+ databus.PYTHON_PATH+"/search.py";
        String[] args = new String[]{"python",Path,keySentence};
        String[] ans = pyOut(args);
        return ans;

    }
    public static Object getAlgorithmResult(String type, String id){
        String Path=System.getProperty("user.dir")+ databus.PYTHON_PATH+"/Algorithm.py";
        String[] args = new String[] { "python", Path,type,id};

        //推荐组队
        String[] ans= pyOut(args);
        return ans;

    }
}
