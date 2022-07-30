package app;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import util.Util;

import java.util.Date;

/**
 * @author xinyan
 * @date 2022/07/06 15:08
 **/
@Data
@NoArgsConstructor
@ToString
public class FileMeta {
    private String name;
    private String path;
    private Boolean isDirectory;
    private Long size;
    private Date lastModified;
    // 若包含中文名称，名称全拼
    private String pinYin;
    // 拼音首字母
    private String pinYinFirst;

    // 以下三个属性需要在界面中展示，将当前属性值做处理之后展示
    // 这些属性名要和app.fxml中保持一致
    // 文件类型
    private String isDirectoryText;
    // 文件大小
    private String sizeText;
    // 上次修改时间
    private String lastModifiedText;

    public void setSize(Long size) {
        this.size = size;
        this.sizeText = Util.parseSize(size);
    }

//    public void setDirectory(Boolean directory) {
//        isDirectory = directory;
//        this.isDirectoryText = Util.parseFileType(directory);
//    }
    public void setIsDirectory(Boolean directory) {
        isDirectory = directory;
        this.isDirectoryText = Util.parseFileType(directory);
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
        this.lastModifiedText = Util.parseDate(lastModified);
    }

    public FileMeta(String name, String path, Boolean isDirectory, Long size, Date lastModified) {
        this.name = name;
        this.path = path;
        this.isDirectory = isDirectory;
        this.size = size;
        this.lastModified = lastModified;
    }

}