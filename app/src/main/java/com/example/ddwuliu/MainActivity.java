package com.example.ddwuliu;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText et_userName = findViewById(R.id.et_userName);
        final EditText et_userPwd = findViewById(R.id.et_userPwd);
        final TextView textView = findViewById(R.id.textView);
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et_userName.getText().toString();
                String password = et_userPwd.getText().toString();
                if(TextUtils.isEmpty(name)){
                    Toast.makeText(MainActivity.this,"用户名不能为空",Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(password)){
                    Toast.makeText(MainActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                }else {
                    //初始化okhttp客户端
                    OkHttpClient client = new OkHttpClient.Builder().build();
                    //创建post表单，获取username和password（没有做非空判断）
                    RequestBody post = new FormBody.Builder()
                            .add("account",name)
                            .add("password",password)
                            .build();
                    //开始请求，填入url，和表单
                    final Request request = new Request.Builder()
                            //10.0.2.2为模拟机访问本地服务器需要填的ip地址，对于真机等情况可以看我之前的博客
//                            .url("http://192.168.1.105:8080/MySQL/SQL/Usuario.php")
                            .url("http://192.168.57.152:8080/MySQL/SQL/Usuario.php")
                            .post(post)
                            .build();

                    client.newCall(request).enqueue(new okhttp3.Callback() {
                        @Override
                        public void onFailure(okhttp3.Call call, IOException e) {
                            Log.v("secondasdfas", "asfdasf");
                            e.printStackTrace();
                        }
                        //获取成功
                        @Override
                        public void onResponse(okhttp3.Call call, final okhttp3.Response response) throws IOException {
                            //UI线程运行
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    UserResult userResult = null;
                                    try {
                                        //获取json
                                        String responseStr = response.body().string();
                                        Log.v("asfsafasfasfasfas", responseStr);
                                        JSONObject jsonObject = new JSONObject(responseStr);
                                        String jsonStr = jsonObject.toString();
                                        Gson gson = new Gson();
                                        userResult = gson.fromJson(jsonStr, UserResult.class);
                                        //解析
                                        Log.v("dsafasfdsafas a", jsonStr);
//                                        Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
//                                        textView.setVisibility(View.VISIBLE);
//                                        textView.setText("Login Successfully");
                                    } catch (JSONException e) {
                                        Log.v("hererererer","afsafafsdasfda");
                                        e.printStackTrace();
                                        Log.v("thererererer","afsafafsdasfda");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    if (userResult!=null&&userResult.getUser_id()>0){
                                        Bundle bundle = new Bundle();
                                        bundle.putString("username", userResult.getUsername());
                                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                        intent.putExtra("Message",bundle);
                                        startActivity(intent);
                                    }
                                    else{
                                        Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                        textView.setVisibility(View.VISIBLE);
                                        textView.setText("Login UnSuccessfully");
                                    }

                                }
                            });
                        }
                    });
                }
            }
        });

    }

    public void register(View view){
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

}

