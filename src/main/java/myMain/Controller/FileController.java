package myMain.Controller;

import myMain.databus;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
public class FileController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @RequestMapping(value = "/fileUpload",method = RequestMethod.POST)
    public Object getFile(@RequestParam MultipartFile file,@RequestParam String email, Model model) {
        if (file.isEmpty()||LoginController.stringIllegal(email)) {
            return null;
        }
        String fileName = file.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        String filePath = System.getProperty("user.dir")+databus.IMG_BASIC_PATH;
        fileName = UUID.randomUUID() + suffixName; // 新文件名
        File dest = new File(filePath + fileName);
        System.out.println("dest的绝对路径:"+dest.getAbsolutePath());
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String filename = "/temp-rainy/" + fileName;
        model.addAttribute("filename", filename);
        String  sql = "update userinfo set userpic=? where email=? ";
        //jdbcTemplate.queryForList(sql,new Object[]{filePath+fileName,email});
        if(jdbcTemplate.update(sql,new Object[]{databus.IMG_BASIC_PATH+fileName,email})>0){
            return databus.setResponse(0,"success");
        }
        else{
            return databus.setResponse(500,"fail to update into database");
        }

    }

    @RequestMapping(value = "/getfile",method = RequestMethod.GET, produces = "image/jpg")
    public Object responseFile(@RequestParam String email,Model model){
        String  sql = "select userpic from userinfo where email=?";
        Object picPath;
        try {
            picPath = jdbcTemplate.queryForObject(sql,Object.class,new Object[]{email});
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return databus.setResponse(401,"该用户不存在");
        }
        if(picPath==null){
            return databus.setResponse(403,"该用户没有头像");
        }
        File file = new File(System.getProperty("user.dir")+(String)picPath);
        model.addAttribute("email",email);
        return export(file);
    }
    private  ResponseEntity export(File file){
        if (file == null) {
            return null;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", "attachment; filename=" + file.getName());
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("Last-Modified", new Date().toString());
        headers.add("ETag", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok().headers(headers).contentLength(file.length()).contentType(MediaType.parseMediaType("application/octet-stream")).body(new FileSystemResource(file));
    }
}

