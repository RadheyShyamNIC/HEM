package in.nic.hem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ceylonlabs.imageviewpopup.ImagePopup;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.nic.hem.db.DBOperations;
import in.nic.hem.mypkg.AppPermission;
import in.nic.hem.mypkg.CommonData;
import in.nic.hem.mypkg.DropDownItem;
import in.nic.hem.mypkg.ImageOperation;
import in.nic.hem.mypkg.InterFragmentCommunication;

public class FragmentNewentry extends Fragment {
    EditText editText_amount,editText_date, editText_description;
    Spinner spinner_category;
    ImageButton btn_capture_image,btn_show_image,btn_remove_image;
    Button btn_save_records;
    LinearLayout row_view_attached_image,row_attach_image;
    List<DropDownItem> categoryDropdownList = new ArrayList<DropDownItem>();
    DatePickerDialog.OnDateSetListener date;
    String dateOfTransaction;
    boolean PERMISSIONS;
    private int REQUEST_CAMERA = 0;
    CommonData commonData;
    byte[] imageByteArray = null;
    DBOperations dbOperations;
    List<String[]> recentExpenditureEntryDetails;
    List<DropDownItem> categoryList;
    Context context;

    public FragmentNewentry() {
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
        refreshGUI();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String[] itemToUpdate = recentExpenditureEntryDetails.get(info.position);
        final String id = itemToUpdate[5];
        final String profile_id = itemToUpdate[6];
        int menuItemIndex= item.getItemId();
        if(menuItemIndex==0)
        {
            //Edit Selected Item
            InterFragmentCommunication.is_data_set_for_report_search = 0;
            InterFragmentCommunication.passed_transaction_data_id= id;
            InterFragmentCommunication.passed_transaction_profile_id= profile_id;
            Fragment newFragment = new FragmentUpdateEntry();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }else if(menuItemIndex==1)
        {
            //delete selected item
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Delete !!");
            builder.setMessage("Do you really want to Delete this entriy?");
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dbOperations = new DBOperations(getContext());
                    boolean status = dbOperations.deleteSingleEntry(id, profile_id);
                    if(status){
                        Toast.makeText(getContext(), "Entry deleted successfully",Toast.LENGTH_SHORT).show();
                        refreshGUI();
                    }
                }
            });
            builder.setNegativeButton("No", null);
            builder.show();
        }

        return true;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_newentry, container, false);
        btn_capture_image = (ImageButton) view.findViewById(R.id.btn_capture_image);
        btn_show_image = (ImageButton) view.findViewById(R.id.btn_show_image);
        btn_remove_image = (ImageButton) view.findViewById(R.id.btn_remove_image);
        btn_save_records = (Button) view.findViewById(R.id.btn_save_records);
        spinner_category=(Spinner) view.findViewById(R.id.spinner_category);
        editText_amount=(EditText) view.findViewById(R.id.editText_amount);
        editText_date=(EditText) view.findViewById(R.id.editText_date);
        editText_description=(EditText) view.findViewById(R.id.editText_description);
        row_view_attached_image = (LinearLayout) view.findViewById(R.id.row_view_attached_image);
        row_attach_image = (LinearLayout) view.findViewById(R.id.row_attach_image);

        //registerForContextMenu((ListView)view.findViewById(R.id.lv_categories));

        commonData = new CommonData();
        commonData.setAskPermisionRequired(true);

        final Calendar myCalendar = Calendar.getInstance();
        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        editText_date.setText(currentDate);
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String  mm,dd;
                mm = String.valueOf(monthOfYear+1);
                dd = String.valueOf(dayOfMonth);
                if(mm.length()<2){
                    mm = "0"+mm;
                }
                if(dd.length()<2){
                    dd = "0"+dd;
                }
                dateOfTransaction = dd + "/"+ mm + "/" + year;
                editText_date.setText(dateOfTransaction);
            }
        };

        btn_capture_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clearPermissionRequest()){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                }
            }
        });
        btn_show_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ImagePopup imagePopup = new ImagePopup(getContext());
                ByteArrayInputStream is = new ByteArrayInputStream(imageByteArray);
                imagePopup.initiatePopup(Drawable.createFromStream(is, "Captured Image"));
                imagePopup.viewPopup();
            }
        });
        btn_remove_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageByteArray= null;
                refreshGUI();
            }
        });
        btn_save_records.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amount, date, description;
                int category_id;
                DropDownItem dropDownItem;
                date = editText_date.getText().toString();
                amount = editText_amount.getText().toString().trim();
                description = editText_description.getText().toString();
                dropDownItem = (DropDownItem) spinner_category.getSelectedItem();
                category_id = dropDownItem.getItemID();
                if(category_id<=0)
                {
                    Toast.makeText(getContext(), "Please Select Category"+description,Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(amount.length()==0)
                {
                    Toast.makeText(getContext(), "Please enter amount"+description,Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    boolean status=false;
                    dbOperations = new DBOperations(getContext());
                    status=dbOperations.saveNewExpenditureRecord(category_id, amount, date,description, imageByteArray);
                    if(status)
                    {
                        Toast.makeText(getContext(), "Added successfully"+description,Toast.LENGTH_SHORT).show();
                        refreshGUI();
                        return;
                    }
                    else {
                        Toast.makeText(getContext(), "Error occured..not saved"+description,Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

            }
        });
        editText_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        editText_date.setInputType(InputType.TYPE_NULL);
        return view;
    }
    private void refreshGUI(){
        dbOperations = new DBOperations(getContext());
        categoryDropdownList = dbOperations.getCategoryList("---- Select Category ----");
        ArrayAdapter<DropDownItem> mySpinnerAdapter =  new ArrayAdapter<DropDownItem> (getContext(),android.R.layout.simple_spinner_dropdown_item, categoryDropdownList);
        spinner_category.setAdapter(mySpinnerAdapter);
        if(imageByteArray!=null){
            row_view_attached_image.setVisibility(View.VISIBLE);
            row_attach_image.setVisibility(View.GONE);
        }else{
            row_view_attached_image.setVisibility(View.GONE);
            row_attach_image.setVisibility(View.VISIBLE);
        }
        editText_description.setText("");
        refreshListView();
    }

    private boolean clearPermissionRequest()
    {
        AppPermission appPermission = new AppPermission(getContext(),getActivity());
        boolean flag = commonData.isAskPermisionRequired();
        boolean permissions = appPermission.checkPermissions(flag);
        if (!permissions) {
            permissions = appPermission.checkPermissions(false);
            Toast.makeText(getContext(), "Camera Access Permission Denied.... Give Permision to use this feature", Toast.LENGTH_LONG).show();
        }
        return permissions;
    }
    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        Bitmap resizedImage = thumbnail;
        //Bitmap resizedImage = new ImageOperation().CompressResizeImage(thumbnail);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        resizedImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        imageByteArray = bytes.toByteArray();
        Toast.makeText(getContext(), "Image Captured and compressed Successfully", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }
    private void refreshListView() {
        dbOperations = new DBOperations(getContext());
        try
        {
            recentExpenditureEntryDetails = dbOperations.getRecentlyAddedExpenditureDeta();
            populate_list_view();
            if(recentExpenditureEntryDetails==null || recentExpenditureEntryDetails.isEmpty()||recentExpenditureEntryDetails.size()==0)
            {
                Toast.makeText(getActivity(), "No recently addred record found", Toast.LENGTH_SHORT).show();
            }
        }
        catch(Exception x){
            x.printStackTrace();
        }
    }
    private void  populate_list_view(){
        ArrayAdapter<String[]> adapter = new MyListAdapter();
        ListView list = (ListView) getView().findViewById(R.id.lv_categories);
        registerForContextMenu(list);
        list.setAdapter(adapter);
    }
    private class MyListAdapter extends ArrayAdapter<String[]>{
        public MyListAdapter() {
            super(context, R.layout.listview_item_expenditurerecord, recentExpenditureEntryDetails);
        }

        @Override
        public View getView(int position, View convertView, final ViewGroup parent) {
            View itemView = convertView;
            if(convertView==null)
            {
                itemView = getLayoutInflater().inflate(R.layout.listview_item_expenditurerecord, parent, false);
            }
            String[] currentItem = recentExpenditureEntryDetails.get(position);
            final String date, category, description, img_attachemnt;
            String amount;

            date = currentItem[0];
            amount = currentItem[1];
            category = currentItem[2];
            description = currentItem[3];
            img_attachemnt = currentItem[4];
            TextView tv_expenditure_date = (TextView)itemView.findViewById(R.id.tv_expenditure_date);
            TextView tv_expenditure_amount = (TextView)itemView.findViewById(R.id.tv_expenditure_amount);
            TextView tv_expenditure_category = (TextView)itemView.findViewById(R.id.tv_expenditure_category);
            TextView tv_expenditure_description = (TextView)itemView.findViewById(R.id.tv_expenditure_description);
            ImageButton btn_expenditure_attachment = (ImageButton) itemView.findViewById(R.id.btn_expenditure_attachment);
            tv_expenditure_date.setText(date);
            tv_expenditure_amount.setText(amount);
            tv_expenditure_category.setText(category);
            tv_expenditure_description.setText(description);
            if(img_attachemnt.equals("-1")){
                btn_expenditure_attachment.setVisibility(View.INVISIBLE);
            }else{
                btn_expenditure_attachment.setVisibility(View.VISIBLE);
                btn_expenditure_attachment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showImage(img_attachemnt);
                    }
                });
            }
            return itemView;
        }
        private void showImage(String img_id){
            ////////////////////////////////////
            dbOperations = new DBOperations(getContext());
            byte[] imageByteArray1 = dbOperations.getImageBytes(img_id);
            if(imageByteArray1!=null){
                final ImagePopup imagePopup = new ImagePopup(getContext());
                ByteArrayInputStream is = new ByteArrayInputStream(imageByteArray1);
                imagePopup.initiatePopup(Drawable.createFromStream(is, "Captured Image"));
                imagePopup.viewPopup();
            }
            else{
                Toast.makeText(getActivity(), "Image not found", Toast.LENGTH_SHORT).show();
            }
        }
    }
}