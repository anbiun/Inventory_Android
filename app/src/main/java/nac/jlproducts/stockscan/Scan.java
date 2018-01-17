package nac.jlproducts.stockscan;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import nac.jlproducts.module;


/**
 * Created by nac on 14/12/2560.
 */

public class Scan extends Login {
    EditText TAGID = null;
    int REQUEST_OR_SCAN = 4;
    String JSon = "";
    Button btFind = null;
    Button btQRScan = null;
    Button btReq = null;
    //String TAG = "";
    TextView tvMatname = null;
    EditText etUnit1 = null;
    EditText etUnit3 = null;
    String Unit1_Name = "";
    String Unit3_Name = "";
    String Unit1 = "";
    String Unit3 = "";
    Double DBUnit1 = 0.0;
    Double DBUnit3 = 0.0;
    String UserName = "";
    String Unit1_ID = "";
    String LocID = "";
    Double dRatio = 0.0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
    //onLoad
        currentPage = new Intent(Scan.this,Scan.class);
        currentPage.putExtra("Page","Scan");
        btFind =  findViewById(R.id.btFind);
        btQRScan = findViewById(R.id.btQR);
        btReq =  findViewById(R.id.btRequisition);
        TAGID = findViewById(R.id.etTagID);
        tvMatname = findViewById(R.id.tvMatName);
        etUnit1 = findViewById(R.id.etUnit1);
        etUnit3 = findViewById(R.id.etUnit3);

        //getUsername
        Intent getUserName = getIntent();
        UserName = getUserName.getStringExtra("UserName");
        tvMatname.setText("สวัสดีคุณ" + UserName);

        //ButtonQR
        btQRScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QRScan();
            }
        });

        //ButtonFind
        btFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SOAP_ACTION = "http://jlproducts.co.th:99/FindTag";
                METHOD_NAME = "FindTag";
                module.ParamList.clear();
                module.ParamList.put("TagID",TAGID.getText().toString());

                dialog = new ProgressDialog(Scan.this);
                GetTagTask task = new GetTagTask();
                task.execute();
            }
        });

        //ButtonReq
        btReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkInput() == true) {
                    CFDialog();
                }
        }
        });

    }
    private Boolean checkInput() {
        Unit1 = etUnit1.getText().toString();
        Unit3 = etUnit3.getText().toString();
        //inputTagID
        if (TAGID.getText().toString().isEmpty()) {
            Toast.makeText(Scan.this,"โปรดตรวจสอบ TagID",Toast.LENGTH_LONG).show();
            return false;
        }
        //inputNumber
        if (Unit1.toString().isEmpty() && Unit3.toString().isEmpty()) {
            Toast.makeText(Scan.this,"โปรดตรวจสอบจำนวนให้ถูกต้อง",Toast.LENGTH_LONG).show();
            return false;
        } else if (!Unit1.toString().isEmpty() && Unit3.toString().isEmpty()) {
            Unit3 = "0";
        } else if (!Unit3.toString().isEmpty() && Unit1.toString().isEmpty()) {
            Unit1 = "0";
        }
        //input > DB
        if (Double.parseDouble(Unit1) > DBUnit1 ||
                Double.parseDouble(Unit3) > DBUnit3){
                Toast.makeText(Scan.this, "ไม่สามารถเบิกเกินจำนวนได้", Toast.LENGTH_LONG).show();
            return false;
        } else if ( ((DBUnit1.intValue() * dRatio.intValue()) + Integer.parseInt(Unit1)) > DBUnit3.intValue() ) {
                Toast.makeText(Scan.this, "ไม่สามารถเบิกเกินจำนวนได้", Toast.LENGTH_LONG).show();
            return false;
        }

        return  true;
    }
    private void CFDialog() {
        final AlertDialog.Builder adb = new AlertDialog.Builder(this);
        //Dialog

        adb.setTitle("ยืนยันการเบิก?");
        adb.setMessage("คุณต้องการเบิก" + tvMatname.getText().toString() +
                " จำนวน " + Unit1 + " " + Unit1_Name +
                " กับเศษ " + Unit3 + " " + Unit3_Name
        );

        adb.setNegativeButton("ไม่ใช่", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                // Cancel Event

            }
        });
        adb.setPositiveButton("ใช่", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                // OK Event
                SOAP_ACTION = "http://jlproducts.co.th:99/Requisition";
                METHOD_NAME = "Requisition";
                module.ParamList.clear();
                module.ParamList.put("TagID",TAGID.getText().toString());
                module.ParamList.put("Unit1",Unit1);
                module.ParamList.put("Unit3",Unit3);
                module.ParamList.put("UserRequest", UserName);
                module.ParamList.put("Unit1_ID",Unit1_ID);
                module.ParamList.put("LocID",LocID);

                dialog = new ProgressDialog(Scan.this);
                InsertTask task = new InsertTask();
                task.execute();
            }
        });
        adb.show();
    }
    private String CombineString(String[] StringArr) {
        String result = "";
        for (int i = 0;i < StringArr.length;i++) {
            if (i == StringArr.length - 1) {
                result += StringArr[i].toString();
                return result;
            }
            result += StringArr[i].toString() + " ";
        }
        return result;
    }
    private void  FoundTagID() {
    try {
        JSONArray JAR = new JSONArray(module.ResultString.toString());
        JSONObject JOB = JAR.getJSONObject(0);
        Unit1_Name = JOB.getString("Unit1_Name");
        Unit3_Name = JOB.getString("Unit3_Name");
        DBUnit1 = JOB.getDouble("Unit1");
        DBUnit3 = JOB.getDouble("Unit3");
        Unit1_ID = JOB.getString("Unit1_ID");
        LocID = JOB.getString("LocID");
        dRatio = JOB.getDouble("Ratio");

        String[] UnitArr = {
                DBUnit1.toString(),
                Unit1_Name,
                " เป็นปริมาณ ",
                DBUnit3.toString(),
                Unit3_Name
                };

        tvMatname.setText(JOB.getString("MatName"));
        TextView Unitresult = (TextView) findViewById(R.id.tvUnit);
        Unitresult.setText(CombineString(UnitArr));
        //setUnitHint
        EditText Unit1 = (EditText) findViewById(R.id.etUnit1);
        EditText Unit3 = (EditText) findViewById(R.id.etUnit3);
        Unit1.setHint("เบิกเป็น" + JOB.getString("Unit1_Name"));
        Unit3.setHint("เบิกเป็น" + JOB.getString("Unit3_Name"));
        TAGID.setEnabled(false);

    } catch (JSONException e) {
        Toast.makeText(this,"ERROR " + e.getMessage(),Toast.LENGTH_LONG).show();
    }
}
    private void QRScan() {
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        startActivityForResult(Intent.createChooser(intent, "โปรดเลือกแอพสแกนคิวอาร์"), REQUEST_OR_SCAN);
    }
    protected void onActivityResult(int reqCode,int resCode,Intent intent){
        if (reqCode == REQUEST_OR_SCAN && resCode == RESULT_OK) {
            String content = intent.getStringExtra("SCAN_RESULT");
            TAGID.setText(content);
            btFind.performClick();
        }

    }
    public class GetTagTask extends AsyncTask<Void, Void, Void> {
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
            if (module.ResultString != null && !module.ResultString.toString().equals("[]")) {
                //Toast.makeText(Login.this, "Response " + resultString.toString(), Toast.LENGTH_LONG).show();
                dialog.dismiss();
                FoundTagID();
            } else {
                dialog.dismiss();
                Toast.makeText(Scan.this,"ไม่พบข้อมูลโปรดตรวจสอบ TagID",Toast.LENGTH_LONG).show();
            }

        }

    }
    public class InsertTask extends AsyncTask<Void, Void, Void> {
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
            if (module.xcode.getCode() == module.xcode.Success) {
                dialog.dismiss();
                Toast.makeText(Scan.this, "บันทึกข้อมูลเบิกแล้ว", Toast.LENGTH_LONG).show();
                finish();
                startActivity(getIntent());
            } else {
                dialog.dismiss();
                Toast.makeText(Scan.this,"บันทึกข้อมูลไม่สำเร็จโปรดติดต่อผู้พัฒนา",Toast.LENGTH_LONG).show();
            }

        }

    }
}


