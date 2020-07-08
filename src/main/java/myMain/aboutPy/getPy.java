package myMain.aboutPy;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class getPy {
    public static void get(){
        String[] args = new String[] { "python", "F:\\HelloWorld.py"};
        try {
            String line=null;
            Process process =Runtime.getRuntime().exec(args);
            BufferedReader in =new BufferedReader(new InputStreamReader(process.getInputStream()));
            while((line = in.readLine())!=null){
                //line中存储的便是cmd中每一行的输出
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
