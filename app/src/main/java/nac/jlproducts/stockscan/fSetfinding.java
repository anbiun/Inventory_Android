package nac.jlproducts.stockscan;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import nac.jlproducts.module;

public class fSetfinding extends Fragment {
    @Nullable
    //boolean findall = true;
    Switch swFind;
    TextView sDate;
    TextView eDate;
    TextView tvMat;
    public static module.findSeting fSetting;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fsetfinding,container,false);
        //super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.setfinding);
        swFind = getView().findViewById(R.id.swFindall);
        sDate = getView().findViewById(R.id.tvSdate);
        eDate = getView().findViewById(R.id.tvEdate);
        tvMat = getView().findViewById(R.id.tvMat);
        fSetting = new module.findSeting();


        swFind.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ConstraintLayout cLaydate = getView().findViewById(R.id.cLaydate);
                if (b) {
                    cLaydate.setVisibility(View.GONE);
                } else {
                    cLaydate.setVisibility(View.VISIBLE);
                }
                fSetting.findalldate =b;
            }
        });
        swFind.setChecked(true);

        sDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dateRange dRange = new dateRange(R.string.pram_sdate);
                dRange.show(sDate);
            }
        });

        eDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateRange dRange = new dateRange(R.string.pram_edate);
                dRange.show(eDate);
            }
        });
        tvMat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkDialog();
            }
        });

        Button btFind = getView().findViewById(R.id.btFind);
        btFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FindClick();
            }
        });

    }

    private class dateRange {
        private dateRange(int paramName) {
            this.modParamSelect = paramName;
        }

        int modParamSelect = 0;
        TextView tvs = null;
        Calendar dPick = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int yy, int mm, int dd) {
                dPick.set(Calendar.YEAR, yy);
                dPick.set(Calendar.MONTH, mm);
                dPick.set(Calendar.DAY_OF_MONTH, dd);
                updateResult();
            }
        };

        public void show(TextView inputTV) {
            tvs = inputTV;
                new DatePickerDialog(getActivity(), date,
                        dPick.get(Calendar.YEAR),
                        dPick.get(Calendar.MONTH),
                        dPick.get(Calendar.DAY_OF_MONTH)).show();
        }

        private void updateResult() {
            String result;
            String dFormat = "dd-MM-yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(dFormat);
            result = sdf.format(dPick.getTime());
            if (modParamSelect == R.string.pram_sdate) {
                fSetting.sel_sdate = result;
            } else if (modParamSelect == R.string.pram_edate) {
                fSetting.sel_edate = result;
            }
            tvs.setText(": " + result);
        }
    }
    ArrayList<String> matParam = new ArrayList<>();
    private void checkDialog()  {
        // setup the alert builder
        String title = "กรุณาเลือก";
        final HashMap<String,String> hParam = new HashMap<>();

            matParam.clear();
            title += "วัสดุ";
            hParam.put("ซอง","0303");
            hParam.put("ลังกระดาษ","0302");
            hParam.put("ถุงพลาสติก","0301");
            hParam.put("หิน","0304");
            hParam.put("บาร์โค๊ด","0305");
            hParam.put("กล่องโหล","0306");


        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        boolean[] checkedItems = null;
        builder.setTitle(title);

        //final String[] listDialog = hParam.keySet().toArray(new String[0]);
        final String[] listKey = hParam.keySet().toArray(new String[0]);
        checkedItems = new boolean[hParam.size()];
        Arrays.fill(checkedItems,false);

        builder.setMultiChoiceItems(listKey,checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item, boolean checkedItems) {
                // user checked or unchecked a box
                //param.put(item,listLoc[item]);

                if (checkedItems) {
                    matParam.add(hParam.get(listKey[item]));
                } else {
                    matParam.remove(hParam.get(listKey[item]));

                }
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                TextView tv = getView().findViewById(R.id.tvMat);
                    tv.setText(getCurselect(matParam,hParam));
                    fSetting.sel_mat = tv.getText().toString();
                    addtoMatID(matParam);
            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void addtoMatID(ArrayList<String> matParam){
        String newMatIDlist;
        newMatIDlist = matParam.toString()
                .replace("[","")
                .replace("]","")
                .replace(" ","");
        fSetting.sel_matID = newMatIDlist;
    }
    private String getCurselect(ArrayList<String> inputParam, HashMap<String,String> hParam) {
        ArrayList<String> slect = new ArrayList<>();

        for (String item : inputParam) {
            for (Map.Entry<String, String> value : hParam.entrySet()) {
                if (value.getValue().equals(item)) {
                    slect.add(value.getKey());
                }
            }
        }
        return slect.toString().replace("[","").replace("]","");
    }
    private void FindClick() {

        if (fSetting.sel_mat.isEmpty()) {
            Toast.makeText(getActivity(),"เลือกวัสดุ",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!fSetting.findalldate) {
            if (fSetting.sel_mat.isEmpty()
                || fSetting.sel_sdate.isEmpty()
                || fSetting.sel_edate.isEmpty()) {
                Toast.makeText(getActivity(), "ข้อมูลไม่ครบ", Toast.LENGTH_SHORT).show();
                return;
            }
        } else { fSetting.sel_sdate = "ค้นหาทั้งหมด"; fSetting.sel_edate = ""; }

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.content_main,new fLogReq(),"LogReq");
        ft.addToBackStack(null).commit();
    }

 }


