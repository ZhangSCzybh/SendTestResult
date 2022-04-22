package com.utils;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.codec.binary.Base64;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;



/**
 * @author zhangshichao
 * @version main.java, v 0.1 2021-07-06 17:15
 */
public class ResultToDK {

    private static final int timeout;
    static {
        timeout = 10000;
    }
    static String Reportpath = "/Users/zhangshichao/Documents/Yololiv/jmeter_interface/report/";
    static String bpReportpath = "/Users/zhangshichao/Documents/Yololiv/jmeter_interface/backupReport/";
    static File bpfile;
    static File srcFile;
    public static void main(String[] args) throws IOException {
        try {
            //加密secret
            String dingUrl = "https://oapi.dingtalk.com/robot/send?access_token=6ca1bc74766afffbe49369ae6d7a06de954f7fd01aace49b8c22769e33bb143d";
            //获取当前时间戳
            Long timestamp = System.currentTimeMillis();
            //签名计算
            String secret = "SECefdb1f1012985e2fc0fc8bb04dcbcfadfaae08cf976f6883d7d152e01f3b1a9f";
            String stringToSign =timestamp + "\n" + secret;
            Mac mac =Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes("UTF-8"),"HmacSHA256"));
            byte[] signData =mac.doFinal(stringToSign.getBytes("UTF-8"));
            String sign= URLEncoder.encode(new String(Base64.encodeBase64(signData)),"UTF-8");
            //System.out.println(sign);
            dingUrl = dingUrl + "&timestamp=" + timestamp + "&sign=" + sign;
            //System.out.println(stringToSign);


            //是否通知所有人  isAtAll为true和mobileList冲突只能二选一
            boolean isAtAll = false;
            //通知具体人的手机号码列表
            List<String> mobileList = Lists.newArrayList();
            mobileList.add("17858803001");

            // 根据给定的路径创建一个File对象
            srcFile = new File(Reportpath);
            //获取给定的File目录下所有的文件或者目录的File数组
            File[] fileArray = srcFile.listFiles();
            Document document = Jsoup.parse(fileArray[0], "utf-8");

            //summary--用例总数、失败用例数、忽略用例数 （右键 copy json value）
            Elements totalRequests = document.select("#pills-requests-tab > span");
            Elements failedTests = document.select("#pills-failed-tab > span");
            Elements skippedTests = document.select("#pills-skipped-tab > span");
            // 总断言数量
            Elements totalAssertions = document.select("#pills-summary > div > div > div:nth-child(3) > div:nth-child(2) > div > div > h1");
            //获取报告时间
            //document.querySelector("#pills-summary > div > div > h5")
            Elements date = document.select("#pills-summary > div > div > h5");
            //System.out.println("Total：" + totalRequests.html() + "\n" + "failed：" + failedTests.html() + "\n" + "skipped：" + skippedTests.html() + "\n" + "totalAssertions：" + totalAssertions.html() + "\n"  );
            //钉钉机器人消息内容
            String title ="API自动化测试结果如下：" + "\n" + date.html();
            String content =title + "\n" +"Total："+totalRequests.html() + "\n" + "Failed："+failedTests.html() + "\n" + "Skipped："+ skippedTests.html() + "\n" + "TotalAssertions：" + totalAssertions.html() + "\n";
            //组装请求内容
            String reqStr = buildReqStr(content, isAtAll, mobileList);

            //推送消息（http请求）
            //  String result = HttpUtil.postJson(dingUrl, reqStr);
            String result = HttpRequest.post(dingUrl).body(reqStr).timeout(timeout).execute().body();
            System.out.println("result == " + result);

            //移动本次报告至backupReport文件夹
            bpfile = new File(bpReportpath+fileArray[0].getName());
            fileArray[0].renameTo(bpfile);


        }catch (Exception e){
            e.printStackTrace();
        }

    }



    /**
     * 组装请求报文
     * @param content
     * @return
     */
    private static String buildReqStr(String content, boolean isAtAll, List<String> mobileList) {
        //消息内容
        Map<String, String> contentMap = Maps.newHashMap();
        contentMap.put("content", content);

        //通知人
        Map<String, Object> atMap = Maps.newHashMap();
        //1.是否通知所有人
        atMap.put("isAtAll", isAtAll);
        //2.通知具体人的手机号码列表
        atMap.put("atMobiles", mobileList);

        Map<String, Object> reqMap = Maps.newHashMap();
        reqMap.put("msgtype", "text");
        reqMap.put("text", contentMap);
        reqMap.put("at", atMap);

        return JSON.toJSONString(reqMap);
    }

    /**
     *遍历report文件夹获取最新的报告
     * @param srcFile
     */
    // 定义一个方法，用于获取给定目录下的所有内容，参数为第1步创建的File对象
    public static void getAllFilePath(File srcFile) {
        // 3：获取给定的File目录下所有的文件或者目录的File数组
        File[] fileArray = srcFile.listFiles();
        // 4：遍历该File数组，得到每一个File对象
        if (fileArray != null) {
            for (File file : fileArray) {
                // 5：判断该File对象是否是目录
                if (file.isDirectory()) {
                    // 5.1：是：递归调用
                    getAllFilePath(file);
                } else {
                    // 5.2：不是：获取绝对路径输出在控制台
                    System.out.println(file.getAbsolutePath());
                }
            }
        }
    }



//    public static String postJson(String url, String reqStr) {
//        String body = null;
//        try {
//            body = HttpRequest.post(url).body(reqStr).timeout(timeout).execute().body();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return body;
//    }
}