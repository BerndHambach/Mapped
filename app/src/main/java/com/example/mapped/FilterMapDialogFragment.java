package com.example.mapped;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class FilterMapDialogFragment extends DialogFragment {

    RadioGroup rg_Kategorie, rg_Datum;
    RadioButton rb_Alles, rb_Sport, rb_Nachtleben, rb_Verteiler, rb_Heute, rb_Morgen;

    public FilterMapDialogFragment() {
    }
    public static FilterMapDialogFragment newInstance(String title) {

        FilterMapDialogFragment frag = new FilterMapDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

     /*@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String title = getArguments().getString("title");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        //alertDialogBuilder.setTitle(title);

        //alertDialogBuilder.setMessage("Are you sure?");

        alertDialogBuilder.setPositiveButton("OK",  new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // on success
            }
        });

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (dialog != null && ((Dialog) dialog).isShowing()) {
                    dialog.dismiss();
                }
            }
        });
        return alertDialogBuilder.create();
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.filter_layout, container);
        view.findViewById(R.id.rb_Alles).setOnClickListener((View.OnClickListener) view.getContext());
        view.findViewById(R.id.rb_Sport).setOnClickListener((View.OnClickListener) view.getContext());
        return view;
        // return inflater.inflate(R.layout.filter_layout, container);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        // Get field from view

        /*rg_Kategorie = (RadioGroup) view.findViewById(R.id.rg_Kategorie);
        rg_Datum = (RadioGroup) view.findViewById(R.id.rg_Datum);
        rb_Alles = (RadioButton) view.findViewById(R.id.rb_Alles);
        rb_Sport = (RadioButton) view.findViewById(R.id.rb_Sport);
        rb_Nachtleben = (RadioButton) view.findViewById(R.id.rb_Nachtleben);
        rb_Verteiler = (RadioButton) view.findViewById(R.id.rb_Verteiler);
        rb_Heute = (RadioButton) view.findViewById(R.id.rb_Heute);
        rb_Morgen = (RadioButton) view.findViewById(R.id.rb_Morgen);*/

        // Fetch arguments from bundle and set title

        String title = getArguments().getString("title", "Filter");

        getDialog().setTitle(title);

        // Show soft keyboard automatically and request focus to field

        //mEditText.requestFocus();

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }

    /*@Override
    public void onCheckedChanged(CompoundButton v, boolean checked) {
        switch(v.getId()) {
            case R.id.rb_Alles:
                // todo
                break;
            case R.id.:
                // todo
                break;
            // and so on..
        }
        this.getDialog().dismiss();
    }*/
    /*@Override
    public void onClick(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.rb_Alles:
                if (checked) {
                    Log.i("MENU", ((RadioButton) view).getText().toString());
                }
                break;
            case R.id.rb_Sport:
                if (checked) {
                    Log.i("MENU", ((RadioButton) view).getText().toString());
                }
                break;
        }
    }*/
}
