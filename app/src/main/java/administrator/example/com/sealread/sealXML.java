package administrator.example.com.sealread;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.nio.Buffer;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.Iterator;

public class sealXML {
    static String strImg;
    static String certificate;

    //将path文件写进path2.xml中
    private static void createSealXML(String path,String path2){
        SAXReader sr = new SAXReader();
        try {

            //读取xml文档

//            Document doc = sr.read("D:/SX/src/user.xml");
            Document doc = sr.read(path);
//            Document doc = DocumentHelper.createDocument();
            Element root = doc.getRootElement();        //获取根标签
            Element seal = root.addElement("seal");
            seal.addElement("sealPicture").setText(strImg);        //为标签设置值
            seal.addElement("sealCer").setText(certificate);

            XMLWriter sw = new XMLWriter(
                    new FileOutputStream(path2)
                    ,OutputFormat.createPrettyPrint()        //以正常格式写进文档
            );
            sw.write(doc);
            sw.close();
        } catch (Exception e) {
            System.out.println("error: " + e);
        }
    }

    //读取path目录下的path.xml文件,并在picpath和cerpath中写出来
    public static void readSealXML(String path, String picpath , String cerpath){
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(path);
            Element root = document.getRootElement();
            Element element;
            element=(Element)root.elementIterator("seal").next();
            generateFile(bufferChange(element.elementText("sealPicture")),picpath);
            generateFile(bufferChange(element.elementText("sealCer")),cerpath);
        }
        catch (DocumentException e)
        {
            e.printStackTrace();
        }
    }

    private static void generateFile(String file, String path){
        if (file == null)
            return;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            byte[] b = decoder.decodeBuffer(file);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }

            }
            String filePath = path;
            OutputStream out = new FileOutputStream(filePath);
            out.write(b);
            out.flush();
            out.close();
        } catch (Exception e) {
        }


    }

    public static String bufferChange(String msg){
        String buffer = "";

        String[] pic = msg.split(" ");
        for (int i = 0;i < pic.length; i++){
            if (i != pic.length-1 ) buffer += (pic[i] + "\n");
            else buffer += pic[i];
        }
        return buffer;
    }

    //将path目录下的证书私钥读取出来
    public static PrivateKey readPrivate(String path){
        //文档密码
        String keyStore_password = "scsrsy";
        try {
            //根据类型获取keyStore（密钥和证书的存储设施）
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            //打开证书
            FileInputStream fileInputStream = new FileInputStream(path);
            //密码的局部变量，因为后面会用到的密码是字符数组，所以使用字符数组
            char[] password = null;
            if ((keyStore_password == null) || keyStore_password.trim().equals("")) {
                password = null;
            } else {
                password = keyStore_password.toCharArray();
            }
            //用密码加载keyStore
            keyStore.load(fileInputStream, password);
            fileInputStream.close();


            //获取别名的枚举类型
            Enumeration enumeration = keyStore.aliases();
            //别名
            String keyAlias = null;
            if (enumeration.hasMoreElements()) {
                keyAlias = (String) enumeration.nextElement();
            }

            //利用别名直接加载私钥
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(keyAlias, password);

            return privateKey;
        }catch (Exception e){

        }
        return null;
    }

    //将path目录下的证书公钥读取出来
    public static PublicKey readPublic(String path){
        String keyStore_password = "scsrsy";
        try {
            //根据类型获取keyStore（密钥和证书的存储设施）
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            //打开证书
            FileInputStream fileInputStream = new FileInputStream(path);
            //密码的局部变量，因为后面会用到的密码是字符数组，所以使用字符数组
            char[] password = null;
            if (keyStore_password.trim().equals("")){
            }else{
                password = keyStore_password.toCharArray();

            }
            //用密码加载keyStore
            keyStore.load(fileInputStream,password);
            fileInputStream.close();
            //证书类型

            //获取别名的枚举类型
            Enumeration enumeration = keyStore.aliases();
            //别名
            String keyAlias = null;
            if (enumeration.hasMoreElements()){
                keyAlias = (String) enumeration.nextElement();
            }
            //获取证书
            Certificate certificate = keyStore.getCertificate(keyAlias);
            //公钥获取
            PublicKey publicKey = certificate.getPublicKey();

            return publicKey;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
