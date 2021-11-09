package com.example.conversormonedainternet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.example.conversormonedainternet.MainActivity;

public class MostrarDivisas extends AppCompatActivity {
    ListView todaslvCM;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_divisas);

        todaslvCM=(ListView) findViewById(R.id.ListViewTodas);
        ArrayAdapter<String>adaptador=new ArrayAdapter<>(this, android.R.layout.simple_list_item_2, android.R.id.text1,MainActivity.iniciales_ratioCM);
        todaslvCM.setAdapter(adaptador);
    }
}