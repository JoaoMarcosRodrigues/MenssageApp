package com.example.menssageapp.horario;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.example.menssageapp.R;

import java.util.ArrayList;

public class HorarioAdapter extends ArrayAdapter<Horario> {
    private final Context context;
    private final ArrayList<Horario> elementos;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    SwitchCompat habilitar;

    public HorarioAdapter(Context context, ArrayList<Horario> elementos){
        super(context, R.layout.linha_horarios,elementos);
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
        habilitar = rowView.findViewById(R.id.switchHabilitar);

        horario.setText(elementos.get(position).getHorario());
        mensagem.setText(elementos.get(position).getMensagem());

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        // sharedPreferences = getSharedPreference("mydatabase",Context.MODE_PRIVATE);
        habilitar.setChecked(sharedPreferences.getBoolean("value",true));

        // O estado de um é igual aos demais
        habilitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(habilitar.isChecked()){
                    editor = sharedPreferences.edit();
                    editor.putBoolean("value",true);
                    editor.apply();
                    habilitar.setChecked(true);
                }else{
                    editor = sharedPreferences.edit();
                    editor.putBoolean("value",false);
                    editor.apply();
                    habilitar.setChecked(false);
                }
            }
        });

        return rowView;
    }
}
