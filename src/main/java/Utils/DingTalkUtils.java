package Utils;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import sun.misc.UCEncoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.ws.Response;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DingTalkUtils {

    private static final int timeout = 10000;
    Response Response = null;

    /**
     * 发送消息到钉钉
     *
     * @param dingUrl
     *            钉钉机器人地址
     * @param secret
     *            钉钉机器人密钥
     * @param content
     *            发送的内容
     * @param phone
     *            通知具体人的手机号码
     * @return
     */

    public Response sendDingTalkUseSecret(String dingUrl,String secret,String content,String phone){
        // 钉钉机器人地址（配置机器人的webhook）
        Long timestamp =System.currentTimeMillis();
        String sign = null;
        try{
            //获取签名
            sign = getSign(timestamp,secret);
        } catch (Exception e){
            //System.out.println("获取标签失败");
            e.printStackTrace();
        }
        dingUrl = dingUrl + "&timestamp=" + timestamp + "&sign=" + sign;
        // 是否通知所有人
        boolean isAtAll = false;
        // 通知具体人的手机号码列表
        List<String> mobileList = Lists.newArrayList();
        mobileList.add(phone);
        mobileList.add(phone);
        // 组装请求内容
        String reqStr = buildReqStr(content, isAtAll, mobileList);
        //推送消息（http请求）
//            String result = HttpUtil.postJson(dingUrl, reqStr);
        String result = HttpRequest.post(dingUrl).body(reqStr).timeout(timeout).execute().body();
        System.out.println("result == " + result);
        return Response;
    }


    /**
     * 组装请求报文
     *
     * @param content
     *            内容
     * @param isAtAll
     *            是否通知所有人
     * @param mobileList
     *            通知具体人的手机号码列表
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
     *
     * @param timestamp
     *            当前时间戳，单位是毫秒
     * @param secret
     *            密钥
     * @return
     * @throws Exception
     */
    private String getSign(Long timestamp, String secret) throws Exception {
        //timestamp=System.currentTimeMillis();
        //secret = "SECefdb1f1012985e2fc0fc8bb04dcbcfadfaae08cf976f6883d7d152e01f3b1a9f";
        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
        String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
        System.out.println(sign);
        return sign;
    }

    /**
     * 读取txt文本信息
     *
     */
    public  String readTxt() throws IOException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmm");
     /*   String time = sdf.format(new Date());
        if(time.endsWith("0900")){

        }else if(time.endsWith("1100")){

        }else if(time.endsWith("1500")){

        }else if(time.endsWith("2000")){

        }*/
        String txt = "/Users/admin/Documents/参考教程.txt";
        System.out.println(txt+"--111");
        String message = "";
        BufferedReader br = new BufferedReader(new FileReader(txt));
        String line;
        while((line = br.readLine()) != null){
            message = message + line + "\r\n";
        }
        System.out.println(message);
        return message;
    }




}
