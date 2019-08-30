package com.picoplaca.fredr.co.activitys;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.picoplaca.fredr.co.adapters.AdapterBitacora;
import com.picoplaca.fredr.co.R;
import com.picoplaca.fredr.co.models.Plate;

import java.util.ArrayList;

public class BitacoraActivity extends AppCompatActivity {

    private AdapterBitacora adapter;
    private DatabaseReference platesBd1;
    private ArrayList<Plate> listPlates;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bitacora_activity);

        ArrayList<Plate> mValues = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listPlates = new ArrayList<>();
        adapter = new AdapterBitacora(listPlates,this);
        recyclerView.setAdapter(adapter);
        getDataFromFirebase();
    }

    private void getDataFromFirebase(){
        platesBd1 = FirebaseDatabase.getInstance().getReference();

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
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
