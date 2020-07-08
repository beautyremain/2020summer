package myMain.aboutMail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class VerifyCodeMailSender {
    @Autowired
    JavaMailSenderImpl mailSender;
    String emailServiceCode;
    public static String getRandomCode(int len){
        String str = "90GABHO24I63WXJ1YZ78KLMCDEN5PSTFUQRV";
        String code = "";
        for(int i= 0;i<len;i++){
            int index = (int)(Math.random()*str.length());
            code+=str.charAt(index);
        }
        return code;
    }
    public void sendCodeToMail(String receiver_name){
        emailServiceCode = getRandomCode(6);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("注册验证码");
        message.setText("注册的验证码是："+emailServiceCode);
        message.setTo(receiver_name);
        message.setFrom("3391436581@qq.com");
        System.out.println(mailSender);
        mailSender.send(message);
    }
}
