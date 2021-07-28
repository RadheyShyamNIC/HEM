package in.nic.hem;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.List;

import in.nic.hem.db.DBOperations;
import in.nic.hem.db.DataBaseHelper;

public class MainActivity extends AppCompatActivity {

    LinearLayout LLprofile_disp;

    BottomNavigationView myNavView;
    Fragment fragment;
    FragmentTransaction fragmentTransaction;
    TextView profile_name;
    ListView lv_profilechooser;
    DBOperations dbOperations;
    List<String[]> availableProfilesData;
    AlertDialog alertDialog;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getBaseContext();
        createDatabaseIfNotExist();
        LLprofile_disp = (LinearLayout)findViewById(R.id.profile_disp);
        profile_name = findViewById(R.id.profile_name);
        myNavView = findViewById(R.id.my_nav_view);
        myNavView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        fragment = new FragmentHome();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
        LLprofile_disp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Rect displayRectangle = new Rect();
                Window window = MainActivity.this.getWindow();
                window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.CustomAlertDialog);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.my_custom_popup_view, viewGroup, false);
                dialogView.setMinimumWidth((int)(displayRectangle.width() * 1f));
                dialogView.setMinimumHeight((int)(displayRectangle.height() * 1f));
                builder.setView(dialogView);
                alertDialog = builder.create();
                ////
                lv_profilechooser = dialogView.findViewById(R.id.lv_profilechooser);
                dbOperations = new DBOperations(context);
                availableProfilesData = dbOperations.getAvailableProfilesDeta();
                ArrayAdapter<String[]> adapter = new MyListAdapter();
                ListView list = (ListView) dialogView.findViewById(R.id.lv_profilechooser);
                list.setAdapter(adapter);
                /////
                alertDialog.show();
            }
        });
    }
    class MyListAdapter extends ArrayAdapter<String[]>{

        public MyListAdapter() {
            super(context, R.layout.listview_item_profilechooser, availableProfilesData);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if(convertView==null)
            {
                itemView = getLayoutInflater().inflate(R.layout.listview_item_profilechooser, parent, false);
            }
            final String[] currentItem = availableProfilesData.get(position);
            if((currentItem[2]).equals("1")){
                ((ImageView)itemView.findViewById(R.id.item_img)).setVisibility(View.VISIBLE);
                ((Button)itemView.findViewById(R.id.item_btn_setprofile)).setVisibility(View.INVISIBLE);

            }else{
                ((ImageView)itemView.findViewById(R.id.item_img)).setVisibility(View.INVISIBLE);
                ((Button)itemView.findViewById(R.id.item_btn_setprofile)).setVisibility(View.VISIBLE);
                ((Button)itemView.findViewById(R.id.item_btn_setprofile)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setThisProfileAsDefault(currentItem[0]);
                    }
                });
            }
            ((TextView)itemView.findViewById(R.id.profiletext)).setText(currentItem[1]);
            return itemView;
        }
    }

void setThisProfileAsDefault(String profileID){
        dbOperations = new DBOperations(context);
        dbOperations.setDefaultProfile(profileID);
        alertDialog.dismiss();
        profile_name.setText(dbOperations.getDefaultProfileName());
        navigateAndRelodFirstFragment();
}
    @Override
    protected void onPostResume() {
        super.onPostResume();
        DBOperations dbOperations = new DBOperations(getBaseContext());
        profile_name.setText(dbOperations.getDefaultProfileName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    private void createDatabaseIfNotExist(){
        try {
            new DataBaseHelper(getBaseContext()).createDataBaseIfNotExist();
        }catch (Exception ex){
            ex.printStackTrace();
            Toast.makeText(getBaseContext(),"Error !!!!! Unable to initialize database !!!", Toast.LENGTH_LONG).show();
        }
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.nav_home:
                    fragment = new FragmentHome();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container,fragment);
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentTransaction.commit();
                    return true;
                case R.id.nav_new:
                    fragment = new FragmentNewentry();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container,fragment);
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentTransaction.commit();
                    return true;
                case R.id.nav_report:
                    fragment = new FragmentReport();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container,fragment);
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentTransaction.commit();
                    return true;
                case R.id.nav_config:
                    fragment = new FragmentConfig();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container,fragment);
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentTransaction.commit();
                    return true;
            }
            return false;
        }
    };
    void navigateAndRelodFirstFragment(){
        finish();
        startActivity(getIntent());
    }
}