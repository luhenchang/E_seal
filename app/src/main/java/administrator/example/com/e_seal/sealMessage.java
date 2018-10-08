package administrator.example.com.e_seal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.Enumeration;

import Decoder.BASE64Decoder;
import administrator.example.com.sealread.sealXML;

public class sealMessage extends AppCompatActivity {

    private TextView data_textView;
    private TextView dataPlave_textView;
    private TextView long_textView;
    private TextView people_textView;
    private TextView useWay_textView;
    private ImageView seal_pic;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seal_message);
        seal_pic = findViewById(R.id.sealImage);
        data_textView = findViewById(R.id.textView3);
        dataPlave_textView = findViewById(R.id.textView2);
        long_textView = findViewById(R.id.textView4);
        people_textView = findViewById(R.id.textView);
        useWay_textView = findViewById(R.id.textView5);
//        readSealXML("file:///android_asset/seal.xml");
        button = findViewById(R.id.cerButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readSealXML();
                Bitmap bitmap = getLocalBitmap("seal.png");
                seal_pic.setImageBitmap(bitmap);
            }
        });

    }

    private Bitmap getLocalBitmap(String url){
        try {
            FileInputStream fileInputStream = openFileInput(url);
            return BitmapFactory.decodeStream(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    /*
    msg是传入的文件信息
     */
    public void save(String file,String filePath){
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
            FileOutputStream out = openFileOutput(filePath, Context.MODE_PRIVATE);
            out.write(b);
            out.flush();
            out.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readSealXML(){
        SAXReader saxReader = new SAXReader();
        try {
            InputStream in = getAssets().open("seal.xml");
            Document document = saxReader.read(in);
            Element root = document.getRootElement();
            Element element;
            element=(Element)root.elementIterator("seal").next();
//            return sealXML.bufferChange(element.elementText("sealPicture"));
//            sealXML.bufferChange(element.elementText("sealCer"));
            people_textView.setText("证书制作人：" + element.elementText("people"));
            dataPlave_textView.setText("证书颁发地：" + element.elementText("dataplace"));
            data_textView.setText("证书颁发日期：" + element.elementText("data"));
            long_textView.setText("证书有效期：" + element.elementText("longy"));
            useWay_textView.setText("证书使用限制：" + element.elementText("useWay"));
            save(sealXML.bufferChange(element.elementText("sealPicture")),"seal.png");
            save(sealXML.bufferChange(element.elementText("sealCer")),"seal.p12");


        }
        catch (IOException | DocumentException e)
        {
            e.printStackTrace();
        }
    }
    private PrivateKey readPrivate(String path){
        //文档密码
        String keyStore_password = "scsrsy";
        try {
            //根据类型获取keyStore（密钥和证书的存储设施）
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            //打开证书
            FileInputStream fileInputStream = openFileInput(path);
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
            e.printStackTrace();
        }
        return null;
    }


}
