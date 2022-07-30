package task;

import app.FileMeta;
import callback.FileScannerCallBack;
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

    private FileScannerCallBack callBack;
    public FileScanner(FileScannerCallBack callBack) {
        this.callBack = callBack;
    }

    public void scan(File filePath) {
        if (filePath == null) {
            return;
        }

        this.callBack.callback(filePath);
        File[] files = filePath.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                dirNum++;
                scan(file);
            } else {
                fileNum++;
            }
        }
    }
}
