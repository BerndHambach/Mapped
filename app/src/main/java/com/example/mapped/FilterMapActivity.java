package com.example.mapped;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.Calendar;

public class FilterMapActivity extends AppCompatActivity {
    RadioGroup rg_Kategorie, rg_Datum;
    String beispiel = "Beispiel";
    RadioButton rb_Alles, rb_Sport, rb_Nachtleben, rb_Verteiler, rb_Heute, rb_Morgen;

    String filterByCategorie, filterByDate;
    Button btn_FilterOk;
    Calendar calendar;
    public String today;
    public String tomorrow;

    public String b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_map_activity);

        rg_Kategorie = (RadioGroup) findViewById(R.id.rg_Kategorie);
        rg_Datum = (RadioGroup) findViewById(R.id.rg_Datum);
        rb_Alles = (RadioButton) findViewById(R.id.rb_Alles);
        rb_Sport = (RadioButton) findViewById(R.id.rb_Sport);
        rb_Nachtleben = (RadioButton) findViewById(R.id.rb_Nachtleben);
        rb_Verteiler = (RadioButton) findViewById(R.id.rb_Verteiler);
        rb_Heute = (RadioButton) findViewById(R.id.rb_Heute);
        rb_Morgen = (RadioButton) findViewById(R.id.rb_Morgen);
        btn_FilterOk = (Button) findViewById(R.id.btn_FilterOk);


        calendar = Calendar.getInstance();

        // calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        today = (dayOfMonth + "/" + (month + 1) + "/" + year);

        calendar.add(Calendar.DAY_OF_YEAR, 1);

        int year2 = calendar.get(Calendar.YEAR);
        int month2 = calendar.get(Calendar.MONTH);
        int dayOfMonth2 = calendar.get(Calendar.DAY_OF_MONTH);

        tomorrow = (dayOfMonth2 + "/" + (month2 + 1) + "/" + year2);

        filterByDate = today;
        filterByCategorie = "Alles in meiner Umgebung";

        rg_Kategorie.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = (RadioButton) findViewById(i);
                filterByCategorie = radioButton.getText().toString();
            }
        });

        rg_Datum.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = (RadioButton) findViewById(i);
                String resultdate = radioButton.getText().toString();
                if(resultdate.equals("Heute")) {
                    filterByDate = today;
                }
                if(resultdate.equals("Morgen")) {
                    filterByDate = tomorrow;
                }

            }
        });

        btn_FilterOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("CategorieFilter", filterByCategorie);
                intent.putExtra("DateFilter", filterByDate);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}