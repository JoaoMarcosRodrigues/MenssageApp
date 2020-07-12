package com.example.menssageapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.ArrayList;

public class Dialog extends AppCompatDialogFragment {
    Home home = new Home();
    ArrayList<Contato> lista = home.listaContatos;
    ListView listView = home.listView;
    ArrayAdapter<Contato> adapter = home.adapter;

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Excluir contato")
                .setIcon(android.R.drawable.ic_menu_delete)
                .setMessage("Tem certeza que deseja excluir este contato?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        lista.remove(listView.getSelectedItemPosition());
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(),"Contato excluido.",Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(),"Operação cancelada.",Toast.LENGTH_SHORT).show();
                    }
                });

        return builder.create();
    }
}
