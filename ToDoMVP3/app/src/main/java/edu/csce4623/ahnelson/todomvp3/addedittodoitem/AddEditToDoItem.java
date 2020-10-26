package edu.csce4623.ahnelson.todomvp3.addedittodoitem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import edu.csce4623.ahnelson.todomvp3.AlarmReceiver;
import edu.csce4623.ahnelson.todomvp3.R;
import edu.csce4623.ahnelson.todomvp3.data.ToDoItem;

public class AddEditToDoItem extends AppCompatActivity {

    ToDoItem item;
    EditText etTitle;
    EditText etContent;
    Button btnSaveItem;
    Button btnDeleteItem;
    //Button btnDatePicker;
    Button checkFinished;
    EditText date_time_in;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_to_do_item);

        etTitle = findViewById(R.id.etItemTitle);
        etContent = findViewById(R.id.etItemContent);
        btnSaveItem = findViewById(R.id.btnSaveToDoItem);
        btnDeleteItem = findViewById(R.id.btnDelete);
       // checkFinished = findViewById(R.id.checkBox2);
        date_time_in = findViewById(R.id.date_time_input);

        //date_time_in.setInputType(InputType.TYPE_NULL);

        Intent callingIntent = getIntent();
        if (callingIntent != null) {
            if (callingIntent.hasExtra("ToDoItem")) {
                item = (ToDoItem) callingIntent.getSerializableExtra("ToDoItem");
            } else {
                item = new ToDoItem();
                item.setTitle("Title");
                item.setContent("Content");
            }
        }

        date_time_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimeDialog(date_time_in);
            }
        });


        
        btnSaveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItems();
            }
        });
       /* btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
            }
        });*/
       /* checkFinished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckboxClicked(v);
                item.getCompleted();
            }
        });*/
    }


    @Override
    protected void onStart() {
        super.onStart();
        etTitle.setText(item.getTitle());
        etContent.setText(item.getContent());
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(item.getDueDate());
       date_time_in.setText(String.valueOf(getDate(item.getDueDate(),"MM-dd-yyyy HH:mm")));

    }

    public static String getDate(long milliSeconds, String dateFormat)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(milliSeconds);
        return formatter.format(c.getTime());
    }

    @Override
    protected void onStop() {
        super.onStop();
        item.setTitle(etTitle.getText().toString());
        item.setContent(etContent.getText().toString());
      //  item.setDueDate();
    }

    private void saveItems(){
        item.setTitle((etTitle.getText().toString()));
        item.setContent(etContent.getText().toString());

        Intent dataIntent = new Intent();
        dataIntent.putExtra("ToDoItem",item);
        setResult(RESULT_OK,dataIntent);
        Calendar c = Calendar.getInstance();
        //c.add(Calendar.SECOND,0);
        c.setTimeInMillis(item.getDueDate());
        c.set(Calendar.SECOND,0);
        AlarmManager almManager;
        if(Build.VERSION.SDK_INT >= 23) {
            almManager = this.getSystemService(AlarmManager.class);
        }
        else {
            almManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        }

       // c.setTimeInMillis(item.getDueDate());
        //c.add(Calendar.SECOND,0);
        Intent alarmNotificationIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this,0,alarmNotificationIntent,0);
        almManager.setExact(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),alarmIntent);
        finish();
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new DialogFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    private void deleteItems(){

    }

        /*
        Button toDoButton = findViewById(R.id.btnNewToDo);
        toDoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

public void openAddEditToDoItem() {
   Intent intent = new Intent(this,AddEditToDoItem.class);
   startActivity(intent);
}
*/

    private void showDateTimeDialog(final EditText date_time_in) {
        final Calendar calendar=Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeSetListener=new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        calendar.set(Calendar.MINUTE,minute);
                        item.setDueDate(calendar.getTimeInMillis());

                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MM-dd-yy HH:mm");

                        date_time_in.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                };

                new TimePickerDialog(AddEditToDoItem.this,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
            }
        };

        new DatePickerDialog(AddEditToDoItem.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();

    }
        public void onCheckboxClicked(View view) {
            // Is the view now checked?
            boolean checked = ((CheckBox) view).isChecked();

            // Check which checkbox was clicked
            switch(view.getId()) {
                case R.id.checkBoxFinished:
                    if (checked) {
                        item.setCompleted(true);
                    }
            else
                    // Set to false
                    item.setCompleted(false);
                    break;
                // TODO: Veggie sandwich
            }
        }

}