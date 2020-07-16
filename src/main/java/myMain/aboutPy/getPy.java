package myMain.aboutPy;

import myMain.databus;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Component
public class getPy {
    public static Object get(String type,String id){
        String Path=System.getProperty("user.dir")+ databus.PYTHON_PATH+"/Algorithm.py";
        String[] args = new String[] { "python", Path,type,id};

        //在机器学习推荐组队时接受python的输出，暂时还没有使用
        try {
            String line=null;
            Process process =Runtime.getRuntime().exec(args);
            System.out.println(process);
            BufferedReader in =new BufferedReader(new InputStreamReader(process.getInputStream()));
            System.out.println("get");
            String[] lines=null;
            while((line = in.readLine())!=null){
                //line中存储的便是cmd中每一行的输出
                System.out.println(line);
                line=line.substring(1,line.length()-1);
                lines=line.split(",");

                //System.out.println(typeof());
            }

            in.close();

            process.waitFor();
            process.destroy();
            System.out.println(lines[0]);
            //System.exit(0);
            return lines;
            //System.out.println(line);
        } catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }

    }
}
