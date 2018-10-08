package administrator.example.com.e_seal;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.List;

import javax.crypto.Cipher;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;
import ru.bartwell.exfilepicker.ExFilePicker;
import ru.bartwell.exfilepicker.data.ExFilePickerResult;


public class Open extends AppCompatActivity {
    private ImageView imageView;
    private AppCompatActivity mActivity;
    private final int EX_FILE_PICKER_RESULT = 0xfa01;
    private String startDirectory = null;
    private WebView webView;
    private String filePath;
    private static boolean tag = false;
    private FileInputStream fis;
    private String filex;
//    private File root2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open);
        imageView = findViewById(R.id.image);
        Button button = findViewById(R.id.button);
        Button button2 = findViewById(R.id.button3);
        mActivity = this;
        webView = findViewById(R.id.newWebView);

//        WebSettings webSettings = webView.getSettings();
//        webSettings.setJavaScriptEnabled(true);
//        webView.loadUrl("file:///android_asset/Index.html");
        button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void onClick(View view) {
                webView.loadUrl("file:///android_asset/open.html");
                WebSettings webSettings = webView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                ExFilePicker exFilePicker = new ExFilePicker();
                exFilePicker.setCanChooseOnlyOneItem(true);// 单选
                exFilePicker.setQuitButtonEnabled(true);

                if (TextUtils.isEmpty(startDirectory)) {
                    exFilePicker.setStartDirectory(Environment.getExternalStorageDirectory().getPath());
                } else {
                    exFilePicker.setStartDirectory(startDirectory);
                }

                exFilePicker.setChoiceType(ExFilePicker.ChoiceType.FILES);
                exFilePicker.start(mActivity, EX_FILE_PICKER_RESULT);
                tag = true;

            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                验章操作
                 */
                if (!tag){
                    Toast.makeText(Open.this,"请先打开文件",Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        signTest(filex);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EX_FILE_PICKER_RESULT) {
            ExFilePickerResult result = ExFilePickerResult.getFromIntent(data);
            if (result != null && result.getCount() > 0) {
                String path = result.getPath();

                List<String> names = result.getNames();
                for (int i = 0; i < names.size(); i++) {
                    File f = new File(path, names.get(i));
                    try {
                        Uri uri = Uri.fromFile(f); //这里获取了真实可用的文件资源
//                        Toast.makeText(mActivity, "选择文件:" + uri.getPath(), Toast.LENGTH_SHORT).show();
//                        Log.d("uri","uri = " + uri.toString());
                        String[] uris = uri.toString().split("/",2);
                        filex = uris[uris.length-1];
//                        filePath = uri.toString();

                        openXML(filex);


                        startDirectory = path;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    /*
    读表操作
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void openXML(String file){
//        BufferedReader reader = null;
        fis = null;
//        StringBuilder sbd = new StringBuilder();
        String state = Environment.getExternalStorageState();
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "SD卡未就绪", Toast.LENGTH_SHORT).show();
        }
//        root2 = Environment.getExternalStorageDirectory();
        try {
//            Log.d("filePath2",root2 + "/sd.txt");
            Log.d("filePath",file);
            fis = new FileInputStream(file);

        } catch (FileNotFoundException e) {
            Toast.makeText(this, "文件不存在", Toast.LENGTH_SHORT).show();
            //e.printStackTrace();
        }
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(fis);
            Element root = document.getRootElement();
            Element element;
            element=(Element)root.elementIterator("user").next();
//            Log.d("openXMLname",element.elementText("name"));
//            Log.d("openXMLsex",element.elementText("sex"));
            String name = element.elementText("name");
            String sex = element.elementText("sex");
            String sealMes = element.elementText("EnInfo");
            String img = element.elementText("sealPicture");
//            webView.loadUrl("file:///android_asset/Index.html");
//            WebSettings webSettings = webView.getSettings();
//            webSettings.setJavaScriptEnabled(true);

            webView.loadUrl("javascript:if(window.remoteName){window.remoteName('" + name +  "')}");
            webView.loadUrl("javascript:if(window.remoteSex){window.remoteSex('" + sex + "')}");
            Bitmap bitmap = getLocalBitmap("seal.png");
            imageView.setImageBitmap(bitmap);

        }
        catch (DocumentException e)
        {
            e.printStackTrace();
        }
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

    public void signTest(String file) throws  Exception{
        fis = null;
//        StringBuilder sbd = new StringBuilder();
        String state = Environment.getExternalStorageState();
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "SD卡未就绪", Toast.LENGTH_SHORT).show();
        }
//        root2 = Environment.getExternalStorageDirectory();
        try {
//            Log.d("filePath2",root2 + "/sd.txt");
            fis = new FileInputStream(file);

        } catch (FileNotFoundException e) {
            Toast.makeText(this, "文件不存在", Toast.LENGTH_SHORT).show();
            //e.printStackTrace();
        }
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(fis);
        Element root = document.getRootElement();
        Element element;
        element=(Element)root.elementIterator("user").next();
        //  System.out.println(elements.length);
        String info=element.elementText("name")+element.elementText("sex");
        String infoseal = element.elementText("EnInfo");

        BASE64Decoder decoder=new BASE64Decoder();
        byte[] decryptedData= decoder.decodeBuffer((bufferChange(infoseal)));//解码后的加密信息

        if (info.equals(new String(decrypt(decryptedData), "UTF8"))){
            Toast.makeText(Open.this,"验章成功",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(Open.this,"验章失败",Toast.LENGTH_SHORT).show();
        }

    }
    private static String bufferChange(String msg){
        String buffer = "";

        String[] pic = msg.split(" ");
        for (int i = 0;i < pic.length; i++){
            if (i != pic.length-1 ) buffer += (pic[i] + "\r" + "\n");
            else buffer += pic[i];
        }
        return buffer;
    }

    private byte[] BASE64ToInfo(String base)throws  Exception{
        BASE64Decoder decoder=new BASE64Decoder();
        return decoder.decodeBuffer(base);
    }

    private byte[]  decrypt(byte[] encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, readPublic("seal.p12"));
        // 解密byte数组最大长度限制: 256
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > 256) {
                cache = cipher.doFinal(encryptedData, offSet, 256);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache);
            i++;
            offSet = i * 256;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;

//        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
//        cipher.init(Cipher.DECRYPT_MODE, readPublic("seal.p12"));
//        byte[] msg2=cipher.doFinal(encrypedInfo);
//        return msg2;
//        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
//        cipher.init(Cipher.DECRYPT_MODE, readPublic("seal.p12"));
//        // 解密byte数组最大长度限制: 256
//        int inputLen = encrypedInfo.length;
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        int offSet = 0;
//        byte[] cache;
//        int i = 0;
//        // 对数据分段解密
//        while (inputLen - offSet > 0) {
//            if (inputLen - offSet > 256) {
//                cache = cipher.doFinal(encrypedInfo, offSet, 256);
//            } else {
//                cache = cipher.doFinal(encrypedInfo, offSet, inputLen - offSet);
//            }
//            out.write(cache);
//            i++;
//            offSet = i * 256;
//        }
//        byte[] decryptedData = out.toByteArray();
//        out.close();
//        return decryptedData;
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

        }
        return null;
    }

    public PublicKey readPublic(String path){
        String keyStore_password = "scsrsy";
        try {
            //根据类型获取keyStore（密钥和证书的存储设施）
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            //打开证书
            FileInputStream fileInputStream = openFileInput(path);
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
