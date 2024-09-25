package com.websarva.wings.android.tasks;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private EditText titleEditText, descriptionEditText;
    private ListView taskListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> taskList;
    private Spinner spinner, monthspin, dayspin;
    private ArrayAdapter<Integer> yearadp, monthadp, dayadp;
    private int[] daysInMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private Spinner sortSpinner;
    private List<Task> tasks;
    private Calendar calender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        taskListView = findViewById(R.id.taskListView);
        Button addTaskButton = findViewById(R.id.addTaskButton);

        taskList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, taskList);
        taskListView.setAdapter(adapter);
        spinner = findViewById(R.id.yearspin);
        monthspin = findViewById(R.id.monthspin);
        dayspin = findViewById(R.id.dayspin);
        sortSpinner = findViewById(R.id.sortspin);

        setupyearspin();
        setupmonthspin();
        setupdayspin();

        //タスクを加えるボタンを押したとき
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();
                String description = descriptionEditText.getText().toString();
                String ymd = spinner.getSelectedItem().toString() + monthspin.getSelectedItem().toString() + dayspin.getSelectedItem().toString();

                if (title.isEmpty() || description.isEmpty()) {
                    Toast.makeText(MainActivity.this, "タイトル、もしくは説明がありません", Toast.LENGTH_SHORT).show();
                    return;
                }


                //データベースヘルパーにデータを渡す
                dbHelper.addTask(title, description, ymd);
                updateTaskList();
            }
        });

        //ソートの設定
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        sortTasksByAdditionOrder();
                        break;
                    case 1:
                        sortTaskBytitle();
                        break;
                    case 2:
                        break;
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                sortTasksByAdditionOrder();
            }
        });

        //タスク部分を長押ししたとき削除するか
        taskListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id){
                //削除するタスクのIdを取得
                final int TaskId = (int) id;

                //ダイアログで表示
                new AlertDialog.Builder(MainActivity.this).setTitle("タスク削除").setMessage("タスクを削除しますか？").setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dbHelper.deleteTask(TaskId);
                        updateTaskList();
                    }
                }).setNegativeButton(android.R.string.no, null).show();
                return  true;
            }
        });
        updateTaskList();
    }

    private void sortTasksByAdditionOrder(){
        Collections.sort(tasks, new Comparator<Task>() {
            public int compare(Task task1, Task task2){
                return Integer.compare(task1.getId(),task2.getId());
            }
        });
    }

    private void sortTaskBytitle(){
        //ソートあいうえお順
    }

    private void updateTaskList() {
        taskList.clear();
        Cursor cursor = dbHelper.getAllTasks();
        double deadlines;
        calender = Calendar.getInstance();
        String today = String.valueOf(calender.get(Calendar.YEAR) + calender.get(Calendar.MONTH) + calender.get(Calendar.DATE));
        int todays = Integer.parseInt(today);
        String deadline;
        if (cursor.moveToFirst()) {
            do {
                //タスクリストに表示するところ
                deadlines = cursor.getInt(cursor.getColumnIndex("ymd")) - todays;
                if(deadlines > 0){
                    deadline = "締め切りまで" + deadlines + "日";
                }else if(deadlines == 0){
                    deadline = "今日が締め切りです";
                }else {
                    deadline = "締め切りを過ぎています  " + deadlines;
                }
                String task = cursor.getString(cursor.getColumnIndex("title")) + " - " +
                        cursor.getString(cursor.getColumnIndex("description")) + "\n" +
                        cursor.getString(cursor.getColumnIndex("ymd")) +
                        "\n" + deadline;
                taskList.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    //年のスピナーの処理
    private void setupyearspin(){
        Integer[] years = new Integer[10];
        for (int i = 0; i < 10; i++) {
            years[i] = 2020 + i;
        }
        yearadp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        spinner.setAdapter(yearadp);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateDays();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    //月のスピナーの処理
    private void setupmonthspin(){
        Integer[] months = new Integer[12];
        for (int i = 0; i < 12; i++) {
            months[i] = i + 1;
        }
        monthadp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,months);
        monthspin.setAdapter(monthadp);

        monthspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateDays();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    //日のスピナーの処理
    private void setupdayspin(){
        dayadp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        dayspin.setAdapter(dayadp);
        updateDays();
    }

    //年と月のスピナーの内容によって日のスピナーの中身を変更する
    private void updateDays(){
        int year = (int) spinner.getSelectedItem();
        int month = (int) monthspin.getSelectedItem();
        int daysInSelectedMonth = daysInMonth[month - 1];

        //うるう年の2月
        if (month == 2 && isLeapYear(year)) {
            daysInSelectedMonth = 29;
        }
        dayadp.clear();
        for (int i = 1; i <= daysInSelectedMonth; i++) {
            dayadp.add(i);
        }

    }
    private boolean isLeapYear(int year) {
        //うるう年かどうか
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }
}