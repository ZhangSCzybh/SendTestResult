package Utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;


public class imageChange {
    public static void main(String[] args) throws IOException {
        List<String> item=new ArrayList<String>();
        File file = new File("/Users/zhangshichao/Documents/material/picture");
        File[] files = file.listFiles();
//        System.out.println("files长度"+files.length);
        for (int i = 0; i < files.length; i++) {
            File file1 = files[i];
            file1.getName();
//            System.out.println("输出文件后缀名"+file1.getName());
            if (file1.getName().indexOf(".jpg")>-1) {
                item.add("/Users/zhangshichao/Documents/material/picture"+files[i].getName());
                System.out.println("输出文件files[i].getName():"+files[i].getName());
            }
        }
        for (int i = 0; i < item.size(); i++) {
            System.out.println("item集合内部数据"+item.get(i));
            String type= "jpg";
            File src = new File(item.get(i));
            File dir = new File("/Users/zhangshichao/Documents/material/pictures");
            trans(src, dir, type);
        }

    }

    public static void trans(File imgFile,File outDir,String type) throws IOException {
        BufferedImage img = ImageIO.read(imgFile);
        //设置文件名
        String srcName = imgFile.getName().replace(".jpg", "")+"."+type;
        File out = new File(outDir+File.separator+srcName);
        ImageIO.write(img,type,out);

    }
}
