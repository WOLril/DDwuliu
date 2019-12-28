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
import java.text.SimpleDateFormat;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }
    public void registerUser(View view){
        Intent intent = new Intent();
        EditText user_account = (EditText) findViewById(R.id.register_account);
        String account = user_account.getText().toString();
        EditText user_name = (EditText) findViewById(R.id.register_name);
        String name = user_name.getText().toString();
        Log.v("thisasa", name);
        EditText user_pwd1 = (EditText) findViewById(R.id.register_pwd1);
        String pwd1 = user_pwd1.getText().toString();
        EditText user_pwd2 = (EditText) findViewById(R.id.register_pwd2);
        String pwd2 = user_pwd2.getText().toString();
        final TextView result = (TextView) findViewById(R.id.result);
        if(TextUtils.isEmpty(account)){
            Toast.makeText(RegisterActivity.this,"The Account is not null",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(name)){
            Toast.makeText(RegisterActivity.this,"The UserName is not null",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(pwd1)){
            Toast.makeText(RegisterActivity.this,"The UserPwd1 is not null",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(pwd2)){
            Toast.makeText(RegisterActivity.this,"The UserPwd2 is not null",Toast.LENGTH_SHORT).show();
        }
        else if(!pwd1.equals(pwd2)){
            Toast.makeText(RegisterActivity.this,"You should input same password",Toast.LENGTH_SHORT).show();
        }
        else{
            //初始化okhttp客户端
            OkHttpClient client = new OkHttpClient.Builder().build();
            Date currentTime = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = formatter.format(currentTime);
            //创建post表单，获取username和password（没有做非空判断）
            RequestBody post = new FormBody.Builder()
                    .add("account",account)
                    .add("username", name)
                    .add("userpwd",pwd1)
                    .add("register_time", dateString)
                    .build();
            //开始请求，填入url，和表单
            final Request request = new Request.Builder()
                    //10.0.2.2为模拟机访问本地服务器需要填的ip地址，对于真机等情况可以看我之前的博客
                    .url("http://192.168.1.105:8080/MySQL/SQL/register.php")
                    .post(post)
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    e.printStackTrace();
                }
                //获取成功
                @Override
                public void onResponse(okhttp3.Call call, final okhttp3.Response response) throws IOException {
                    //UI线程运行
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RegisterResult registerResult = null;
                            try {
                                //获取json
                                String responseStr = response.body().string();
                                Log.v("asfsafasfasfasfas", responseStr);
                                JSONObject jsonObject = new JSONObject(responseStr);
                                String jsonStr = jsonObject.toString();
                                Gson gson = new Gson();
                                registerResult = gson.fromJson(jsonStr, RegisterResult.class);
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
                            if (registerResult!=null&&registerResult.getResult()>0){
                                Bundle bundle = new Bundle();
                                bundle.putInt("username", registerResult.getResult());
                                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                            else if(registerResult!=null&&registerResult.getError().equals("username")) {
                                result.setText("The username has already existed");
                                result.setVisibility(View.VISIBLE);
                            }
                            else if(registerResult!=null&&registerResult.getError().equals("account")){
                                result.setText("The account has already existed");
                                result.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            });
        }


    }
}

