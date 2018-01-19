package nac.jlproducts.stockscan;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import nac.jlproducts.module;

public class fLogReq extends Fragment {
    module nMod = new module();
    ListView lvRow;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.layout_stock,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.nav_log_requisition);
        Button matSelect = getView().findViewById(R.id.btListMat);
        Button dateSelect = getView().findViewById(R.id.btListLoc);
        lvRow = getView().findViewById(R.id.lvStock);

        matSelect.setText(fSetfinding.fSetting.sel_mat);
        dateSelect.setText(fSetfinding.fSetting.sel_sdate);

        if (!fSetfinding.fSetting.findalldate) {
            String dSel = "วันที่ : " + fSetfinding.fSetting.sel_sdate;
            dSel += System.getProperty("line.separator");
            dSel += "ถึงวันที่ : " + fSetfinding.fSetting.sel_edate;
            dateSelect.setText(dSel);
        }
        matSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });
        dateSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });
        getLogReq();
    }
    private void getLogReq() {
        module nMod = new module();
        nMod.METHOD_NAME = "LogReq";
        nMod.ParamList.clear();
        nMod.ParamList.put("IDValue",fSetfinding.fSetting.sel_matID);
        nMod.ParamList.put("eDate",fSetfinding.fSetting.sel_edate);
        nMod.ParamList.put("sDate",fSetfinding.fSetting.sel_sdate);
        nMod.Dialog = new ProgressDialog(getActivity());
        Async task = new Async();
        task.execute();

    }
    public class Async extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            nMod.Dialog.setMessage(getResources().getString(R.string.dlg_loading));
            nMod.Dialog.setCanceledOnTouchOutside(false);
            nMod.Dialog.show();
            //super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            nMod.Qry();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //super.onPostExecute(aVoid);
            bindLV();
        }
    }
    private void bindLV() {
        if (nMod.Dialog != null && nMod.Dialog.isShowing()) {
            nMod.Dialog.dismiss();
        } else {
            if (module.xcode.getCode() == module.xcode.Notfound) {
                Toast.makeText(getActivity(), R.string.xcode_notfound, Toast.LENGTH_LONG).show();
                return;
            }
        }

        if (module.xcode.getCode() == module.xcode.Cantconnect) {
            Toast.makeText(getActivity(),"ติดต่อไม่ได้",Toast.LENGTH_LONG).show();
            return;
        }
        if (module.xcode.getCode() == module.xcode.Success) {

            try {
                //JSONArray data = new JSONArray(getJSON(url,params));
                JSONArray JAR = new JSONArray(nMod.JSonResult);

                ArrayList<HashMap<String, String>> MyArrList = new ArrayList<>();
                HashMap<String, String> map;

                for (int i = 0; i < JAR.length(); i++) {
                    JSONObject c = JAR.getJSONObject((i));
                    map = new HashMap<>();

                    map.put("cMat", c.getString("MatName"));
                    map.put("cAmount", c.getString("Amount"));
                    map.put("cTagID", c.getString("TagID"));
                    map.put("cReqUser", c.getString("ReqUser"));

                    try {
                        SimpleDateFormat dfm = new SimpleDateFormat("yyyy-MM-dd");
                        Date imDate = dfm.parse(c.getString("ReqDate"));
                        dfm.applyPattern("dd-MM-yyyy");
                        map.put("cReqDate", "วันที่เบิก : " + dfm.format(imDate));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    MyArrList.add(map);
                }


                SimpleAdapter simpleAdapterData;
                simpleAdapterData = new SimpleAdapter(getActivity(), MyArrList, R.layout.listview_item_logreq,
                        new String[]{"cMat", "cAmount", "cTagID", "cReqUser", "cReqDate"}
                        , new int[]{R.id.colMat, R.id.colAmount, R.id.colTagID, R.id.colReqUser, R.id.colReqDate});
                lvRow.setAdapter(simpleAdapterData);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
