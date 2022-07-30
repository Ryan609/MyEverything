package util;

/**
 * @author: xinyan
 * @data: 2022/07/05/14:43
 **/
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.Arrays;

/**
 * 写项目就是一个搭积木的过程，一部分一部分的完成，最终将每个独立的模块拼装在一起
 * 一般来说，项目都是从工具类或是数据库相关的操作开始写起
 * @author xinyan
 * @date 2022/07/04 21:12
 * 拼音工具类
 * 将汉语拼音的字符映射为字母字符串
 **/
public class PinyinUtil {
    // 定义汉语拼音的配置 全局常量，必须在定义时初始化，全局唯一
    // 这个配置就表示将汉字字符转为拼音字符串时的一些设置
    private static final HanyuPinyinOutputFormat FORMAT;

    private static final String CHINESE_PATTERN = "[\\u4E00-\\u9FA5]";

    // 代码块就是在进行一些项目配置的初始化操作
    static {
        // 当PinyinUtil类加载时执行静态块，除了产生对象外，还可以进行一些配置相关的工作
        FORMAT = new HanyuPinyinOutputFormat();
        // 设置转换后的英文字母为全小写 鹏 -> peng
        FORMAT.setCaseType(HanyuPinyinCaseType.LOWERCASE);
       // 设置转换后的英文字母是否带音调
        FORMAT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        // 特殊拼音用v替代 绿 -> lv
        FORMAT.setVCharType(HanyuPinyinVCharType.WITH_V);
    }

    /**
     * 传入任意的文件名称，就能将该文件名称转为字母字符串全拼和首字母小写字符串
     * eg : 文件名为 鹏哥真帅 =>
     * penggezhenshuai / pgzs
     * 若文件名中包含其他字符，英文数字等，不需要做处理，直接保存
     * eg : 铭哥yuisama123 =>
     * minggeyuisama123 / mgyuisama123
     * @param fileName
     * @return
     */
    public static String[] getPinyinByFileName(String fileName) {
        // 第一个字符串为文件名全拼
        // 第二个字符串为首字母
        String[] ret = new String[2];

        // 核心操作就是遍历文件名中的每个字符，碰到非中文直接保留，中文处理
        StringBuilder allNameAppender = new StringBuilder();
        StringBuilder firstCaseAppender = new StringBuilder();

        // fileName = 鹏哥c真帅
        // c = 鹏
        for (char c : fileName.toCharArray()) {
            // 不考虑多音字，就使用第一个返回值作为我们的参数
            try {
                String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(c,FORMAT);
                if (pinyins == null || pinyins.length == 0) {
                    // 碰到非中文字符，直接保留
                    allNameAppender.append(c);
                    firstCaseAppender.append(c);
                }else {
                    // 碰到中文字符，取第一个多音字的返回值 和 -> [he,huo,hu..]
                    allNameAppender.append(pinyins[0]);
                    // he -> h
                    firstCaseAppender.append(pinyins[0].charAt(0));
                }
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                // 碰到非中文字符，直接保留
                allNameAppender.append(c);
                firstCaseAppender.append(c);
            }
        }
        ret[0] = allNameAppender.toString();
        ret[1] = firstCaseAppender.toString();
        return ret;
    }

    public static void main(String[] args) throws BadHanyuPinyinOutputFormatCombination {
        String str1 = "中华人民共和国";
        System.out.println(Arrays.toString(getPinyinByFileName(str1)));
        System.out.println("-------------------------------------------");
        String str2 = "中华c人民共1和2国3";
        System.out.println(Arrays.toString(getPinyinByFileName(str2)));
    }

    public static boolean containsChinese(String str) {
        return str.matches(".*" + CHINESE_PATTERN + ".*");
    }
}

