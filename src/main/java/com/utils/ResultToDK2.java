package com.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author zhangshichao
 * @version ResultToDK2.java, v 2.0 2022-04-20 20:00:00
 */
public class ResultToDK2 {

    private static final int timeout =10000;
    public static String pathname1 = "/Users/zhangshichao/.jenkins/workspace/yolocast-test-api/allure-html/widgets/summary.json";
    public static String pathname2 = "/Users/zhangshichao/.jenkins/workspace/yolocast-test-api/allure-report/executor.json";
    public static String pathname3 = "/Users/zhangshichao/.jenkins/workspace/yolocast-test-api/allure-html/data/categories.json";
    public static String assertioncontent="Product defects";
    static String dingUrl= "https://oapi.dingtalk.com/robot/send?access_token=6ca1bc74766afffbe49369ae6d7a06de954f7fd01aace49b8c22769e33bb143d";
    static String secret = "SECefdb1f1012985e2fc0fc8bb04dcbcfadfaae08cf976f6883d7d152e01f3b1a9f";
    static Date date;



    //读取txt文件内容 pathname
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

    //组装请求报文 content,isAtAll,mobileList
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

    //读取json文件
    public static String readJsonFile(String fileName) {
        String jsonStr = "";
        try {
            File jsonFile = new File(fileName);
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static void readSummaryJsonSendDingTalk(){
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
            boolean isAtAll = true;
            //通知具体人的手机号码列表
            List<String> mobileList = Lists.newArrayList();
            mobileList.add("");

            //读取prometheusData.txt；存放用例数
            //Map map = readText(pathname);
            //String total = map.get("launch_retries_run").toString();
            //String pass = map.get("launch_status_passed").toString();
            //String fail = map.get("launch_status_failed").toString();
            //String skip = map.get("launch_status_skipped").toString();
            //获取总耗时，转成mm:ss
            //int duration = Integer.parseInt(map.get("launch_time_duration").toString())/1000;
            //String totalDuration = DateUtil.secondToTime(duration);


            JSONParser jsonParser = new JSONParser();
            //读取pathname1文件里的summary.json里的case情况 开始时间 转成yyyy-MM-dd HH:mm:ss
            JSONObject jsonObject1 = (JSONObject) jsonParser.parse(new FileReader(pathname1));
            JSONObject jsonObject2= (JSONObject) jsonObject1.get("statistic");//获取第二层statistic对象
            Number total = (Number) jsonObject2.get("total");
            Number passed = (Number) jsonObject2.get("passed");
            Number failed = (Number) jsonObject2.get("failed");
            Number skipped = (Number) jsonObject2.get("skipped");

            JSONObject jsonObject3= (JSONObject) jsonObject1.get("time");//获取第二层time对象
            //获取总耗时 mm:ss
            Number duration = (Number) jsonObject3.get("duration");
            String totalDuration = DateUtil.secondToTime(duration.intValue()/1000);
            //获取开始时间yyyy-MM-dd HH:mm:ss
            Number start = (Number) jsonObject3.get("start");
            Long times = (Long) start;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = new Date(times);
            String starttime = simpleDateFormat.format(date);

            //读取pathname2 executors.json 项目名称、构建编号、报告地址
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(pathname2));
            String buildUrl = (String) jsonObject.get("buildUrl")+"console";
            String reportUrl = (String) jsonObject.get("reportUrl");
            String buildName = (String) jsonObject.get("buildName");

            //读取pathname3 categories.json 失败用例的断言
            com.alibaba.fastjson.JSONObject jobjdata = JSON.parseObject(readJsonFile(pathname3));
            com.alibaba.fastjson.JSONArray children1 = (jobjdata).getJSONArray("children");//构建JSONArray数组 第一个children数组
            String jobjdata1 = children1.get(0).toString();//第一个children数组转成字符串
            com.alibaba.fastjson.JSONObject jobjdata2 = JSON.parseObject(jobjdata1);//第一个children数组转成的字符串 当作一个新的json
            com.alibaba.fastjson.JSONArray children2 = (jobjdata2).getJSONArray("children");//构建JSONArray数组 第二个children数组
            for (int i = 0 ; i < children2.size();i++){
                //String jobjdata3 = children2.get(i).toString();//第二个children数组转成字符串
                //com.alibaba.fastjson.JSONObject jobjdata4 = JSON.parseObject(children2.get(i).toString());//第二个children数组转成的字符串 当作一个新的json
                //String assertionName = (String) jobjdata4.get("name");
                String assertionName = (String) JSON.parseObject(children2.get(i).toString()).get("name");
                assertioncontent=assertioncontent + "\n"+  assertionName;
            }


            //钉钉机器人消息内容--测试结果
            String title ="API自动化测试结果如下：" + "\n" + starttime;
            String content =title + "\n"
                    + "项目名称：" +buildName + "\n"
                    + "总用例数量：" + total + "\n"
                    + "成功用例数：" + passed + "\n"
                    + "失败用例数：" + failed + "\n"
                    + "跳过用例数：" + skipped + "\n"
                    + "总耗时：" + totalDuration + "\n"
                    + "报告地址：" + reportUrl + "\n"
                    + "日志地址：" + buildUrl + "\n";

            //钉钉机器人消息内容---失败断言
            String assertiontitle =
                    "FailedAssertionTitle：" + assertioncontent + "\n";


            //组装请求内容
            String reqStr = buildReqStr(content, isAtAll, mobileList);
            String reqStr2 = buildReqStr(assertiontitle,isAtAll,mobileList);

            //推送消息（http请求）
            //  String result = HttpUtil.postJson(dingUrl, reqStr);
            String result = HttpRequest.post(dingUrl).body(reqStr).timeout(timeout).execute().body();
            String result2 = HttpRequest.post(dingUrl).body(reqStr2).timeout(timeout).execute().body();
            System.out.println("result == " + result + "\n" + "result2 ==" + result2);

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public static void main(String[] args){

        readSummaryJsonSendDingTalk();

    }
}
