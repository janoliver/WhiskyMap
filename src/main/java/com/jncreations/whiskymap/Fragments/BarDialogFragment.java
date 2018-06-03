package com.jncreations.whiskymap.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.RatingBar;
import com.jncreations.whiskymap.Helpers.DataBaseHelper;
import com.jncreations.whiskymap.Models.Whisky;
import com.jncreations.whiskymap.R;

public class BarDialogFragment extends DialogFragment {
    private boolean mIsCustom = false;
    protected DataBaseHelper mDatabase;

    public static BarDialogFragment newInstance(boolean is_custom) {
        BarDialogFragment f = new BarDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean("from_bar", is_custom);
        f.setArguments(args);
        return f;
    }

    public static BarDialogFragment newInstance(boolean is_custom, int id) {
        BarDialogFragment f = new BarDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean("from_bar", is_custom);
        args.putInt("id", id);
        f.setArguments(args);
        return f;
    }

    public Whisky getWhisky() {
        Whisky whisky;
        if(!getArguments().getBoolean("from_bar"))
            whisky = ((WhiskyFragment)getTargetFragment()).getWhisky();
        else if(getArguments().getInt("id",0) > 0) {
            whisky = mDatabase.getWhiskyById(getArguments().getInt("id",0));
        } else {
            whisky = new Whisky();
            whisky.setIsCustom(true);
        }
        return whisky;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDatabase = new DataBaseHelper(getActivity());


        final Whisky whisky = getWhisky();

        final DataBaseHelper database = new DataBaseHelper(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialog_content = inflater.inflate(R.layout.dialog_bar, null);

        final EditText item_name = (EditText) dialog_content.findViewById(R.id.item_name);
        final CheckedTextView item_owns = (CheckedTextView) dialog_content.findViewById(R.id.item_owns);
        final CheckedTextView item_owned = (CheckedTextView) dialog_content.findViewById(R.id.item_owned);
        final CheckedTextView item_tasted = (CheckedTextView) dialog_content.findViewById(R.id.item_tasted);
        final CheckedTextView item_wants = (CheckedTextView) dialog_content.findViewById(R.id.item_wants);
        final RatingBar item_rating = (RatingBar) dialog_content.findViewById(R.id.rating);
        final EditText item_notes = (EditText) dialog_content.findViewById(R.id.item_notes);

        if(whisky.isCustom()) {
            item_name.setVisibility(View.VISIBLE);
            item_name.setText(whisky.getName());
        }

        item_rating.setRating(whisky.getRating());
        if(whisky.hasBarNotes())
            item_notes.setText(whisky.getBarNotes());

        item_owns.setChecked(whisky.getBarOwns());
        item_owns.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                ((CheckedTextView) v).toggle();
                if (item_owns.isChecked()) {
                    item_owned.setChecked(false);
                    item_wants.setChecked(false);
                } else {
                    item_owned.setChecked(true);
                }
            }
        });

        item_owned.setChecked(whisky.getBarOwned());
        item_owned.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                ((CheckedTextView) v).toggle();
                if (item_owned.isChecked())
                    item_owns.setChecked(false);
            }
        });

        item_tasted.setChecked(whisky.getBarTasted());
        item_tasted.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                ((CheckedTextView) v).toggle();
            }
        });

        item_wants.setChecked(whisky.getBarWants());
        item_wants.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                ((CheckedTextView) v).toggle();
            }
        });

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton(R.string.button_save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(whisky.isCustom())
                            whisky.setName(item_name.getText().toString());
                        database.addToBar(whisky,
                                item_owns.isChecked(),
                                item_owned.isChecked(),
                                item_tasted.isChecked(),
                                item_wants.isChecked(),
                                item_rating.getRating(),
                                item_notes.getText().toString());
                        whisky.setBarOwns(item_owns.isChecked());
                        whisky.setBarOwned(item_owned.isChecked());
                        whisky.setBarTasted(item_tasted.isChecked());
                        whisky.setBarWants(item_wants.isChecked());
                        whisky.setRating(item_rating.getRating());
                        whisky.setBarNotes(item_notes.getText().toString());
                        if(!whisky.isCustom())
                            ((WhiskyFragment) getTargetFragment()).setWhisky(whisky);
                        else
                            ((BarFragment) getTargetFragment()).refresh();
                    }
                })
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {}
                })
                .setView(dialog_content);

        // Create the AlertDialog object and return it
        return builder.create();
    }

}