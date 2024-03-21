package fr.eni.encheres.Tools;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

public class FileUploadUtil {

    public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
        File fileUploadDir = new File(uploadDir);
        if (!fileUploadDir.exists()) {
            fileUploadDir.mkdirs();
        }
        File file = new File(uploadDir + File.separator + fileName);
        multipartFile.transferTo(file);
    }
}