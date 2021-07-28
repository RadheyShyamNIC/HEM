package in.nic.hem;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.List;

import in.nic.hem.db.DBOperations;


public class FragmentHome extends Fragment {
    List<String[]> coparedDataReport;
    ListView mylistview;
    DBOperations dbOperations;
    Context context;
    LinearLayout ll_dashboard_comparision_heading;
    TextView tv_curr_month_total,tv_prev_month_total;
    ToggleButton toggleButton;
    String dashboard_view_type;
    TextView tv_dashboard_amount_prev, tv_dashboard_amount_curr;

    public FragmentHome() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home, container, false);
        mylistview = (ListView)view.findViewById(R.id.mylistview);
        ll_dashboard_comparision_heading = (LinearLayout)view.findViewById(R.id.ll_dashboard_comparision_heading) ;
        tv_curr_month_total = (TextView)view.findViewById(R.id.tv_curr_month_total) ;
        tv_prev_month_total = (TextView)view.findViewById(R.id.tv_prev_month_total) ;
        toggleButton = (ToggleButton) view.findViewById(R.id.toggleButton) ;
        tv_dashboard_amount_prev = (TextView)view.findViewById(R.id.tv_dashboard_amount_prev) ;
        tv_dashboard_amount_curr = (TextView)view.findViewById(R.id.tv_dashboard_amount_curr) ;
        context = getContext();
        toggleButton.setChecked(false);
        dashboard_view_type="monthwise";
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(toggleButton.isChecked()){
                    //////Year wise report
                    dashboard_view_type="yearwise";
                    setDashboardReport_Yearwise();
                }else{
                    //////Month wise report
                    dashboard_view_type="monthwise";
                    setDashboardReport_Monthwise();
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(dashboard_view_type.equals("monthwise")){
            setDashboardReport_Monthwise();
        }else{
            setDashboardReport_Yearwise();
        }
    }
    private void setDashboardReport_Monthwise(){
        dbOperations = new DBOperations(context);
        coparedDataReport = dbOperations.getComparedData();
        String[] coparedDataReportSum = dbOperations.getComparedDataSum();
        ArrayAdapter<String[]> adapter = new MyListAdapter();
        ListView list = (ListView) getView().findViewById(R.id.mylistview);
        list.setAdapter(adapter);
        tv_dashboard_amount_prev.setText("Previous Month");
        tv_dashboard_amount_curr.setText("Current Month");
        tv_curr_month_total.setText("Current Month Amount: Rs. "+coparedDataReportSum[1]+" /-");
        tv_prev_month_total.setText("Previous Month Amount: Rs. "+coparedDataReportSum[0]+" /-");
        if(coparedDataReport.isEmpty() || coparedDataReport.size()<1){
            ll_dashboard_comparision_heading.setVisibility(View.INVISIBLE);
        }
        else{
            ll_dashboard_comparision_heading.setVisibility(View.VISIBLE);
        }
    }
    private void setDashboardReport_Yearwise(){
        dbOperations = new DBOperations(context);
        coparedDataReport = dbOperations.getComparedDataYearwise();
        String[] coparedDataReportSum = dbOperations.getComparedDataSumYearwise();
        ArrayAdapter<String[]> adapter = new MyListAdapter();
        ListView list = (ListView) getView().findViewById(R.id.mylistview);
        list.setAdapter(adapter);
        tv_dashboard_amount_prev.setText("Previous Year");
        tv_dashboard_amount_curr.setText("Current Year");
        tv_curr_month_total.setText("Current Year Amount: Rs. "+coparedDataReportSum[1]+" /-");
        tv_prev_month_total.setText("Previous Year Amount: Rs. "+coparedDataReportSum[0]+" /-");
        if(coparedDataReport.isEmpty() || coparedDataReport.size()<1){
            ll_dashboard_comparision_heading.setVisibility(View.INVISIBLE);
        }
        else{
            ll_dashboard_comparision_heading.setVisibility(View.VISIBLE);
        }
    }
    class MyListAdapter extends ArrayAdapter<String[]>{

        public MyListAdapter() {
            super(context, R.layout.listview_item_comparision, coparedDataReport);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if(convertView==null)
            {
                itemView = getLayoutInflater().inflate(R.layout.listview_item_comparision, parent, false);
            }
            final String[] currentItem = coparedDataReport.get(position);

            ((TextView)itemView.findViewById(R.id.tv_dashboard_category)).setText(currentItem[1]);
            ((TextView)itemView.findViewById(R.id.tv_dashboard_amount_prev)).setText(currentItem[2]);
            ((TextView)itemView.findViewById(R.id.tv_dashboard_amount_curr)).setText(currentItem[3]);
            return itemView;
        }
    }
}