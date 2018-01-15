package nac.jlproducts.stockscan;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import nac.jlproducts.module;

public class fStock_Tag extends Fragment {
    module newMod = new module();
    ListView lvStock = null;
    ArrayList<String> locParam = new ArrayList<>();
    ArrayList<String> matParam = new ArrayList<>();

    String[] listDialog = null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        return  inflater.inflate(R.layout.layout_stock,container,false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.nav_stock_tag);

        Button btListLoc = getView().findViewById(R.id.btListLoc);
        final Button btListMat = getView().findViewById(R.id.btListMat);
        lvStock = getView().findViewById(R.id.lvStock);

        btListLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkDialog(checkLoc);

            }
        });
        btListMat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkDialog(checkMat);
            }
        });
    }
    private void getStock() {
        newMod.METHOD_NAME = "stock_tag_byall";
        newMod.SOAP_ACTION = newMod.NAMESPACE + "/" + newMod.METHOD_NAME;
        newMod.ParamList.clear();
        newMod.ParamList.put("IDValue",matParam.toString()
                        .replace("[","")
                        .replace("]","")
                            );
        newMod.ParamList.put("LocID",locParam.toString()
                .replace("[","")
                .replace("]","")
                             );
        newMod.ParamList.put("Stat","1");

        newMod.Dialog = new ProgressDialog(getActivity());
        Async task = new Async();
        task.execute();
    }
    public class Async extends AsyncTask<Void, Void, Void> {
        protected void onPreExecute() {
            newMod.Dialog.setMessage("กำลังโหลด..");
            newMod.Dialog.setCanceledOnTouchOutside(false);
            newMod.Dialog.show();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.i("onPre", "onPreExecute");
        }
        protected Void doInBackground(Void... params) {
            Log.i("doBack", "doInBackground");
            newMod.Qry();
            return null;
        }
        protected void onPostExecute(Void result) {
            Log.i("onPost", "onPostExecute");
            bindLV();
        }

    }
    private void bindLV() {
        if (newMod.Dialog != null && newMod.Dialog.isShowing()) {
            newMod.Dialog.dismiss();
        } else {
            if (module.xcode.getCode() == module.xcode.Notfound) {
                Toast.makeText(getActivity(), R.string.xcode_notfound, Toast.LENGTH_LONG).show();
                return;
            }
        }

        try {
            //JSONArray data = new JSONArray(getJSON(url,params));
            JSONArray JAR = new JSONArray(newMod.ResultString.toString());

            ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> map;

            for (int i = 0; i < JAR.length(); i++) {
                JSONObject c = JAR.getJSONObject((i));
                map = new HashMap<String, String>();

                map.put("r1col1", c.getString("TagID") + " " + c.getString("MatName"));
                map.put("r1col2",c.getString("Unit1") + " " + c.getString("UnitName"));
                map.put("r2col2",c.getString("LocName"));

                try {
                    SimpleDateFormat dfm = new SimpleDateFormat("yyyy-MM-dd");
                    Date importdate = dfm.parse(c.getString("ImportDate"));
                    dfm.applyPattern("dd-MM-yyyy");
                    map.put("r2col1","วันที่นำเข้า " + dfm.format(importdate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                MyArrList.add(map);
            }


            SimpleAdapter simpleAdapterData;
            simpleAdapterData = new SimpleAdapter(getActivity(), MyArrList, R.layout.layout_listrow,
                    new String[]{"r1col1","r1col2","r2col1","r2col2"}
                    , new int[]{R.id.r1col1,R.id.r1col2,R.id.r2col1,R.id.r2col2});
            lvStock.setAdapter(simpleAdapterData);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    int checkMat = 0;
    int checkLoc = 1;

    private void checkDialog(final int cMode)  {
        // setup the alert builder
        String title = "กรุณาเลือก";
        final HashMap<String,String> hParam = new HashMap<>();

        if (cMode == checkMat) {
            matParam.clear();
            title += "วัสดุ";
            hParam.put("ซอง","0303");
            hParam.put("ลังกระดาษ","0302");
            hParam.put("ถุงพลาสติก","0301");
            hParam.put("หิน","0304");
            hParam.put("บาร์โค๊ด","0305");
            hParam.put("กล่องโหล","0306");
        } else {
            locParam.clear();
            title += "สถานที่";
            hParam.put("JL","Loc001");
            hParam.put("Kiwi","Loc002");
            hParam.put("JLK","Loc003");
        }


        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        boolean[] checkedItems = null;

        listDialog = hParam.keySet().toArray(new String[0]);
        checkedItems = new boolean[hParam.size()];
        Arrays.fill(checkedItems,false);

        builder.setMultiChoiceItems(listDialog,checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item, boolean checkedItems) {
                // user checked or unchecked a box
                //param.put(item,listLoc[item]);
                if (checkedItems == true && cMode == checkMat) {
                    matParam.add(hParam.get(listDialog[item]));
                } else if (checkedItems == true && cMode == checkLoc) {
                    locParam.add(hParam.get(listDialog[item]));
                } else if (checkedItems == false && cMode == checkMat) {
                    matParam.remove(hParam.get(listDialog[item]));
                } else if (checkedItems == false && cMode == checkLoc) {
                    locParam.remove(hParam.get(listDialog[item]));
                }
            }
        });

        // add OK and Cancel buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                // user clicked OK
                //Toast.makeText(getActivity(),listLoc[0],Toast.LENGTH_LONG).show();
                //getStock();
                Button btListMat = getView().findViewById(R.id.btListMat);
                Button btListLoc = getView().findViewById(R.id.btListLoc);
                if (cMode == checkMat) {
                    btListMat.setText(getCurselect(matParam,hParam));
                } else if (cMode == checkLoc) {
                    btListLoc.setText(getCurselect(locParam,hParam));
                }
                getStock();
            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private String getCurselect(ArrayList<String> inputParam,HashMap<String,String> hParam) {
        ArrayList<String> slect = new ArrayList<>();
        //Map<String, String> testMap = new HashMap<String,String>();
        //testMap = hParam;

        for (String item : inputParam) {
            for (Map.Entry<String, String> value : hParam.entrySet()) {
                if (value.getValue().equals(item)) {
                    slect.add(value.getKey());
                }
            }
        }
        return slect.toString().replace("[","").replace("]","");
    }
}


