package com.utils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

public class Html {
    static String Reportpath = "/Users/zhangshichao/Documents/Yololiv/jmeter_interface/report1";
    static String bpReportpath = "/Users/zhangshichao/Documents/Yololiv/jmeter_interface/backupReport";
    static File bpfile;
    static File srcFile;

    public static void main(String[] args) throws IOException {
        // 根据给定的路径创建一个File对象
        srcFile = new File(Reportpath);
        //获取给定的File目录下所有的文件或者目录的File数组
        File[] fileArray = srcFile.listFiles();
        Document document = Jsoup.parse(fileArray[0], "utf-8");
        //summary--用例总数、失败用例数、忽略用例数
        Elements totalRequests = document.select("#pills-requests-tab > span");
        Elements failedTests = document.select("#pills-failed-tab > span");
        Elements skippedTests = document.select("#pills-skipped-tab > span");
        // 总断言数量
        Elements totalAssertions = document.select("#pills-summary > div > div > div:nth-child(3) > div:nth-child(2) > div > div > h1");
        System.out.println("测试用例总数：" + totalRequests.html() + "\n" + "失败用例数：" + failedTests.html() + "\n" + "忽略执行用例数：" + skippedTests.html() + "\n" + "总断言数量：" + totalAssertions.html());
        //获取当前时间戳
        Long timestamp = System.currentTimeMillis();
        bpfile = new File(bpReportpath+fileArray[0].getName());
        fileArray[0].renameTo(bpfile);


        //for (Element element : es) {
        //    System.out.printf("%s\t%s\n", element.html(), element.val());
        //}
/**
 Connection conn = Jsoup.connect("http://www.baidu.com");
 Document document1 = conn.get();
 //解析出 class为feedback的li标签  的后代a标签元素
 Elements elements = document1.select("#s-top-left");

 for (Element element : elements) {
 System.out.println(element.html());
 System.out.println(element.attr("href"));
 }
 */

        // 6：调用方法
        //getAllFilePath(srcFile);

    }

    public static void getAllFilePath(File srcFile) throws IOException {
        // 3：获取给定的File目录下所有的文件或者目录的File数组
        File[] fileArray = srcFile.listFiles();
        System.out.println(fileArray[0] + "33333");
        // 4：遍历该File数组，得到每一个File对象
        if (fileArray != null) {
            for (File file : fileArray) {
                // 5：判断该File对象是否是目录
                if (file.isDirectory()) {
                    // 5.1：是：递归调用
                    getAllFilePath(file);
                } else {
                    // 5.2：不是：获取绝对路径输出在控制台
                    //System.out.println(file.getAbsolutePath());
                }
            }
        }

    }

}
