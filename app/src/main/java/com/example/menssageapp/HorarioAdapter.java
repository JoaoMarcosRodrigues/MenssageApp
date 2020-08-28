package com.example.menssageapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class HorarioAdapter extends ArrayAdapter<Horario> {
    private final Context context;
    private final ArrayList<Horario> elementos;

    public HorarioAdapter(Context context, ArrayList<Horario> elementos){
        super(context,R.layout.linha_horarios,elementos);
        this.context = context;
        this.elementos = elementos;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.linha_horarios,parent,false);

        TextView horario = rowView.findViewById(R.id.txtHorario);
        TextView mensagem = rowView.findViewById(R.id.txtMensagem);
        Switch habilitar = rowView.findViewById(R.id.switchHabilitar);

        horario.setText(elementos.get(position).getHorario());
        mensagem.setText(elementos.get(position).getMensagem());
        habilitar.setChecked(true);

        return rowView;
    }
}
