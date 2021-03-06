package nac.jlproducts.stockscan;

import android.app.ProgressDialog;
import android.content.Intent;
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

import nac.excode;

public class Login extends AppCompatActivity{
    String TAG = "";
    SoapPrimitive resultString;
    String UserID = "";
    String UserPwd = "";

    String SOAP_ACTION = "http://jlproducts.co.th:99/Login";
    String METHOD_NAME = "Login";
    String NAMESPACE = "http://jlproducts.co.th:99";
    String URL = "http://jlproducts.co.th:99/Android/Android.asmx";
    SoapObject request = null;
    Map<String,String> ParamList = new HashMap<String,String>();

    EditText getUser = null;
    EditText getPwd = null;
    public ProgressDialog dialog = null;
    excode EXCODE = new excode();
    Intent currentPage = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //set object
        getUser = findViewById(R.id.txtUser);
        getPwd = findViewById(R.id.txtPwd);
        currentPage = new Intent(Login.this,Login.class);
        currentPage.putExtra("Page","Login");

        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserID = getUser.getText().toString();
                UserPwd = getPwd.getText().toString();

                ParamList.put("UserID",UserID);
                ParamList.put("UserPwd",UserPwd);
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
            Qry();
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {

            Log.i(TAG, "onPostExecute");
            CheckLogin();
        }

    }
    public void Qry() {
        try {
            request = new SoapObject(NAMESPACE,METHOD_NAME);
            //Map<String, Label> map = new HashMap<String, Label>();
            /*
            for ( String key : ParamList.keySet() ) {

            }

            for ( String value : ParamList.values() ) {
            }
**/

            for ( Map.Entry<String, String> entry : ParamList.entrySet() ) {
                String key = entry.getKey();
                String value = entry.getValue();
                request.addProperty(key,value);
            }

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(request);

            HttpTransportSE transport = new HttpTransportSE(URL);

            transport.call(SOAP_ACTION, soapEnvelope);
            resultString = (SoapPrimitive) soapEnvelope.getResponse();

            Log.i(TAG, "Result From WebService: " + resultString);
            EXCODE.Setcod(EXCODE.Success);
        } catch (Exception ex) {
            if (ex.getMessage().contains("unexpected end of stream")) {
                EXCODE.Setcod(EXCODE.Cantconnect);
            } else if (ex.getMessage().contains("Unable to resolve host")) {
                EXCODE.Setcod(EXCODE.Cantconnect);
            } else if (ex.getMessage().contains("Server was unable to process request.")) {
                EXCODE.Setcod(EXCODE.CantInsert);
            }
            resultString = null;
            Log.e(TAG, "Error: " + ex.getMessage());
        }

    }
    public void CheckLogin(){
        String getVal = "";
        if (EXCODE.Getcode() == EXCODE.Cantconnect){
            dialog.dismiss();
            Toast.makeText(Login.this,"ติดต่อเครือข่ายไม่ได้",Toast.LENGTH_LONG).show();
            return;
        } else if (EXCODE.Getcode() == 0 && resultString.toString().equals("[]")) {
            dialog.dismiss();
            Toast.makeText(Login.this,"ชื่อหรือรหัสผ่านไม่ถูกต้อง",Toast.LENGTH_LONG).show();
            return;
        }

        try {
            dialog.dismiss();
            JSONArray JAR = new JSONArray(resultString.toString());
            JSONObject JOB = JAR.getJSONObject(0);
            getVal = JOB.getString("UserName");
            Intent newPage = new Intent(this,Scan.class);
            newPage.putExtra("UserName",getVal);
            startActivity(newPage);

        } catch (JSONException e) {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
            Log.e(TAG,e.getMessage());
        }
    }
    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce || !currentPage.getStringExtra("Page").equals("Login")) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "โปรดแตะอีกครั้งออกจากโปรแกรม", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

}

