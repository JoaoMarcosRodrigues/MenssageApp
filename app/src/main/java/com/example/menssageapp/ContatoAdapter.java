package com.example.menssageapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ContatoAdapter extends ArrayAdapter<Contato> {
    private final Context context;
    private final ArrayList<Contato> elementos;

    public ContatoAdapter(Context context, ArrayList<Contato> elementos){
        super(context,R.layout.linha,elementos);
        this.context = context;
        this.elementos = elementos;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.linha,parent,false);

        TextView nome = rowView.findViewById(R.id.nome);
        TextView telefone = rowView.findViewById(R.id.telefone);
        ImageView imagem = rowView.findViewById(R.id.imagem);

        nome.setText(elementos.get(position).getNome());
        telefone.setText(elementos.get(position).getTelefone());
        imagem.setImageResource(elementos.get(position).getImagem());

        return rowView;
    }
}
