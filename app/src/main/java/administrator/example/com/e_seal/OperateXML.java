package administrator.example.com.e_seal;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.FileOutputStream;
public class OperateXML {
    //读取XML文件中指定结点到字符串
//    public String ReadInfo(String XmlPath,String[] elements,String ele){
//        SAXReader saxReader = new SAXReader();
//        String info="";
//        try {
//            Document document = saxReader.read(XmlPath);
//            Element root = document.getRootElement();
//            Element element;
//            element=(Element)root.elementIterator(ele).next();
//            System.out.println(elements.length);
//            for(int i=0;i<elements.length;i++){
//                info=info+element.elementText(elements[i]).trim();
//            }
//        }catch(DocumentException e){
//            e.printStackTrace();
//        }
//        return info;
//    }
//    public void WriteInfo(String XmlPath,String EncryptedInfo,String PicInfo) {
//        SAXReader sr = new SAXReader();
//        try {
//            Document doc = sr.read(XmlPath);
//            Element root = doc.getRootElement();
//            Element element=(Element)root.elementIterator("user").next();
//            element.addElement("EnInfo").setText(EncryptedInfo);//加密后的信息
//            element.addElement("sealPicture").setText(PicInfo);//印章的信息
//            XMLWriter sw = new XMLWriter(new FileOutputStream("D:\\K\\sc\\18/user1.xml"), OutputFormat.createPrettyPrint());
//            sw.write(doc);
//            sw.close();
//        } catch (Exception var17) {
//            System.out.println(var17);
//        }
//    }

}