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
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class HistoricoHorarios extends AppCompatActivity {

    Button btnSalvar;
    Toolbar toolbar;

    ArrayAdapter<Horario> adapter;
    ListView listViewHorarios;
    ArrayList<Horario> arrayListHorario = new ArrayList<Horario>();
    Horario horario = new Horario();
    DBHelper dbHelper;

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

        ArrayList<Horario> listaHorarios = new ArrayList<Horario>();
        Cursor data = dbHelper.getAllHorarios();

        if(data.getCount() == 0){
            Toast.makeText(this,"A lista está vazia :(",Toast.LENGTH_SHORT).show();
        }else{
            while(data.moveToNext()){
                Horario h = new Horario();
                h.setHorario(data.getString(1));
                h.setMensagem(data.getString(2));

                listaHorarios.add(h);
                ListAdapter listAdapter = new HorarioAdapter(this,listaHorarios);
                listViewHorarios.setAdapter(listAdapter);
            }
        }
        /*
        try{
            List<String> horarios = new ArrayList<>();
            horarios.add("7:00");
            horarios.add("8:00");
            horarios.add("9:00");
            horarios.add("10:00");
            horarios.add("11:00");
            List<String> mensagens = new ArrayList<>();
            mensagens.add("Teste 1");
            mensagens.add("Teste 2");
            mensagens.add("Teste 3");
            mensagens.add("Teste 4");
            mensagens.add("Teste 5");

            for(int i=0; i<horarios.size(); i++) {
                //Contato contato = new Contato();
                Horario horario = new Horario();
                horario.setHorario(horarios.get(i));
                horario.setMensagem(mensagens.get(i));
                arrayListHorario.add(horario);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

         */

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HistoricoHorarios.this,"Alterações salvas!",Toast.LENGTH_SHORT).show();
            }
        });
    }
}