package com.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * @author zhangshichao
 * @version main.java, v 0.1 2021-07-06 17:15
 */
public class ResultToDK2 {

    private static final int timeout =10000;
    public static String pathname = "/Users/zhangshichao/.jenkins/workspace/yolocast2.0-test-api/allure-html/export/prometheusData.txt";
    static String dingUrl= "https://oapi.dingtalk.com/robot/send?access_token=6ca1bc74766afffbe49369ae6d7a06de954f7fd01aace49b8c22769e33bb143d";
    static String secret = "SECefdb1f1012985e2fc0fc8bb04dcbcfadfaae08cf976f6883d7d152e01f3b1a9f";
    static Date date;

    /**
     * 读取txt文件内容
     * @param pathname
     * @return
     */
    public static Map readText(String pathname) throws Exception{
        FileReader reader = new FileReader(pathname);
        BufferedReader br = new BufferedReader(reader);
        String line=null;
        Map map = new HashMap();
        while ((line = br.readLine()) != null) {
            String launch = line.split(" ")[0];
            String num = line.split(" ")[1];
            map.put(launch,num);
        }
        return map;
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



    public static void readTxtSendDingTalk(){
        try {
            //获取当前时间戳
            Long timestamp = System.currentTimeMillis();
            //签名计算

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

            //读取prometheusData.txt；存放用例数
            Map map = new HashMap();
            map = readText(pathname);
            String total = map.get("launch_retries_run").toString();
            String pass = map.get("launch_status_passed").toString();
            String fail = map.get("launch_status_failed").toString();
            String skip = map.get("launch_status_skipped").toString();
            int duration = Integer.parseInt(map.get("launch_time_duration").toString())/1000;
            String totalDuration = DateUtil.secondToTime(duration);

            //获取当前时间yyyy-MM-dd HH:mm:ss
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(timestamp);
            String res = simpleDateFormat.format(date);

            //钉钉机器人消息内容
            String title ="API自动化测试结果如下：" + "\n" +res ;
            String content =title + "\n"
                    + "总用例数：" + total + "\n"
                    + "通过用例数：" + pass + "\n"
                    + "失败用例数：" + fail + "\n"
                    + "跳过用例数：" + skip + "\n"
                    + "总耗时：" + totalDuration ;

            //组装请求内容
            String reqStr = buildReqStr(content, isAtAll, mobileList);

            //推送消息（http请求）
            //  String result = HttpUtil.postJson(dingUrl, reqStr);
            String result = HttpRequest.post(dingUrl).body(reqStr).timeout(timeout).execute().body();
            System.out.println("result == " + result);

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public static void main(String[] args){

        readTxtSendDingTalk();
    }
}
