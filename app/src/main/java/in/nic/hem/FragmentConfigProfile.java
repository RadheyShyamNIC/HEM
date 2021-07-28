package in.nic.hem;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import in.nic.hem.db.DBOperations;
import in.nic.hem.mypkg.DropDownItem;

public class FragmentConfigProfile extends Fragment {
    LinearLayout layout_add_new_profile, layout_transparent_gray_background,layout_delete_profile;
    ImageButton btn_add_profile,btn_back_newprofile,btn_back_deleteprofile;
    TextView tvdelete_profile;
    Button btn_save_profile,btn_profile_delete1,btn_profile_delete2;
    EditText et_new_profile_name;
    Spinner merge_spinner_profile;
    List<DropDownItem> profileList,profileList1;
    DBOperations dbOperations;
    Context context;
    int action_profile_id=0;
    String action_profile_text="";

    public FragmentConfigProfile() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshListView();
    }
    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v.getId()==R.id.lv_profiles)////ListView id.
        {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle("Choose action: ");
            String[] menuItem=getResources().getStringArray(R.array.menu_listview_longpress_action);
            for(int i=0;i<menuItem.length;i++)
            {
                menu.add(Menu.NONE,i,i,menuItem[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        DropDownItem selectdListItem = profileList.get(info.position);
        action_profile_id = selectdListItem.getItemID();
        action_profile_text = selectdListItem.getItemText();
        int menuItemIndex= item.getItemId();
        if(menuItemIndex==0)
        {
            //Edit Selected Item
            showPopup();
        }
        else if(menuItemIndex==1)
        {
            //Delete Selected Item
            int defaultprofileid = new DBOperations(getContext()).getDefaultProfileID();
            if(defaultprofileid==action_profile_id){
                Toast.makeText(getContext(),"Default Profile Cannot be Deleted", Toast.LENGTH_SHORT).show();
                return true;
            }
            showPopup1();
        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_config_profile, container, false);
        btn_add_profile = (ImageButton) view.findViewById(R.id.btn_add_profile);
        btn_back_newprofile = (ImageButton) view.findViewById(R.id.btn_back_newprofile);
        btn_back_deleteprofile = (ImageButton) view.findViewById(R.id.btn_back_deleteprofile);
        layout_add_new_profile = (LinearLayout) view.findViewById(R.id.layout_add_new_profile);
        layout_transparent_gray_background = (LinearLayout) view.findViewById(R.id.layout_transparent_gray_background);
        layout_delete_profile = (LinearLayout) view.findViewById(R.id.layout_delete_profile);
        et_new_profile_name = (EditText) view.findViewById(R.id.et_new_profile_name);
        btn_save_profile = (Button) view.findViewById(R.id.btn_save_profile);
        tvdelete_profile = (TextView) view.findViewById(R.id.tvdelete_profile);
        merge_spinner_profile = (Spinner)view.findViewById(R.id.merge_spinner_profile);
        btn_profile_delete1 = (Button) view.findViewById(R.id.btn_profile_delete1);
        btn_profile_delete2 = (Button) view.findViewById(R.id.btn_profile_delete2);
        btn_add_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action_profile_id=-1;
                action_profile_text = "";
                showPopup();
            }
        });
        btn_back_newprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hidePopup();
            }
        });
        btn_back_deleteprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hidePopup1();
            }
        });
        btn_save_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String profile_name = et_new_profile_name.getText().toString();
                if(profile_name!=null && profile_name.length()>2){
                    dbOperations = new DBOperations(getContext());
                    boolean result = dbOperations.saveNewExpenditureProfile(profile_name,action_profile_id);
                    if(result){
                        hidePopup();
                        refreshListView();
                        Toast.makeText(getContext(),"New Profile saved successfully", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getContext(),"Oops!! Error occured", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getContext(),"Please enter a valid profile name", Toast.LENGTH_LONG).show();
                }
            }
        });
        btn_profile_delete1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbOperations = new DBOperations(getContext());
                dbOperations.deleteProfile(action_profile_id);
                hidePopup1();
                refreshListView();
            }
        });
        btn_profile_delete2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DropDownItem dropDownItem = (DropDownItem) merge_spinner_profile.getSelectedItem();
                int profileId = dropDownItem.getItemID();
                if(profileId<=0)
                {
                    Toast.makeText(getContext(), "Please Select Profile",Toast.LENGTH_SHORT).show();
                    return;
                }
                dbOperations.deleteProfileAndMergeEntries(action_profile_id,profileId);
                hidePopup1();
                refreshListView();
            }
        });
        return view;
    }
    private void refreshListView() {
        dbOperations = new DBOperations(getContext());
        try
        {
            profileList = dbOperations.getProfileList();
            populate_list_view();
            if(profileList==null || profileList.isEmpty()||profileList.size()==0)
            {
                Toast.makeText(getActivity(), "No Expenditure Profile Found. Add a Profile First", Toast.LENGTH_SHORT).show();
            }
        }
        catch(Exception x){
            x.printStackTrace();
        }
    }
    private void  populate_list_view(){
        ArrayAdapter<DropDownItem> adapter = new myListAdapter();
        ListView list = (ListView) getView().findViewById(R.id.lv_profiles);
        registerForContextMenu(list);
        list.setAdapter(adapter);
    }
    private class myListAdapter extends ArrayAdapter<DropDownItem>{
        public myListAdapter() {
            super(context, R.layout.listview_item_category, profileList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if(convertView==null)
            {
                itemView = getLayoutInflater().inflate(R.layout.listview_item_category, parent, false);
            }
            DropDownItem currentItem = profileList.get(position);
            TextView profile_txt = (TextView)itemView.findViewById(R.id.lv_category_txt);
            profile_txt.setText(currentItem.toString());
            return itemView;
        }
    }
    private void hidePopup(){
        layout_transparent_gray_background.setVisibility(View.GONE);
        layout_add_new_profile.setVisibility(View.GONE);
    }
    private void showPopup(){
        et_new_profile_name.setText(action_profile_text);
        if(action_profile_id>0){
            btn_save_profile.setText("Update");
        }else{
            btn_save_profile.setText("Save");
        }
        layout_transparent_gray_background.setVisibility(View.VISIBLE);
        layout_add_new_profile.setVisibility(View.VISIBLE);
    }
    private void hidePopup1(){
        layout_transparent_gray_background.setVisibility(View.GONE);
        layout_delete_profile.setVisibility(View.GONE);
    }
    private void showPopup1(){
        et_new_profile_name.setText(action_profile_text);
        tvdelete_profile.setText(action_profile_text);
        layout_transparent_gray_background.setVisibility(View.VISIBLE);
        layout_delete_profile.setVisibility(View.VISIBLE);
        profileList1 = dbOperations.getProfileListExceptOne(action_profile_id);
        ArrayAdapter<DropDownItem> mySpinnerAdapter =  new ArrayAdapter<DropDownItem> (getContext(),android.R.layout.simple_spinner_dropdown_item, profileList1);
        merge_spinner_profile.setAdapter(mySpinnerAdapter);

    }
}