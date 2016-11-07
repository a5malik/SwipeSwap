package com.example.tsleeve.swipeswap;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by footb on 10/22/2016.
 */

public class AddSwipeDialogFragment extends DialogFragment implements View.OnClickListener {
    private Calendar calendar;
    private EditText editTextSwipePrice;
    Button btnSwipeDate, btnSwipeTime;
    TextView tvSwipeDate, tvSwipeTime;
    ImageView closeButton;
    private SwipeDataAuth mDb = new SwipeDataAuth();
    private UserAuth mUAuth = new UserAuth();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog =  super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        }
    }

    @Override
    public void onClick(View v) {
        boolean checked = ((CheckBox) v).isChecked();

        // Check which checkbox was clicked
        switch (v.getId()) {
            case R.id.checkBoxBPlate:
                if (checked)
                    diningHall |= SwipeDataAuth.BPLATE_ID;
                else
                    diningHall &= (~SwipeDataAuth.BPLATE_ID);
                break;
            case R.id.checkBoxCovel:
                if (checked)
                    diningHall |= SwipeDataAuth.COVEL_ID;
                else
                    diningHall &= (~SwipeDataAuth.COVEL_ID);
                break;
            case R.id.checkBoxDeNeve:
                if (checked)
                    diningHall |= SwipeDataAuth.DENEVE_ID;
                else
                    diningHall &= (~SwipeDataAuth.DENEVE_ID);
                break;
            case R.id.checkBoxFeast:
                if (checked)
                    diningHall |= SwipeDataAuth.FEAST_ID;
                else
                    diningHall &= (~SwipeDataAuth.FEAST_ID);
                break;
        }
    }

    private CheckBox DeNeve, Covel, BPlate, Feast;
    private Integer diningHall = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_swipe_dialog, container, false);
        calendar = Calendar.getInstance();

        TextView textViewHeader = (TextView) view.findViewById(R.id.textViewAddSwipeHeader);
        textViewHeader.setText("Swipe Transaction");
        btnSwipeDate = (Button) view.findViewById(R.id.buttonSwipeDate);
        tvSwipeDate = (TextView) view.findViewById(R.id.textViewSwipeDate);
        btnSwipeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        tvSwipeDate.setText(Integer.toString(dayOfMonth) + "/" + Integer.toString(month) + "/" + Integer.toString(year));

                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        btnSwipeTime = (Button) view.findViewById(R.id.buttonSwipeTime);
        tvSwipeTime = (TextView) view.findViewById(R.id.textViewSwipeTime);
        btnSwipeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        tvSwipeTime.setText(Integer.toString(hourOfDay) + ":" + Integer.toString(minute));
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                timePickerDialog.show();
            }
        });

        editTextSwipePrice = (EditText) view.findViewById(R.id.editTextswipeprice);
        editTextSwipePrice.setHint("Add price ");

        Covel = (CheckBox) view.findViewById(R.id.checkBoxCovel);
        Feast = (CheckBox) view.findViewById(R.id.checkBoxFeast);
        BPlate = (CheckBox) view.findViewById(R.id.checkBoxBPlate);
        DeNeve = (CheckBox) view.findViewById(R.id.checkBoxDeNeve);

        setupCheckboxes();
        //final FirebaseAuth auth = FirebaseAuth.getInstance();

        Button btnSell = (Button) view.findViewById(R.id.buttonaddswipe);
        btnSell.setText("Sell");
        btnSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: ADD VERIFICATION FOR DATA ENTERED
                mDb.addSwipe(new Swipe(Double.parseDouble(editTextSwipePrice.getText().toString()), calendar.getTimeInMillis(),
                        //calendar.getTimeInMillis(), auth.getCurrentUser().getUid(), diningHall));
                        calendar.getTimeInMillis(), mUAuth.uid(), diningHall), mUAuth.uid());
                mUAuth.sendNotification(getActivity()); // TODO: test - remove later
                dismiss();
            }
        });

        Button btnBuy = (Button) view.findViewById(R.id.buttonaddrequest);
        btnBuy.setText("Buy");
        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDb.addRequest(new Swipe(Double.parseDouble(editTextSwipePrice.getText().toString()), calendar.getTimeInMillis(),
                        calendar.getTimeInMillis(), mUAuth.uid(), diningHall), mUAuth.uid());
                dismiss();
            }
        });
        closeButton = (ImageView) view.findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    private void setupCheckboxes() {
        Covel.setOnClickListener(this);
        DeNeve.setOnClickListener(this);
        BPlate.setOnClickListener(this);
        Feast.setOnClickListener(this);
    }

    public void show(FragmentManager fragmentManager, String add_swipe_fragment) {
    }
}
