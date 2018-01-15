package nac.jlproducts.stockscan;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import nac.jlproducts.module;

public class Main extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Fragment fm = null;
    FragmentTransaction ft = null;
    //getFragmentManager().beginTransaction();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navHeader = navigationView.getHeaderView(0);
        TextView tvWelcome = navHeader.findViewById(R.id.tv_nav_welcome);
        tvWelcome.setText("สวัสดีคุณ " + module.UserName);

        ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_main,new fMain()).commit();
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else {
            ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_main,new fMain(),"main").commit();

            Fragment fm = getFragmentManager().findFragmentByTag("main");
            if (fm != null && fm.isVisible()) {
                // add your code here
                if (module.backPress() == false) {
                    Toast.makeText(this,R.string.exit_cf, Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                }
            }


            if (getTitle().toString().equals(R.string.title_activity_main)) {

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        displayChange(id);
        return true;
    }

    public void displayChange(int id) {

        switch (id) {
            case R.id.nav_requisition:
                fm = new fReq();
                break;
            case R.id.nav_changepwd:
                break;
            case R.id.nav_stock_tag:
                fm = new fStock_Tag();
                break;
            case R.id.nav_log_requisition:
                fm = new fLogReq();
                break;
            case R.id.nav_exit:
                finish();
                break;
        }

        if (fm != null) {
            //FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_main, fm).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }
}


