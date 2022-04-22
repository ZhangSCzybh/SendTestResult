import Utils.DingTalkUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class dtTest {






    public static void main(String args[]) throws IOException {

        String keyword = "DYZ3AALTRBD2AIDLL0Y3EQ4TYGLJDUM";
        String subTitle = "服务启动通知";
        String content = "服务启动异常啦。。。";


        Long timestamp = System.currentTimeMillis();
        Date date =new Date(timestamp);

        //钉钉机器人消息内容
        DingTalkUtils dingTalkUtils =new DingTalkUtils();
        dingTalkUtils.sendDingTalkUseSecret(
                "https://oapi.dingtalk.com/robot/send?access_token=6ca1bc74766afffbe49369ae6d7a06de954f7fd01aace49b8c22769e33bb143d",
                "SECefdb1f1012985e2fc0fc8bb04dcbcfadfaae08cf976f6883d7d152e01f3b1a9f",
                "日期："+ date+"\n"+subTitle+"\n"+readTxt(),
                "17858803001"
        );

    }


    /**
     * 读取txt文本信息
     *
     */
    public static String readTxt() throws IOException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmm");
     /*   String time = sdf.format(new Date());
        if(time.endsWith("0900")){

        }else if(time.endsWith("1100")){

        }else if(time.endsWith("1500")){

        }else if(time.endsWith("2000")){

        }*/
        //String txt = "/Users/admin/Documents/"+sdf.format(new Date())+".txt";
        String txt = "/Users/admin/Documents/20210709-2127.txt";

        System.out.println(txt);
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
