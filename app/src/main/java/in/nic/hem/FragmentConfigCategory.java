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


public class FragmentConfigCategory extends Fragment {
    LinearLayout layout_add_new_category, layout_transparent_gray_background,layout_delete_category;
    ImageButton btn_add_category,btn_back_newcategory,btn_back_deletecategory;
    Button btn_save_category, btn_category_delete1, btn_category_delete2;
    EditText et_new_category_name;
    Spinner merge_spinner_category;
    List<DropDownItem> categoryList,categoryList1;
    TextView selected_profile_name,tvdelete_category;
    DBOperations dbOperations;
    Context context;
    int action_category_id=0;
    String action_category_text="";


    public FragmentConfigCategory() {
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
        if(v.getId()==R.id.lv_categories)////ListView id.
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
        DropDownItem selectdListItem = categoryList.get(info.position);
        action_category_id = selectdListItem.getItemID();
        action_category_text = selectdListItem.getItemText();
        int menuItemIndex= item.getItemId();
        if(menuItemIndex==0)
        {
            //Edit Selected Item
            showPopup();
        }else if(menuItemIndex==1)
        {
            showPopup1();
        }
        return true;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_config_category, container, false);
        btn_add_category = (ImageButton) view.findViewById(R.id.btn_add_category);
        btn_back_newcategory = (ImageButton) view.findViewById(R.id.btn_back_newcategory);
        btn_back_deletecategory = (ImageButton) view.findViewById(R.id.btn_back_deletecategory);
        layout_add_new_category = (LinearLayout) view.findViewById(R.id.layout_add_new_category);
        layout_transparent_gray_background = (LinearLayout) view.findViewById(R.id.layout_transparent_gray_background);
        layout_delete_category = (LinearLayout) view.findViewById(R.id.layout_delete_category);
        et_new_category_name = (EditText) view.findViewById(R.id.et_new_category_name);
        btn_save_category = (Button) view.findViewById(R.id.btn_save_category);
        btn_category_delete1 = (Button) view.findViewById(R.id.btn_category_delete1);
        btn_category_delete2 = (Button) view.findViewById(R.id.btn_category_delete2);
        tvdelete_category = (TextView) view.findViewById(R.id.tvdelete_category);
        selected_profile_name = (TextView)view.findViewById(R.id.selected_profile_name);
        merge_spinner_category = (Spinner)view.findViewById(R.id.merge_spinner_category);
        btn_add_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action_category_id=-1;
                action_category_text = "";
                showPopup();
            }
        });
        btn_back_newcategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hidePopup();
            }
        });
        btn_back_deletecategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hidePopup1();
            }
        });
        btn_save_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String category_name = et_new_category_name.getText().toString();
                if(category_name!=null && category_name.length()>2){
                    dbOperations = new DBOperations(getContext());
                    boolean result = dbOperations.saveNewExpenditureCategory(category_name,action_category_id);
                    if(result){
                        hidePopup();
                        refreshListView();
                        Toast.makeText(getContext(),"Category saved successfully", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getContext(),"Oops!! Error occured", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getContext(),"Please enter a valid category name", Toast.LENGTH_LONG).show();
                }
            }
        });
        btn_category_delete1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbOperations = new DBOperations(getContext());
                dbOperations.deleteCategory(action_category_id);
                hidePopup1();
                refreshListView();
            }
        });
        btn_category_delete2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DropDownItem dropDownItem = (DropDownItem) merge_spinner_category.getSelectedItem();
                int categoryID = dropDownItem.getItemID();
                if(categoryID<=0)
                {
                    Toast.makeText(getContext(), "Please Select Category",Toast.LENGTH_SHORT).show();
                    return;
                }
                dbOperations.deleteCategoryAndMergeEntries(action_category_id,categoryID);
                hidePopup1();
                refreshListView();
            }
        });

        return view;
    }

    private void refreshListView() {
        dbOperations = new DBOperations(getContext());
        selected_profile_name.setText("Selected Profile: "+dbOperations.getDefaultProfileName());
        try
        {
            categoryList = dbOperations.getCategoryList();
            populate_list_view();
            if(categoryList==null || categoryList.isEmpty()||categoryList.size()==0)
            {
                Toast.makeText(getActivity(), "No Expenditure Category Found. Add new Category First", Toast.LENGTH_SHORT).show();
            }
        }
        catch(Exception x){
            x.printStackTrace();
        }
    }
    private void  populate_list_view(){
        ArrayAdapter<DropDownItem> adapter = new myListAdapter();
        ListView list = (ListView) getView().findViewById(R.id.lv_categories);
        registerForContextMenu(list);
        list.setAdapter(adapter);
    }
    private class myListAdapter extends ArrayAdapter<DropDownItem>{
        public myListAdapter() {
            super(context, R.layout.listview_item_category, categoryList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if(convertView==null)
            {
                itemView = getLayoutInflater().inflate(R.layout.listview_item_category, parent, false);
            }
            DropDownItem currentItem = categoryList.get(position);
            TextView category_txt = (TextView)itemView.findViewById(R.id.lv_category_txt);
            category_txt.setText(currentItem.toString());
            return itemView;
        }
    }
    private void hidePopup(){
        layout_transparent_gray_background.setVisibility(View.GONE);
        layout_add_new_category.setVisibility(View.GONE);
    }
    private void showPopup(){
        et_new_category_name.setText(action_category_text);
        if(action_category_id>0){
            btn_save_category.setText("Update");
        }else{
            btn_save_category.setText("Save");
        }
        layout_transparent_gray_background.setVisibility(View.VISIBLE);
        layout_add_new_category.setVisibility(View.VISIBLE);
    }
    private void hidePopup1(){
        layout_transparent_gray_background.setVisibility(View.GONE);
        layout_delete_category.setVisibility(View.GONE);
    }
    private void showPopup1(){
        et_new_category_name.setText(action_category_text);
        tvdelete_category.setText(action_category_text);
        layout_transparent_gray_background.setVisibility(View.VISIBLE);
        layout_delete_category.setVisibility(View.VISIBLE);
        categoryList1 = dbOperations.getCategoryListExceptOne(action_category_id);
        ArrayAdapter<DropDownItem> mySpinnerAdapter =  new ArrayAdapter<DropDownItem> (getContext(),android.R.layout.simple_spinner_dropdown_item, categoryList1);
        merge_spinner_category.setAdapter(mySpinnerAdapter);

    }
}