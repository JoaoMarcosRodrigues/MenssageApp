package com.example.menssageapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class HistoricoHorarios extends AppCompatActivity {

    Button btnSalvar;
    Toolbar toolbar;

    ArrayList<Horario> listaHorarios = new ArrayList<Horario>();
    ListView listViewHorarios;
    DBHelper dbHelper;
    ListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico_horarios);

        toolbar = findViewById(R.id.toolbar);

        setActionBar(toolbar);
        getActionBar().setTitle("Histórico");
        getActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent voltar = new Intent(HistoricoHorarios.this,Home.class);
                startActivity(voltar);
            }
        });

        btnSalvar = findViewById(R.id.btnSalvar);
        listViewHorarios = findViewById(R.id.listHorarios);
        dbHelper = new DBHelper(this);


        final Cursor data = dbHelper.getAllHorarios();

        if(data.getCount() == 0){
            Toast.makeText(this,"A lista está vazia :(",Toast.LENGTH_SHORT).show();
        }else{
            while(data.moveToNext()){
                Horario h = new Horario();
                h.setHorario(data.getString(1));
                h.setMensagem(data.getString(2));
                h.setStatus(true);

                listaHorarios.add(h);
                listAdapter = new HorarioAdapter(this,listaHorarios);
                listViewHorarios.setAdapter(listAdapter);
            }
        }

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redefinirStatus("0",false);
            }
        });
    }

    public void redefinirStatus(String id, boolean status){
        if(dbHelper.redefinirStatus(id,status)){
            Toast.makeText(HistoricoHorarios.this,"Alterações salvas!",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(HistoricoHorarios.this,"Houve um erro =(",Toast.LENGTH_SHORT).show();
        }
    }
}