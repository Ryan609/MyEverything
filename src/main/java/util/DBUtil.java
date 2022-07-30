package util;

import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.sql.*;

/**
 * @author: xinyan
 * @data: 2022/07/17/21:30
 **/
public class DBUtil {
    private volatile static DataSource DATASOURCE;

    private volatile static Connection CONNECTION;

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
        String path = "/Users/ryan/Desktop/Bit/MyEverything/target";
        String url = "jdbc:sqlite://" + path + File.separator + "search_everything.db";
        System.out.println("获取数据库的连接为 : " + url);
        return url;
    }

    public static Connection getConnection() throws SQLException {
        if (CONNECTION == null) {
            synchronized (DBUtil.class) {
                if (CONNECTION == null) {
                    CONNECTION = getDataSource().getConnection();
                }
            }
        }
        return CONNECTION;
    }

    public static void main(String[] args) throws SQLException {
        System.out.println(getConnection());
    }


    public static void close(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void close(PreparedStatement ps, ResultSet rs) {
        close(ps);
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
