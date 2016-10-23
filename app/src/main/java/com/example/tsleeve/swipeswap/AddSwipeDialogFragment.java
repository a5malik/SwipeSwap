package com.example.tsleeve.swipeswap;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

/**
 * Created by footb on 10/22/2016.
 */

public class AddSwipeDialogFragment extends DialogFragment {
    private Calendar calendar;
    private EditText editTextSwipePrice, editTextSwipeDate, editTextSwipeTime;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_swipe_dialog, container, false);
        editTextSwipeDate = (EditText) view.findViewById(R.id.editTextswipedate);
        calendar = Calendar.getInstance();
        editTextSwipeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
        editTextSwipeDate.setHint("Double Click for date");
        editTextSwipeTime = (EditText) view.findViewById(R.id.editTextswipetime);
        editTextSwipeTime.setHint("Double Click for time");
        //editTextSwipeTime.setText("FUCK THIS SHIT");
        editTextSwipeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                timePickerDialog.show();
            }
        });
        Button btn = (Button) view.findViewById(R.id.buttonaddswipe);
        editTextSwipePrice = (EditText) view.findViewById(R.id.editTextswipeprice);
        editTextSwipePrice.setHint("Name your price");
        final FirebaseAuth auth = FirebaseAuth.getInstance();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("swipes").
                        push().setValue(new Swipe(Double.parseDouble(editTextSwipePrice.getText().toString()), calendar.getTimeInMillis(), calendar.getTimeInMillis(), auth.getCurrentUser().getUid(), 2));
                dismiss();
            }
        });
        btn.setText("Add");
        Button btn2 = (Button) view.findViewById(R.id.buttoncancel);
        editTextSwipePrice = (EditText) view.findViewById(R.id.editTextswipeprice);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btn2.setText("Cancel");
        return view;
    }
}
