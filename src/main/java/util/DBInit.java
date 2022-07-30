package util;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author: xinyan
 * @data: 2022/07/18/08:52
 **/
public class DBInit {

    public static List<String> readSQL() {
        List<String> ret = new ArrayList<>();

        try {
            // 采用类加载器的方式引入资源文件
            // JVM在加载类的时候用到的ClassLoader类
            InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("init.sql");
            Scanner sc = new Scanner(in);
            //自定义分隔符";"
            sc.useDelimiter(";");

            while (sc.hasNext()) {
                String str = sc.next();
                //如果是空格或换行符继续读取, 直到;
                if ("".equals(str) || "\n".equals(str)) {
                    continue;
                }
//                if (str.contains("--")) {
//                    str = str.replaceAll("--", "");
//                }
                ret.add(str);
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return ret;
    }

    public static void init() {
        Connection connection = null;
        Statement statement = null;

        try {
            connection = DBUtil.getConnection();
            List<String> sqls = readSQL();
            statement = connection.createStatement();
            for (String sql : sqls) {
                System.out.println("执行SQL操作 : " + sql);
                statement.executeUpdate(sql);
            }
        } catch (SQLException e) {
            System.out.println("数据库初始化失败");
            e.printStackTrace();
        } finally {
            DBUtil.close(statement);
        }
    }

    public static void main(String[] args) throws FileNotFoundException {

    }
}
