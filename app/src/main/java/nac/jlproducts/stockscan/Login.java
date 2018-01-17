package nac.jlproducts.stockscan;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.HashMap;
import java.util.Map;

import nac.jlproducts.module;
import nac.jlproducts.excode;

public class Login extends AppCompatActivity{
    String TAG = "";
    String UserID = "";
    String UserPwd = "";

    String SOAP_ACTION = "http://jlproducts.co.th:99/Login";
    String METHOD_NAME = "Login";

    EditText getUser = null;
    EditText getPwd = null;
    public ProgressDialog dialog = null;
    Intent currentPage = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //set object
        getUser = findViewById(R.id.txtUser);
        getPwd = findViewById(R.id.txtPwd);

        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserID = getUser.getText().toString();
                UserPwd = getPwd.getText().toString();

                module.SOAP_ACTION = SOAP_ACTION;
                module.METHOD_NAME = METHOD_NAME;
                module.ParamList.put("UserID",UserID);
                module.ParamList.put("UserPwd",UserPwd);
                dialog = new ProgressDialog(Login.this);


                AsyncCallWS task = new AsyncCallWS();
                task.execute();
            }
        });

        }
    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            dialog.setMessage("กำลังโหลด..");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            try {
               Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");
            module.Qry();
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute");
            CheckLogin();
        }

    }

    public void CheckLogin(){
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        if (module.xcode.getCode() == excode.Cantconnect){
            Toast.makeText(Login.this,"ติดต่อเครือข่ายไม่ได้",Toast.LENGTH_LONG).show();
            return;
        } else if (module.xcode.getCode() == module.xcode.Notfound) {
            Toast.makeText(Login.this,"ชื่อหรือรหัสผ่านไม่ถูกต้อง",Toast.LENGTH_LONG).show();
            return;
        }

        try {
            JSONArray JAR = new JSONArray(module.ResultString.toString());
            JSONObject JOB = JAR.getJSONObject(0);
            module.UserName = JOB.getString("UserName");
            Intent newPage = new Intent(this,Main.class);
            startActivity(newPage);
            finish();

        } catch (JSONException e) {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
            Log.e(TAG,e.getMessage());
        }
    }
    @Override
    public void onBackPressed() {
        if (module.backPress() == false) {
            Toast.makeText(this, R.string.exit_cf, Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }

}

