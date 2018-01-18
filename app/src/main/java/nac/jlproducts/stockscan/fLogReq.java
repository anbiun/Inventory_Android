package nac.jlproducts.stockscan;


import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import nac.jlproducts.module;

public class fLogReq extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.layout_stock,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button matSelect = getView().findViewById(R.id.btListMat);
        Button dateSelect = getView().findViewById(R.id.btListLoc);

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
    }
}
