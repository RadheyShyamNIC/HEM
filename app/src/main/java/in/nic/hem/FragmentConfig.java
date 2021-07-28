package in.nic.hem;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

public class FragmentConfig extends Fragment {
    TabLayout tab_layout_config;
    Fragment fragment;
    FragmentTransaction fragmentTransaction;

    public FragmentConfig() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =   inflater.inflate(R.layout.fragment_config, container, false);
        fragment = new FragmentConfigCategory();
        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentcontainer_config,fragment);
        fragmentTransaction.commit();
        /////////////////////////////////////////////////////
        tab_layout_config = view.findViewById(R.id.tab_layout_config);
        tab_layout_config.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabPosition = tab.getPosition();
                if(tabPosition==0){
                    fragment = new FragmentConfigCategory();
                    fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentcontainer_config,fragment);
                    fragmentTransaction.commit();
                }else if(tabPosition==1){
                    fragment = new FragmentConfigProfile();
                    fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentcontainer_config,fragment);
                    fragmentTransaction.commit();
                }else if(tabPosition==2){
                    fragment = new FragmentIO();
                    fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentcontainer_config,fragment);
                    fragmentTransaction.commit();
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        return view;
    }
}