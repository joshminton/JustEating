package com.example.justeating;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
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
    Spinner authoritySpinner;

    private ArrayList<BusinessType> businessTypes;
    private ArrayAdapter<BusinessType> businessTypeAdpt;

    private ArrayList<Authority> authorities;
    private ArrayAdapter<Authority> authorityAdapter;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
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
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View dialogView = requireActivity().getLayoutInflater().inflate(R.layout.dialog_filter, null);

        businessTypeSpinner = dialogView.findViewById(R.id.businessTypeSpinner);
        businessTypeAdpt = new ArrayAdapter(this.getActivity(), android.R.layout.simple_spinner_item, businessTypes);
        businessTypeAdpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        businessTypeSpinner.setAdapter(businessTypeAdpt);

        authoritySpinner = dialogView.findViewById(R.id.authoritySpinner);
        authorityAdapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_spinner_item, businessTypes);
        authorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        authoritySpinner.setAdapter(authorityAdapter);

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

    public void setFilterLists(ArrayList<BusinessType> businessTypes, ArrayList<Authority> authorities){
        this.businessTypes = businessTypes;
        this.authorities = authorities;
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
        return -1;
    }
}
