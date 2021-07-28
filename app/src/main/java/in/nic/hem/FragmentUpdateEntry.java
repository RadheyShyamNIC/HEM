package in.nic.hem;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.ceylonlabs.imageviewpopup.ImagePopup;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import in.nic.hem.db.DBOperations;
import in.nic.hem.mypkg.AppPermission;
import in.nic.hem.mypkg.CommonData;
import in.nic.hem.mypkg.DropDownItem;
import in.nic.hem.mypkg.InterFragmentCommunication;

public class FragmentUpdateEntry extends Fragment {
    EditText editText_amount,editText_date, editText_description;
    Spinner spinner_category;
    ImageButton btn_capture_image,btn_show_image,btn_remove_image;
    Button btn_cancel_updaterecords,btn_updaterecords;
    boolean PERMISSIONS;
    private int REQUEST_CAMERA = 0;
    CommonData commonData;
    byte[] imageByteArray = null;
    DBOperations dbOperations;
    List<DropDownItem> categoryDropdownList = new ArrayList<DropDownItem>();
    DatePickerDialog.OnDateSetListener date;
    String dateOfTransaction;
    LinearLayout row_view_attached_image,row_attach_image;
    Context context;
    int captured_fresh_image=0,saved_image_removed=0;
    String attached_image_id="-1";

    public FragmentUpdateEntry() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshGUI();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =   inflater.inflate(R.layout.fragment_update_entry, container, false);
        btn_capture_image = (ImageButton) view.findViewById(R.id.btn_capture_image);
        btn_show_image = (ImageButton) view.findViewById(R.id.btn_show_image);
        btn_remove_image = (ImageButton) view.findViewById(R.id.btn_remove_image);
        btn_cancel_updaterecords = (Button) view.findViewById(R.id.btn_cancel_updaterecords);
        btn_updaterecords = (Button) view.findViewById(R.id.btn_updaterecords);
        spinner_category=(Spinner) view.findViewById(R.id.spinner_category);
        editText_amount=(EditText) view.findViewById(R.id.editText_amount);
        editText_date=(EditText) view.findViewById(R.id.editText_date);
        editText_description=(EditText) view.findViewById(R.id.editText_description);
        row_view_attached_image = (LinearLayout) view.findViewById(R.id.row_view_attached_image);
        row_attach_image = (LinearLayout) view.findViewById(R.id.row_attach_image);

        commonData = new CommonData();
        commonData.setAskPermisionRequired(true);
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
                if(captured_fresh_image==1 && imageByteArray!=null){
                    final ImagePopup imagePopup = new ImagePopup(getContext());
                    ByteArrayInputStream is = new ByteArrayInputStream(imageByteArray);
                    imagePopup.initiatePopup(Drawable.createFromStream(is, "Captured Image"));
                    imagePopup.viewPopup();
                }else{
                    dbOperations = new DBOperations(getContext());
                    byte[] imageByteArray1 = dbOperations.getImageBytes(attached_image_id);
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
        });
        btn_remove_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageByteArray= null;
                captured_fresh_image=0;
                saved_image_removed = 1;
                refreshGUI();
            }
        });
        btn_cancel_updaterecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        btn_updaterecords.setOnClickListener(new View.OnClickListener() {
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
                boolean status=false;
                dbOperations = new DBOperations(getContext());
                status=dbOperations.updateExpenditureRecord(InterFragmentCommunication.passed_transaction_profile_id,InterFragmentCommunication.passed_transaction_data_id,category_id, amount, date,description, imageByteArray,captured_fresh_image,saved_image_removed);
                if(status){
                    Toast.makeText(getContext(), "Record updated successfully.", Toast.LENGTH_LONG).show();
                    getActivity().onBackPressed();
                }
            }
        });
        return view;
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
        captured_fresh_image = 1;
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
    private void refreshGUI(){
        dbOperations = new DBOperations(getContext());
        String transaction_item_id = InterFragmentCommunication.passed_transaction_data_id;
        String[] expenditureEntryDetails = dbOperations.getOneExpenditureData(transaction_item_id);
        String date,amount,category,description,img_attachemnt;
        date = expenditureEntryDetails[0];
        amount = expenditureEntryDetails[1];
        category = expenditureEntryDetails[2];
        description = expenditureEntryDetails[3];
        img_attachemnt = expenditureEntryDetails[4];
        attached_image_id = img_attachemnt;
        categoryDropdownList = dbOperations.getCategoryList();
        ArrayAdapter<DropDownItem> mySpinnerAdapter =  new ArrayAdapter<DropDownItem> (getContext(),android.R.layout.simple_spinner_dropdown_item, categoryDropdownList);
        spinner_category.setAdapter(mySpinnerAdapter);
        int selectedItemPosition = getId2PositionInDropdownList(Integer.parseInt(category));
        spinner_category.setSelection(selectedItemPosition);
        editText_date.setText(date);
        editText_amount.setText(amount);
        editText_description.setText(description);
        if((!img_attachemnt.equals("-1") && saved_image_removed==0) || (captured_fresh_image==1 && imageByteArray!=null)){
            row_view_attached_image.setVisibility(View.VISIBLE);
            row_attach_image.setVisibility(View.GONE);
        }else{
            row_attach_image.setVisibility(View.VISIBLE);
            row_view_attached_image.setVisibility(View.GONE);
        }
    }
    private int getId2PositionInDropdownList(int id){
        int position =0;
        int len = categoryDropdownList.size();
        for(int i=0;i<len;i++){
            int item_id = (categoryDropdownList.get(i)).getItemID();
            if(item_id==id){
                position = i;
                break;
            }
        }
        return position;
    }
}