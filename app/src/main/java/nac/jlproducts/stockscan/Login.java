package nac.jlproducts.stockscan;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import nac.jlproducts.module;

public class Login extends AppCompatActivity{
    String TAG = "";
    String UserID = "";
    String UserPwd = "";

    String METHOD_NAME = "Login";

    EditText getUser = null;
    EditText getPwd = null;
    public ProgressDialog dialog = null;
    Intent currentPage = null;
    Boolean btnLogInClick = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //set object
        getUser = findViewById(R.id.txtUser);
        getPwd = findViewById(R.id.txtPwd);

        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserID = getUser.getText().toString();
                UserPwd = getPwd.getText().toString();

                module.METHOD_NAME = METHOD_NAME;
                module.ParamList.put("UserID",UserID);
                module.ParamList.put("UserPwd",UserPwd);
                dialog = new ProgressDialog(Login.this);

                if (btnLogInClick == false) {
                    AsyncCallWS task = new AsyncCallWS();
                    task.execute();
                }
            }
        });

        }
    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            btnLogInClick = true;
            dialog.setMessage(getResources().getString(R.string.dlg_loading));
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
            btnLogInClick = false;
            CheckLogin();
        }

    }

    public void CheckLogin(){
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        if (module.xcode.getCode() == module.xcode.Cantconnect){
            Toast.makeText(Login.this,"ติดต่อเครือข่ายไม่ได้",Toast.LENGTH_LONG).show();
            return;
        } else if (module.xcode.getCode() == module.xcode.Notfound) {
            Toast.makeText(Login.this,"ชื่อหรือรหัสผ่านไม่ถูกต้อง",Toast.LENGTH_LONG).show();
            return;
        }

        try {
            JSONArray JAR = new JSONArray(module.JSonResult);
            JSONObject JOB = JAR.getJSONObject(0);
            module.UserName = JOB.getString("UserName");
            Intent newPage = new Intent(this,Main.class);
            //Toast.makeText(this,"select location",Toast.LENGTH_LONG).show();
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

