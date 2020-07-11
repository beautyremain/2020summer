package myMain.Controller;

import myMain.databus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/file")
public class FileController {

    //文件上传





    @Autowired
    JdbcTemplate jdbcTemplate;
    //type: imgIcon\imgDynamic
    @RequestMapping(value = "/ImgUpload/{type}",method = RequestMethod.POST)
    public Object getFile(@RequestParam MultipartFile file, @RequestParam String target, Model model, @PathVariable String type) {
        if (file.isEmpty()||LoginController.stringIllegal(target)) {
            return null;
        }
        String fileName = file.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        String path_dir = type.equals("imgIcon")? databus.ICON_IMG_BASIC_PATH:databus.DYNAMIC_IMG_BASIC_PATH;
        String filePath = System.getProperty("user.dir")+path_dir;
        fileName = UUID.randomUUID() + suffixName; // 新文件名
        File dest = new File(filePath +""+fileName);
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


        String  sql = type.equals("imgIcon")?"update userinfo set userpic=? where email=? ":"update news_info_stream set pic_name=? where id=? ";
        //jdbcTemplate.queryForList(sql,new Object[]{filePath+fileName,email});
        try {
            jdbcTemplate.update(sql,new Object[]{fileName,target});
            return databus.setResponse(0,"success");
        }catch (DataAccessException e){
            System.out.println(e.getMessage());
            return databus.setResponse(500,"fail to update into database");
        }
    }

    @RequestMapping(value = "/ImgDownload/{type}",method = RequestMethod.GET, produces = "image/jpg")
    public Object responseFile(@RequestParam String imgName,@PathVariable String type){
        try {
            String path_dir = type.equals("imgIcon") ? databus.ICON_IMG_BASIC_PATH : databus.DYNAMIC_IMG_BASIC_PATH;
            File file = new File(System.getProperty("user.dir") + path_dir + imgName);
            return export(file);
        } catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
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

