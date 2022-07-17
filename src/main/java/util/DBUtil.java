package util;

import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author: xinyan
 * @data: 2022/07/17/21:30
 **/
public class DBUtil {
    private volatile static DataSource DATASOURCE;

    private static DataSource getDataSource() {
        if (DATASOURCE == null) {
            synchronized (DBUtil.class) {
                if (DATASOURCE == null) {
                    // SQLite没有账户密码，只需要配置日期格式即可
                    SQLiteConfig config = new SQLiteConfig();
                    config.setDateStringFormat(Util.DATE_PATTERN);
                    DATASOURCE = new SQLiteDataSource(config);

                    // 配置数据源的URL是SQLite子类独有的方法，因此向下转型
                    ((SQLiteDataSource) DATASOURCE).setUrl(getUrl());
                }
            }
        }
        return DATASOURCE;
    }


    private static String getUrl() {
        String path = "/Users/ryan/Desktop/比特/MyEverything/target";
        String url = "jdbc:sqlite://" + path + File.separator + "search_everything.db";
        System.out.println("获取数据库的连接为 : " + url);
        return url;
    }

    public static Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    public static void main(String[] args) throws SQLException {
        System.out.println(getConnection());
    }
}
