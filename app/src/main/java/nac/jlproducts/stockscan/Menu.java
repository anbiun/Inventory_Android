package nac.jlproducts.stockscan;

import android.content.Intent;
import android.os.Bundle;

/**
 * Created by nac on 27/12/2560.
 */

public class Menu extends Login {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        currentPage = new Intent(Menu.this,Menu.class);
        currentPage.putExtra("Page","Menu");

    }
}
