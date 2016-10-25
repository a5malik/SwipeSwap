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
import android.widget.CheckBox;
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

public class AddSwipeDialogFragment extends DialogFragment implements View.OnClickListener {
    private Calendar calendar;
    private EditText editTextSwipePrice, editTextSwipeDate, editTextSwipeTime;
    private SwipeDataAuth db = new SwipeDataAuth();
    private UserAuth uAuth = new UserAuth();

    @Override
    public void onClick(View v) {
        boolean checked = ((CheckBox) v).isChecked();

        // Check which checkbox was clicked
        switch (v.getId()) {
            case R.id.checkBoxBPlate:
                if (checked)
                    diningHall |= 1;
                else
                    diningHall &= (~1);
                break;
            case R.id.checkBoxCovel:
                if (checked)
                    diningHall |= 2;
                else
                    diningHall &= (~2);
                break;
            case R.id.checkBoxDeNeve:
                if (checked)
                    diningHall |= 4;
                else
                    diningHall &= (~4);
                break;
            case R.id.checkBoxFeast:
                if (checked)
                    diningHall |= 8;
                else
                    diningHall &= (~8);
                break;
        }
    }

    private CheckBox DeNeve, Covel, BPlate, Feast;
    private Integer diningHall = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_swipe_dialog, container, false);
        editTextSwipeDate = (EditText) view.findViewById(R.id.editTextswipedate);
        calendar = Calendar.getInstance();

        TextView textViewHeader = (TextView) view.findViewById(R.id.textViewAddSwipeHeader);
        textViewHeader.setText("ADD A SWIPE");
        editTextSwipeDate = (EditText) view.findViewById(R.id.editTextswipedate);
        editTextSwipeDate.setHint("Click for date");

        editTextSwipeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        editTextSwipeDate.setText(Integer.toString(dayOfMonth) + "/" + Integer.toString(month) + "/" + Integer.toString(year));

                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        editTextSwipeTime = (EditText) view.findViewById(R.id.editTextswipetime);
        editTextSwipeTime.setHint("Click for time");
        editTextSwipeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        editTextSwipeTime.setText(Integer.toString(hourOfDay) + ":" + Integer.toString(minute));
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                timePickerDialog.show();
            }
        });

        editTextSwipePrice = (EditText) view.findViewById(R.id.editTextswipeprice);
        editTextSwipePrice.setHint("Name your price");

        Covel = (CheckBox) view.findViewById(R.id.checkBoxCovel);
        Feast = (CheckBox) view.findViewById(R.id.checkBoxFeast);
        BPlate = (CheckBox) view.findViewById(R.id.checkBoxBPlate);
        DeNeve = (CheckBox) view.findViewById(R.id.checkBoxDeNeve);

        setupCheckboxes();
        final FirebaseAuth auth = FirebaseAuth.getInstance();

        Button btnSubmit = (Button) view.findViewById(R.id.buttonaddswipe);
        btnSubmit.setText("Add");
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.addSwipe(new Swipe(Double.parseDouble(editTextSwipePrice.getText().toString()), calendar.getTimeInMillis(),
                        calendar.getTimeInMillis(), auth.getCurrentUser().getUid(), diningHall));
                dismiss();
            }
        });

        Button btnCancel = (Button) view.findViewById(R.id.buttoncancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnCancel.setText("Cancel");

        return view;
    }

    private void setupCheckboxes() {
        Covel.setOnClickListener(this);
        DeNeve.setOnClickListener(this);
        BPlate.setOnClickListener(this);
        Feast.setOnClickListener(this);

        Covel.setText("Covel");
        DeNeve.setText("DeNeve");
        BPlate.setText("BPlate");
        Feast.setText("Feast");
    }
}
