package task;

import app.FileMeta;
import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: xinyan
 * @data: 2022/07/18/20:53
 **/
@Data
public class FileScanner {
    // 当前扫描的文件个数
    private int fileNum;
    // 当前扫描的文件夹个数
    // 最开始扫描的根路径没有统计，因此初始化文件夹的个数为1，表示从根目录下开始进行扫描任务
    private int dirNum = 1;
    // 扫描的所有文件信息
    List<FileMeta> fileMetas = new ArrayList<>();

    public void scan(File filePath) {
        if (filePath == null) {
            return;
        }
        File[] files = filePath.listFiles();
        for (File file : files) {
            FileMeta meta = new FileMeta();
            if (file.isDirectory()) {
                //是文件夹 递归扫描
                setCommonFiled(file.getName(),file.getPath(),true,file.lastModified(), meta);
                fileMetas.add(meta);
                dirNum++;
                scan(file);
            } else {
                //是个文件
                setCommonFiled(file.getName(),file.getPath(),false,file.lastModified(), meta);
                //设置文件大小
                meta.setSize(file.length());
                fileMetas.add(meta);
                fileNum++;
            }
        }
    }

    private void setCommonFiled(String name, String path, boolean isDirectory, Long lastModified, FileMeta meta) {
        meta.setName(name);
        meta.setPath(path);
        meta.setDirectory(isDirectory);
        // file对象的lastModified是一个长整型，以时间戳为单位的
        meta.setLastModified(new Date(lastModified));
    }
}
