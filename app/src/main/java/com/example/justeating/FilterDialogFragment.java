package com.example.justeating;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

public class FilterDialogFragment extends DialogFragment {

    public interface FilterDialogListener {
        public void onFilterOKClick(FilterDialogFragment dialog);
    }

    FilterDialogListener listener;
    View dialogView;
    Spinner businessTypeSpinner;
    Spinner regionSpinner;
    Spinner authoritySpinner;
    Spinner ratingValSpinner;
    Spinner ratingOpSpinner;

    private ArrayList<BusinessType> businessTypes;
    private ArrayAdapter<BusinessType> businessTypeAdpt;

    private ArrayList<String> regions;
    private ArrayAdapter<String> regionsAdapter;

    private ArrayList<Authority> authorities;
    private ArrayList<Authority> filteredAuthorities = new ArrayList<>();
    private ArrayAdapter<Authority> authorityAdapter;

    private ArrayList<String> ratingOps;
    private ArrayAdapter<String> ratingOpsAdapter;

    private ArrayList<String> ratingValues;
    private ArrayAdapter<String> ratingValuesAdapter;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        System.out.println("ON ATTACH");

        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (FilterDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(this.toString()
                    + " must implement NoticeDialogListener");
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        System.out.println("ONCREATEDIALOG");
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View dialogView = requireActivity().getLayoutInflater().inflate(R.layout.dialog_filter, null);

        businessTypeSpinner = dialogView.findViewById(R.id.businessTypeSpinner);
        businessTypeAdpt = new ArrayAdapter(this.getActivity(), android.R.layout.simple_spinner_item, businessTypes);
        businessTypeAdpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        businessTypeSpinner.setAdapter(businessTypeAdpt);

        regionSpinner = dialogView.findViewById(R.id.regionSpinner);
        regionsAdapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_spinner_item, regions);
        regionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        regionSpinner.setAdapter(regionsAdapter);
        regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(regionSpinner.getSelectedItem().equals("None")){
                    authoritySpinner.setEnabled(false);
                } else {
                    authoritySpinner.setEnabled(true);
                    filteredAuthorities.clear();
                    for(Authority auth : authorities){
                        if(auth.getRegion().equals(regionSpinner.getSelectedItem())){
                            filteredAuthorities.add(auth);
                        }
                    }
                    authorityAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //i wonder how this works
            }

        });

        authoritySpinner = dialogView.findViewById(R.id.authoritySpinner);
        authorityAdapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_spinner_item, filteredAuthorities);
        authorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        authoritySpinner.setAdapter(authorityAdapter);
        authoritySpinner.setEnabled(false);

        ratingValues = new ArrayList<String>() {{
            add("0");
            add("1");
            add("2");
            add("3");
            add("4");
            add("5");
        }};
        ratingValSpinner = dialogView.findViewById(R.id.ratingValSpinner);
        ratingValuesAdapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_spinner_item, ratingValues);
        ratingValuesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ratingValSpinner.setAdapter(ratingValuesAdapter);
        ratingValSpinner.setEnabled(false);

        ratingOps = new ArrayList<String>() {{
            add("any");
            add("exactly");
            add("maximum");
            add("minimum");
        }};
        ratingOpSpinner = dialogView.findViewById(R.id.ratingOpSpinner);
        ratingOpsAdapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_spinner_item, ratingOps);
        ratingOpsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ratingOpSpinner.setAdapter(ratingOpsAdapter);
        ratingOpSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(ratingOpSpinner.getSelectedItem().equals("any")){
                    ratingValSpinner.setEnabled(false);
                } else {
                    ratingValSpinner.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //i wonder how this works
            }

        });


        builder.setView(dialogView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onFilterOKClick(FilterDialogFragment.this);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it

        return builder.create();
    }

    public void setFilterLists(ArrayList<BusinessType> businessTypes, ArrayList<Authority> authorities, ArrayList<String> regions){
        this.businessTypes = businessTypes;
        this.authorities = authorities;
        this.regions = regions;
    }

    public Integer getSelectedEstab(){
        if(((BusinessType) businessTypeSpinner.getSelectedItem()) != null){
            return ((BusinessType) businessTypeSpinner.getSelectedItem()).getId();
        } else {
            return -1;
        }
    }

    public Integer getSelectedRegion(){
        return -1;
    }

    public Integer getSelectedAuthority(){
        if(authoritySpinner.getSelectedItem() != null){
            return ((Authority) authoritySpinner.getSelectedItem()).getId();
        } else {
            return -1;
        }
    }

    public String getRatingsQuery(){
        String op = (String) ratingOpSpinner.getSelectedItem();
        String val = (String) ratingValSpinner.getSelectedItem();
        String query = "&ratingOperatorKey=";
        switch(op){
            case "any":
                return "";
            case "exactly":
                query = query.concat("6");
                break;
            case "maximum":
                query = query.concat("9");
                break;
            case "minimum":
                query = query.concat("8");
                break;
        }
        return query.concat("&ratingKey=").concat(val);
    }

}
