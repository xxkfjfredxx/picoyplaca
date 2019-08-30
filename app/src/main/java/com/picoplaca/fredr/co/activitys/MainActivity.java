package com.picoplaca.fredr.co.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.picoplaca.fredr.co.R;
import com.picoplaca.fredr.co.models.Plate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String CERO = "0";
    private static final String DOS_PUNTOS = ":";
    private static final String BARRA = "/";
    public final Calendar c = Calendar.getInstance();
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);
    final int hora = c.get(Calendar.HOUR_OF_DAY);
    final int minuto = c.get(Calendar.MINUTE);
    private DatabaseReference platesBd;
    private DatabaseReference platesBd1;
    private ImageButton getDateImg, getTimeImg;
    private TextView setDate, setTime,txtPlate;
    private TextInputLayout edtPlate;
    private Button btnCheck,bitacora;
    private CheckBox safe;
    private String horaFormateada;
    private String minutoFormateado;
    private String dayName;
    private ArrayList<Plate> listPlates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCheck = (Button) findViewById(R.id.checkdata);
        bitacora = (Button) findViewById(R.id.btnbitacora);

        getDateImg = (ImageButton) findViewById(R.id.get_date);
        getTimeImg = (ImageButton) findViewById(R.id.get_time);

        safe = (CheckBox) findViewById(R.id.checkBox);

        setDate = (TextView) findViewById(R.id.set_date);
        setTime = (TextView) findViewById(R.id.set_time);
        txtPlate = (TextView) findViewById(R.id.txtplate);

        edtPlate = (TextInputLayout) findViewById(R.id.edtplate);

        getDateImg.setOnClickListener(this);
        getTimeImg.setOnClickListener(this);
        btnCheck.setOnClickListener(this);
        bitacora.setOnClickListener(this);

        platesBd = FirebaseDatabase.getInstance().getReference();

        getDataFromFirebase();

    }

    private void getDate() {
        DatePickerDialog recogerFecha = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                final int mesActual = month + 1;
                String diaFormateado = (dayOfMonth < 10) ? CERO + String.valueOf(dayOfMonth) : String.valueOf(dayOfMonth);
                String mesFormateado = (mesActual < 10) ? CERO + String.valueOf(mesActual) : String.valueOf(mesActual);
                String form = dayOfMonth + BARRA + mesActual + BARRA + year;
                SimpleDateFormat inFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date date = null;
                try {
                    date = inFormat.parse(form);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
                dayName = outFormat.format(date);
                setDate.setText(diaFormateado + BARRA + mesFormateado + BARRA + year);
            }
        }, anio, mes, dia);
        recogerFecha.show();
    }

    private void getTime() {
        TimePickerDialog recogerHora = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                horaFormateada = (hourOfDay < 10) ? String.valueOf(CERO + hourOfDay) : String.valueOf(hourOfDay);
                minutoFormateado = (minute < 10) ? String.valueOf(CERO + minute) : String.valueOf(minute);
                String AM_PM;
                if (hourOfDay < 12) {
                    AM_PM = "a.m.";
                } else {
                    AM_PM = "p.m.";
                }
                setTime.setText(horaFormateada + DOS_PUNTOS + minutoFormateado + " " + AM_PM);
            }
            //Estos valores deben ir en ese orden
            //Al colocar en false se muestra en formato 12 horas y true en formato 24 horas
            //Pero el sistema devuelve la hora en formato 24 horas
        }, hora, minuto, false);
        recogerHora.show();
    }

    private void sendData() {
        if (TextUtils.isEmpty(edtPlate.getEditText().getText().toString().trim())) {
            Toast.makeText(this, "Ingrese la placa", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(setDate.getText().toString())) {
            Toast.makeText(this, "Ingrese la fecha", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(setTime.getText().toString())) {
            Toast.makeText(this, "Ingrese la hora", Toast.LENGTH_LONG).show();
            return;
        }
        boolean safeIsChek = false;
        String id = platesBd.push().getKey();
        String numberPlate = edtPlate.getEditText().getText().toString().trim();
        boolean contrevencion = false;
        if (safe.isChecked()) {
            safeIsChek = true;
        }

        String last = numberPlate.substring(numberPlate.length() - 1);

        if (dayName.equals("lunes") && last.equals("1") || last.equals("2")) {
            contrevencion = checkHours(horaFormateada,minutoFormateado);
        } else if (dayName.equals("martes") && last.equals("3")|| last.equals("4")) {
            contrevencion = checkHours(horaFormateada,minutoFormateado);
        } else if (dayName.equals("miercoles") && last.equals("5") || last.equals("6")) {
            contrevencion = checkHours(horaFormateada,minutoFormateado);
        } else if (dayName.equals("jueves") && last.equals("7") || last.equals("8")) {
            contrevencion = checkHours(horaFormateada,minutoFormateado);
        } else if (dayName.equals("viernes") && last.equals("9") || last.equals("0")) {
            contrevencion = checkHours(horaFormateada,minutoFormateado);
        }

        if(contrevencion){
            txtPlate.setText("La placa SI tiene contravencion");
        }else{
            txtPlate.setText("La placa NO tiene contravencion");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());

        Plate plate = new Plate(id, numberPlate, safeIsChek, contrevencion,currentDateandTime);
        platesBd.child("placas").child(id).setValue(plate);
    }

    private boolean checkHours(String hourTime,String minTime){
        boolean contrevencion = false;
        int hora = Integer.parseInt(hourTime);
        int min = Integer.parseInt(minTime);
        if (hora >= 7 && hora <= 10) {
            if(hora == 9 && min > 30){
                contrevencion = false;
            }else{
                contrevencion = true;
            }
        }

        if (hora >= 16 && hora <= 20) {
            if(hora == 19 && min > 30){
                contrevencion = false;
            }else{
                contrevencion = true;
            }
        }

        return contrevencion;
    }

    private void getDataFromFirebase(){
        platesBd1 = FirebaseDatabase.getInstance().getReference();
        listPlates = new ArrayList<>();
        platesBd1.child("placas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        String id = ds.child("id").getValue().toString();
                        String plate = ds.child("plate").getValue().toString();
                        String safe = ds.child("safePassage").getValue().toString();
                        String contrav = ds.child("contravention").getValue().toString();
                        String datereg = ds.child("dateRegister").getValue().toString();
                        listPlates.add(new Plate(id,plate,Boolean.parseBoolean(safe),Boolean.parseBoolean(contrav),datereg));
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.get_date:
                getDate();
                break;
            case R.id.get_time:
                getTime();
                break;
            case R.id.checkdata:
                sendData();
                break;
            case R.id.btnbitacora:
                Intent i = new Intent(MainActivity.this,BitacoraActivity.class);
                startActivity(i);
                break;
        }
    }
}
