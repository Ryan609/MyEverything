package util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: xinyan
 * @data: 2022/07/17/21:36
 **/
public class Util {
    public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static String parseSize(Long size) {
        String[] unit = {"B","KB","MB","GB"};
        int flag = 0;
        if (size > 1024) {
            size /= 1024;
            flag ++;
        }
        return size + unit[flag];
    }

    public static String parseFileType(Boolean directory) {
        return directory ? "文件夹" : "文件";
    }

    public static String parseDate(Date lastModified) {
        return new SimpleDateFormat(DATE_PATTERN).format(lastModified);
    }
}
