package nac.jlproducts.stockscan;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import nac.jlproducts.module;
import static android.app.Activity.RESULT_OK;

public class fReq extends Fragment {
    private String TAG = "";
    private int REQUEST_OR_SCAN = 4;
    private TextView etTagID = null;
    final int task_gettag = 1;
    final int task_insert = 2;
    int task_current = 0;
    String Unit1_Name = "";
    String Unit3_Name = "";
    Double DBUnit1 = 0.0;
    Double DBUnit3 = 0.0;
    String Unit1_ID = "";
    String LocID = "";
    Double dRatio = 0.0;
    TextView tvMatName = null;
    String Unit1 = "";
    String Unit3 = "";
    module nModule = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_scan,container,false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("เบิกวัสดุ");
        nModule.TagID = "";
        setObj();
        setButton();
    }
    private void setObj() {
        etTagID = getView().findViewById(R.id.etTagID);
        tvMatName = getView().findViewById(R.id.tvMatName);
        nModule = new module();
        return;
    }
    private void setButton() {
        //BUTTON QR
        Button btQR = getView().findViewById(R.id.btQR);
        btQR.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                QRScan();
                //Scan.GetTagTask task = new Scan.GetTagTask();
            }
        });
        //BUTTON FIND
        Button btFind = getView().findViewById(R.id.btFind);
        btFind.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                findTag();
            }
        });
        //BUTTON REQ
        Button btReq = getView().findViewById(R.id.btRequisition);
        btReq.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (checkInput() == true) {
                    CFDialog();
                }
            }
        });

    }
    public class Async extends AsyncTask<Void, Void, Void> {
        protected void onPreExecute() {
            nModule.Dialog.setMessage("กำลังโหลด..");
            nModule.Dialog.setCanceledOnTouchOutside(false);
            nModule.Dialog.show();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "onPreExecute");
        }
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");
            nModule.Qry();
            return null;
        }
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute");
            onPostResult();
        }

    }
    private void onPostResult() {
        Integer result = 0;
        if (nModule.ResultString != null && !nModule.ResultString.toString().equals("[]")) {
            //Toast.makeText(getActivity(), "Response " + nModule.ResultString.toString(), Toast.LENGTH_LONG).show();
            nModule.Dialog.dismiss();
            result = 1;
        } else {
            nModule.Dialog.dismiss();
            result =0;
        }

        switch (task_current) {
            case task_gettag:
                if (result ==0){
                    Toast.makeText(getActivity(),"ไม่พบข้อมูลโปรดตรวจสอบ TagID",Toast.LENGTH_LONG).show();
                    break;
                }
                foundTag();
                break;
            case task_insert:
                if (result ==0) {
                    Toast.makeText(getActivity(),"บันทึกเบิกไม่ได้โปรดติดต่อผู้พัฒนาแอพ",Toast.LENGTH_LONG).show();
                    break;
                }
                Toast.makeText(getActivity(),"บันทึกข้อมูลเบิกแล้ว",Toast.LENGTH_LONG).show();
                Fragment fm = new fReq();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_main,fm);
                ft.commit();
                break;
        }


    }
    public void QRScan() {
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        startActivityForResult(Intent.createChooser(intent, "โปรดเลือกแอพสแกนคิวอาร์"), REQUEST_OR_SCAN);
    }
    public void onActivityResult(int reqCode, int resCode, Intent intent){
        if (reqCode == REQUEST_OR_SCAN && resCode == RESULT_OK) {
            String content = intent.getStringExtra("SCAN_RESULT");
            etTagID.setText(content);
            findTag();
        }

    }
    private void findTag() {
        nModule.SOAP_ACTION = "http://jlproducts.co.th:99/FindTag";
        nModule.METHOD_NAME = "FindTag";
        nModule.ParamList.clear();
        nModule.TagID = etTagID.getText().toString();
        nModule.ParamList.put("TagID",nModule.TagID);

        nModule.Dialog = new ProgressDialog(getActivity());
        task_current = task_gettag;
        Async task = new Async();
        task.execute();
    }
    private void  foundTag() {
        try {
            JSONArray JAR = new JSONArray(nModule.ResultString.toString());
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

            tvMatName.setText(JOB.getString("MatName"));
            TextView UnitResult = (TextView) getView().findViewById(R.id.tvUnit);
            UnitResult.setText(nModule.CombineString(UnitArr));
            //setUnitHint
            EditText Unit1 = (EditText) getView().findViewById(R.id.etUnit1);
            EditText Unit3 = (EditText) getView().findViewById(R.id.etUnit3);
            Unit1.setHint("เบิกเป็น" + JOB.getString("Unit1_Name"));
            Unit3.setHint("เบิกเป็น" + JOB.getString("Unit3_Name"));
            etTagID.setEnabled(false);

        } catch (JSONException e) {
            Toast.makeText(getActivity(),"ERROR " + e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
    @NonNull
    private Boolean checkInput() {
        EditText etUnit1 = getView().findViewById(R.id.etUnit1);
        EditText etUnit3 = getView().findViewById(R.id.etUnit3);
        Unit1 = etUnit1.getText().toString();
        Unit3 = etUnit3.getText().toString();
        //inputTagID
        if (nModule.TagID.isEmpty()) {
            Toast.makeText(getActivity(),"โปรดตรวจสอบ TagID",Toast.LENGTH_LONG).show();
            return false;
        }
        //inputNumber
        if (Unit1.toString().isEmpty() && Unit3.toString().isEmpty()) {
            Toast.makeText(getActivity(),"โปรดตรวจสอบจำนวนให้ถูกต้อง",Toast.LENGTH_LONG).show();
            return false;
        } else if (!Unit1.toString().isEmpty() && Unit3.toString().isEmpty()) {
            Unit3 = "0";
        } else if (!Unit3.toString().isEmpty() && Unit1.toString().isEmpty()) {
            Unit1 = "0";
        } else if (Integer.parseInt(Unit1) == 0 || Integer.parseInt(Unit3) == 0) {
            Toast.makeText(getActivity(),"จำนวนไม่ถูกต้อง",Toast.LENGTH_LONG).show();;
            return false;
        }
        //input > DB
        if (Double.parseDouble(Unit1) > DBUnit1 ||
                Double.parseDouble(Unit3) > DBUnit3){
            Toast.makeText(getActivity(), "ไม่สามารถเบิกเกินจำนวนได้", Toast.LENGTH_LONG).show();
            return false;
        } else if ( ((Integer.parseInt(Unit1) * dRatio.intValue()) + Integer.parseInt(Unit3)) > DBUnit3.intValue() ) {
            Toast.makeText(getActivity(), "ไม่สามารถเบิกเกินจำนวนได้", Toast.LENGTH_LONG).show();
            return false;
        }

        return  true;
    }
    private void CFDialog() {
        final AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        //Dialog

        adb.setTitle("ยืนยันการเบิก?");
        adb.setMessage("คุณต้องการเบิก" + tvMatName.getText().toString() +
                " จำนวน " + Unit1 + " " + Unit1_Name +
                " " + Unit3 + " " + Unit3_Name
        );

        adb.setNegativeButton("ไม่ใช่", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                // Cancel Event
            }
        });
        adb.setPositiveButton("ใช่", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                // OK Event
                nModule.SOAP_ACTION = "http://jlproducts.co.th:99/Requisition";
                nModule.METHOD_NAME = "Requisition";

                nModule.ParamList.clear();
                nModule.ParamList.put("TagID",nModule.TagID);
                nModule.ParamList.put("Unit1",Unit1);
                nModule.ParamList.put("Unit3",Unit3);
                nModule.ParamList.put("UserRequest", nModule.UserName);
                nModule.ParamList.put("Unit1_ID",Unit1_ID);
                nModule.ParamList.put("LocID",LocID);

                nModule.Dialog = new ProgressDialog(getActivity());
                task_current = task_insert;
                Async task = new Async();
                task.execute();
            }
        });
        adb.show();
    }
}
