package Utils;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class ParseFile {

    /**
     * 解析html文件
     * @param file
     * @return
     */

    public String readHtml(File file){
        String body = "";
        try {
            FileInputStream iStream = new FileInputStream(file);
            Reader reader = new InputStreamReader(iStream);
            BufferedReader htmlReader = new BufferedReader(reader);

            String line;
            boolean found = false;
            while (!found && (line = htmlReader.readLine()) != null) {
                if (line.toLowerCase().indexOf("<body") != -1) { // 在<body>的前面可能存在空格
                    found = true;
                }
            }

            found = false;
            while (!found && (line = htmlReader.readLine()) != null) {
                if (line.toLowerCase().indexOf("</body") != -1) {
                    found = true;
                } else {
                    // 如果存在图片，则将相对路径转换为绝对路径
                    String lowerCaseLine = line.toLowerCase();
                    if (lowerCaseLine.contains("src")) {

                        //这里是定义图片的访问路径
                        String directory = "/Users/zhangshichao/Documents/Yololiv/jmeter_interface/report/yolocast-v2.0.1-2022-03-30-06-18-30-006-0.html";
                        // 如果路径名不以反斜杠结尾，则手动添加反斜杠
                        /*if (!directory.endsWith("\\")) {
                            directory = directory + "\\";
                        }*/
                        //    line = line.substring(0,  lowerCaseLine.indexOf("src") + 5) + directory + line.substring(lowerCaseLine.indexOf("src") + 5);
                        /*String filename = extractFilename(line);
                        line = line.substring(0,  lowerCaseLine.indexOf("src") + 5) + directory + filename + line.substring(line.indexOf(filename) + filename.length());
                    */
                        // 如果该行存在多个<img>元素，则分行进行替代
                        String[] splitLines = line.split("<img\\s+"); // <img后带一个或多个空格
                        // 因为java中引用的问题不能使用for each
                        for (int i = 0; i < splitLines.length; i++) {
                            if (splitLines[i].toLowerCase().startsWith("src")) {
                                splitLines[i] = splitLines[i].substring(0, splitLines[i].toLowerCase().indexOf("src") + 5)
                                        + directory
                                        + splitLines[i].substring(splitLines[i].toLowerCase().indexOf("src") + 5);
                            }
                        }

                        // 最后进行拼接
                        line = "";
                        for (int i = 0; i < splitLines.length - 1; i++) { // 循环次数要-1，因为最后一个字符串后不需要添加<img
                            line = line + splitLines[i] + "<img ";
                        }
                        line = line + splitLines[splitLines.length - 1];
                    }

                    body = body + line + "\n";
                }
            }
            htmlReader.close();
            //        System.out.println(body);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return body;
    }

    /**
     *
     * @param htmlLine 一行html片段，包含<img>元素
     * @return 文件名
     */
    public static String extractFilename(String htmlLine) {
        int srcIndex = htmlLine.toLowerCase().indexOf("src=");
        if (srcIndex == -1) { // 图片不存在，返回空字符串
            return "";
        } else {
            String htmlSrc = htmlLine.substring(srcIndex + 4);
            char splitChar = '\"'; // 默认为双引号，但也有可能为单引号
            if (htmlSrc.charAt(0) == '\'') {
                splitChar = '\'';
            }
            String[] firstSplit = htmlSrc.split(String.valueOf(splitChar));
            String path = firstSplit[1]; // 第0位为空字符串
            String[] secondSplit = path.split("[/\\\\]"); // 匹配正斜杠或反斜杠
            return secondSplit[secondSplit.length - 1];
        }
    }



}