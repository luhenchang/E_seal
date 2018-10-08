package administrator.example.com.e_seal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.Enumeration;

import javax.crypto.Cipher;

import Decoder.BASE64Encoder;
import administrator.example.com.javaScriptHTML.ImoocJsInterface;
import administrator.example.com.sealread.sealXML;

public class New extends AppCompatActivity implements JsBridge {
    private WebView webView;
    String msgValue;
    private TextView textView ;
    private Handler mHandler;
    private Button button;
    private ImageView imageView;
    private Button buttonS;
    private static boolean Tag = false;
    private static boolean flag = false;
    private static boolean tag = false;
    private String filename;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ac_new,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_item:
                if (flag){
                    AlertDialog.Builder builder = new AlertDialog.Builder(New.this);
                    builder.setTitle("请输入印章密码");
                    //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
                    View vie = LayoutInflater.from(New.this).inflate(R.layout.dialog2, null);
                    //    设置我们自己定义的布局文件作为弹出框的Content
                    builder.setView(vie);

                    final EditText key = vie.findViewById(R.id.key);

                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            if (key.getText().toString().equals("scsrsy")){
                                Toast.makeText(New.this,"密码正确",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(New.this,sealMessage.class);
                                startActivity(intent);
                                tag = true;
                            }
                            else {
                                Toast.makeText(New.this,"密码错误,禁止查看",Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {

                        }
                    });
                    builder.show();
                }else {
                    Toast.makeText(New.this,"请先填表",Toast.LENGTH_SHORT).show();
                }

                break;
        }
        return true;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        buttonS = findViewById(R.id.save);
        webView = findViewById(R.id.webView);
        textView = findViewById(R.id.messageText);
        mHandler = new Handler();
        imageView = findViewById(R.id.sealImageInHtml);
        init();
//        webView.loadUrl("file:///android_assset/biaodan.html");
//        webView.setWebViewClient(new WebViewClient());
        webView.addJavascriptInterface(new ImoocJsInterface(this),"imoocLauncher");
        webView.loadUrl("file:///android_asset/Index.html");
        button = findViewById(R.id.sealPlace);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!flag){
                    Toast.makeText(New.this,"请先输入信息并确认",Toast.LENGTH_SHORT).show();
                }else if (!tag){
                    Toast.makeText(New.this,"请先查看印章，确保印章信息正确性",Toast.LENGTH_SHORT).show();
                }else {
                    Tag = true;
                    Bitmap bitmap = getLocalBitmap();
                    imageView.setImageBitmap(bitmap);
                }
            }
        });
        buttonS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                textView.setText(msgValue + "hahah");
                /*
                保存新的表单
                 */
                if (!(Tag && tag)){
                    Toast.makeText(New.this,"请先进行盖章再保存",Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(New.this);
                    builder.setTitle("请输入文件名");
                    //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
                    View vie = LayoutInflater.from(New.this).inflate(R.layout.dialog, null);
                    //    设置我们自己定义的布局文件作为弹出框的Content
                    builder.setView(vie);

                    final EditText username = vie.findViewById(R.id.fileName);

                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            filename = username.getText().toString().trim();
                            //    将输入的用户名和密码打印出来
                            WriteMsgtoXML(msgValue,filename);
                            Toast.makeText(New.this,"保存成功",Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Toast.makeText(New.this,"未保存",Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.show();

                }
//                WriteMsgtoXML(msgValue);
            }
        });
    }

    private void WriteMsgtoXML(String msg,String file){
        FileOutputStream fos = null;
        //获取SD卡状态
        String state = Environment.getExternalStorageState();
        //判断SD卡是否就绪
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "请检查SD卡", Toast.LENGTH_SHORT).show();
            return;
        }
        //取得SD卡根目录
        File file2 = Environment.getExternalStorageDirectory();
        try {
            Log.d("======SD卡根目录：", "" + file2.getCanonicalPath().toString());
            //File myFile=new File(file.getCanonicalPath()+"/sd.txt");
            /*
            输出流的构造参数1：可以是File对象  也可以是文件路径
            输出流的构造参数2：默认为False=>覆盖内容； true=>追加内容
             */
            fos = new FileOutputStream(file2.getCanonicalPath() + "/" + file + ".txt");

        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] strings = msg.split(" ");

        SAXReader sr = new SAXReader();
        try {
            InputStream in = getAssets().open("user.xml");
            Document doc = sr.read(in);
            Element root = doc.getRootElement();
            Element user = root.addElement("user");
//            Log.d("WriteMsgtoXML",strings[0] + strings[1]);
            user.addElement("name").setText(strings[0]);
            user.addElement("sex").setText(strings[1]);
            user.addElement("EnInfo").setText(InfoToBASE64(encrypt(strings[0] + strings[1])));
            user.addElement("sealPicture").setText(readSealXML());

            XMLWriter sw = new XMLWriter(fos, OutputFormat.createPrettyPrint());
            in.close();
            sw.write(doc);
            sw.close();
        } catch (Exception e) {
            e.printStackTrace();
//            System.out.println(var17);
        }
    }

    private Bitmap getLocalBitmap(){
        try {
            FileInputStream fileInputStream = openFileInput("seal.png");
            return BitmapFactory.decodeStream(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init(){
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }


    @Override
    public void setTextViewValue(final String value) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                textView.setText(value);
                msgValue = value;
                flag = true;
            }
        });
    }



    private String readSealXML(){
        SAXReader saxReader = new SAXReader();
        try {
            InputStream in = getAssets().open("seal.xml");
            Document document = saxReader.read(in);
            Element root = document.getRootElement();
            Element element;
            element=(Element)root.elementIterator("seal").next();
//            return sealXML.bufferChange(element.elementText("sealPicture"));
//            sealXML.bufferChange(element.elementText("sealCer"));
            return sealXML.bufferChange(element.elementText("sealPicture"));
        }
        catch (IOException | DocumentException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private String InfoToBASE64(byte[] info){
        BASE64Encoder encoder=new BASE64Encoder();
        return encoder.encode(info);
    }
    private byte[]  encrypt(String information) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE,readPrivate());
        byte[] msg1=cipher.doFinal(information.getBytes());//加密后的数据
        //System.out.println("加密后：  "+new String(msg1,"UTF8"));
        return msg1;
    }
    private PrivateKey readPrivate(){
        //文档密码
        String keyStore_password = "scsrsy";
        try {
            //根据类型获取keyStore（密钥和证书的存储设施）
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            //打开证书
            FileInputStream fileInputStream = openFileInput("seal.p12");
            //密码的局部变量，因为后面会用到的密码是字符数组，所以使用字符数组
            char[] password = null;
            if (keyStore_password.trim().equals("")) {
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
