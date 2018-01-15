package nac.jlproducts.stockscan;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.zip.Inflater;

import nac.jlproducts.module;

/**
 * Created by nac on 8/1/2561.
 */

public class fMain extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_main,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView welcome = getView().findViewById(R.id.tvWelcome);
        welcome.setText("สวัสดีคุณ" + module.UserName);
        getActivity().setTitle(R.string.company);

    }

}
