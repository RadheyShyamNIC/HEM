package in.nic.hem;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ceylonlabs.imageviewpopup.ImagePopup;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.nic.hem.db.DBOperations;
import in.nic.hem.mypkg.DropDownItem;
import in.nic.hem.mypkg.InterFragmentCommunication;

public class FragmentReport extends Fragment {
    EditText editText_date1,editText_date2, editText_amount1,editText_amount2, editText_description;
    Spinner spinner_category;
    RadioGroup radioGroupAttachmentStatus;
    RadioButton radioAttachmentStatus;
    Button btn_search_records;
    TextView report_heading_text;
    DBOperations dbOperations;
    List<DropDownItem> categoryDropdownList = new ArrayList<DropDownItem>();
    List<String[]> reportExpenditureEntryDetails;
    DatePickerDialog.OnDateSetListener date1,date2;
    Context context;
    LinearLayout layput_ll_collepsed,layput_ll_expended;
    ImageButton btn_layout_collepse,btn_layout_expand;
    int report_amount_sum=0;

    public FragmentReport() {
        // Required empty public constructor
    }

    public static FragmentReport newInstance() {
        FragmentReport fragment = new FragmentReport();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }
    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v.getId()==R.id.lv_report)////ListView id.
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
        String[] itemToUpdate = reportExpenditureEntryDetails.get(info.position);
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
                    }
                }
            });
            builder.setNegativeButton("No", null);
            builder.show();
        }

        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_report, container, false);
        editText_date1 = (EditText) view.findViewById(R.id.editText_date1);
        editText_date2 = (EditText) view.findViewById(R.id.editText_date2);
        editText_amount1 = (EditText) view.findViewById(R.id.editText_amount1);
        editText_amount2 = (EditText) view.findViewById(R.id.editText_amount2);
        editText_description = (EditText) view.findViewById(R.id.editText_description);
        spinner_category=(Spinner) view.findViewById(R.id.spinner_category);
        btn_search_records = (Button) view.findViewById(R.id.btn_search_records);
        report_heading_text = (TextView) view.findViewById(R.id.report_heading_text);
        radioGroupAttachmentStatus = (RadioGroup) view.findViewById(R.id.radioGroup);
        btn_layout_expand = (ImageButton) view.findViewById(R.id.btn_layout_expand);
        btn_layout_collepse = (ImageButton) view.findViewById(R.id.btn_layout_collepse);
        layput_ll_collepsed = (LinearLayout) view.findViewById(R.id.layput_ll_collepsed);
        layput_ll_expended = (LinearLayout) view.findViewById(R.id.layput_ll_expended);
        dbOperations = new DBOperations(getContext());
        categoryDropdownList = dbOperations.getCategoryList("---- All Category ----");
        ArrayAdapter<DropDownItem> mySpinnerAdapter =  new ArrayAdapter<DropDownItem> (getContext(),android.R.layout.simple_spinner_dropdown_item, categoryDropdownList);
        spinner_category.setAdapter(mySpinnerAdapter);
        final Calendar myCalendar = Calendar.getInstance();
        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        editText_date1.setText(currentDate);
        editText_date2.setText(currentDate);
        date1 = new DatePickerDialog.OnDateSetListener() {
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
                String datestr = dd + "/"+ mm + "/" + year;
                editText_date1.setText(datestr);
            }
        };
        editText_date1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(), date1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        editText_date1.setInputType(InputType.TYPE_NULL);
        date2 = new DatePickerDialog.OnDateSetListener() {
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
                String datestr = dd + "/"+ mm + "/" + year;
                editText_date2.setText(datestr);
            }
        };
        editText_date2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(), date2, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        editText_date2.setInputType(InputType.TYPE_NULL);
        btn_search_records.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amount1,amount2, date1,date2, description,attachment_filter;
                int category_id;
                DropDownItem dropDownItem;
                dropDownItem = (DropDownItem) spinner_category.getSelectedItem();
                category_id = dropDownItem.getItemID();
                date1 = editText_date1.getText().toString();
                date2 = editText_date2.getText().toString();
                amount1 = editText_amount1.getText().toString().trim();
                amount2 = editText_amount2.getText().toString().trim();
                description = editText_description.getText().toString();
                int selectedId=radioGroupAttachmentStatus.getCheckedRadioButtonId();
                radioAttachmentStatus = (RadioButton)radioGroupAttachmentStatus.findViewById(selectedId);
                attachment_filter = radioAttachmentStatus.getText().toString();
                getAndDisplayReport(category_id,date1,date2,amount1,amount2,description,attachment_filter);
            }
        });
        btn_layout_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layput_ll_collepsed.setVisibility(View.GONE);
                layput_ll_expended.setVisibility(View.VISIBLE);
            }
        });
        btn_layout_collepse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layput_ll_collepsed.setVisibility(View.VISIBLE);
                layput_ll_expended.setVisibility(View.GONE);
            }
        });
        return view;
    }

    private void getAndDisplayReport(int category_id, String date1, String date2, String amount1, String amount2, String description, String attachment_filter) {
        dbOperations = new DBOperations(getContext());
        try
        {
            reportExpenditureEntryDetails = dbOperations.getReport(category_id,date1,date2,amount1,amount2,description,attachment_filter);
            report_amount_sum=dbOperations.getReportAmountSum(category_id,date1,date2,amount1,amount2,description,attachment_filter);
            populate_list_view();
            if(reportExpenditureEntryDetails==null || reportExpenditureEntryDetails.isEmpty()||reportExpenditureEntryDetails.size()==0)
            {
                report_heading_text.setText("");
                Toast.makeText(getActivity(), "No record found", Toast.LENGTH_SHORT).show();
            }else{
                report_amount_sum = dbOperations.getReportAmountSum(category_id,date1,date2,amount1,amount2,description,attachment_filter);
                report_heading_text.setText("Total Amount in the Report: "+report_amount_sum);
            }
        }
        catch(Exception x){
            x.printStackTrace();
        }
    }
    private void  populate_list_view(){
        ArrayAdapter<String[]> adapter = new MyListAdapter();
        ListView list = (ListView) getView().findViewById(R.id.lv_report);
        registerForContextMenu(list);
        list.setAdapter(adapter);
    }
    private class MyListAdapter extends ArrayAdapter<String[]>{
        public MyListAdapter() {
            super(context, R.layout.listview_item_expenditurerecord, reportExpenditureEntryDetails);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if(convertView==null)
            {
                itemView = getLayoutInflater().inflate(R.layout.listview_item_expenditurerecord, parent, false);
            }
            String[] currentItem = reportExpenditureEntryDetails.get(position);
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