package task;

import app.FileMeta;
import util.DBUtil;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: xinyan
 * @data: 2022/07/25/10:39
 **/
public class FileSearch {
    public static List<FileMeta> search(String dir, String content) {
        List<FileMeta> result = new ArrayList<>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DBUtil.getConnection();
            // 先根据用户选择的文件夹dir查询内容
            String sql = "select name,path,size,is_directory,last_modified from file_meta " +
                    " where (path = ? or path like ?)";

            if (content != null && content.trim().length() != 0) {
                sql += " and (name like ? or pinyin like ? or pinyin_first like ?)";
            }
            ps = connection.prepareStatement(sql);
            ps.setString(1,dir);
            ps.setString(2,dir + File.separator + "%");

            // 根据搜索框的内容查询数据库，都是模糊匹配
            if (content != null && content.trim().length() != 0) {
                ps.setString(3,"%" + content + "%");
                ps.setString(4,"%" + content + "%");
                ps.setString(5,"%" + content + "%");
            }
            System.out.println("正在从数据库中检索信息,sql为:" + ps);
            rs = ps.executeQuery();

            while (rs.next()) {
                FileMeta meta = new FileMeta();
                meta.setName(rs.getString("name"));
                meta.setPath(rs.getString("path"));
                meta.setIsDirectory(rs.getBoolean("is_directory"));
                if (!meta.getIsDirectory()) {
                    // 是文件，保存大小
                    meta.setSize(rs.getLong("size"));
                }
                meta.setLastModified(new Date(rs.getTimestamp("last_modified").getTime()));
                System.out.println("检索到文件信息 : name = " + meta.getName() + ",path = " + meta.getPath());
                result.add(meta);
            }
        } catch (SQLException e) {
            System.err.println("从数据库中搜索用户查找内容时出错，请检查SQL语句");
            e.printStackTrace();
        } finally {
            DBUtil.close(ps,rs);
        }
        return result;
    }
}
