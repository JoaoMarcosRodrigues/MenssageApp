package com.example.menssageapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    ImageButton imageBtnSelecionarContato;
    ImageButton btnEnviarSMS;
    ImageButton btnEnviarWhats;
    ListView listView;
    MultiAutoCompleteTextView editMensagem;
    ImageView menu;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    NavController navController;

    // POSSÍVEL MÁSCARA
    MaskTextWatcher mtw;
    SimpleMaskFormatter smf;

    ArrayList<Contato> listaContatos = new ArrayList<Contato>();

    ArrayAdapter<Contato> adapter;

    final int SEND_SMS_PERMISSION_REQUEST_CODE = 1;
    static final int PICK_CONTATO_REQUEST = 1;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        btnEnviarSMS = v.findViewById(R.id.btnEnviarSMS);
        btnEnviarWhats = v.findViewById(R.id.btnEnviarWhats);
        listView = v.findViewById(R.id.listView);
        editMensagem = v.findViewById(R.id.mensagem);
        imageBtnSelecionarContato = v.findViewById(R.id.imageAdicionarContato);

        // Checando permissão para enviar SMS
        if(!checarPermissaoSMS(Manifest.permission.SEND_SMS)){
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.SEND_SMS},SEND_SMS_PERMISSION_REQUEST_CODE);
        }

        // Adicionando máscara no campo Telefone
        //smf = new SimpleMaskFormatter("(NN) NNNNN-NNNN");
        //mtw = new MaskTextWatcher(editTelefone,smf);
        //editTelefone.addTextChangedListener(mtw);
        // FIM MÁSCARA

        // Função para enviar o SMS
        btnEnviarSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarSMS();
            }
        });

        // Função para enviar o SMS
        btnEnviarWhats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarWhats();
            }
        });

        // Função para excluir um contato na lista ao pressionar
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int posicao = position;
                AlertDialog.Builder d = new AlertDialog.Builder(getActivity());
                d.setTitle("Excluir contato.")
                        .setIcon(android.R.drawable.ic_menu_delete)
                        .setMessage("Tem certeza que deseja excluir este contato?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listaContatos.remove(posicao);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getActivity(),"Operação cancelada.",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();

                return true;
            }
        });

        // Função para excluir um contato na lista ao clicar
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int posicao = position;
                AlertDialog.Builder d = new AlertDialog.Builder(getActivity());
                d.setTitle("Excluir contato.")
                        .setIcon(android.R.drawable.ic_menu_delete)
                        .setMessage("Tem certeza que deseja excluir este contato?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listaContatos.remove(posicao);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getActivity(),"Operação cancelada.",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
            }
        });

        imageBtnSelecionarContato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selecionarContato();
            }
        });

        // Tratando o List View
        adapter = new ContatoAdapter(getActivity(),listaContatos);
        listView.setAdapter(adapter);

        return v;
    }

// ------------------------- FUNÇÕES -------------------------

    public void enviarSMS(){

        ArrayList<String> listaTelefones = new ArrayList<>();

        if(editMensagem.getText().toString().equals("") || listaContatos.isEmpty()){
            Toast.makeText(getActivity(),"Adicione um contato ou digite uma mensagem. Tente novamente.",Toast.LENGTH_SHORT).show();
        }else if(checarPermissaoSMS(Manifest.permission.SEND_SMS)){
            for(int i=0; i<listaContatos.size(); i++){
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(listaContatos.get(i).getTelefone(), null, editMensagem.getText().toString(), null, null);
                //listaTelefones.add(listaContatos.get(i).getTelefone()+" ");
            }
            Toast.makeText(getActivity(), "SMS enviado!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getActivity(), "Permissão negada!", Toast.LENGTH_SHORT).show();
        }
    }

    // PROLEMA COM O appInstalado
    public void enviarWhats(){
        // Verificar se o aplicativo do whatsapp está instalado
        //boolean instalado = appInstalado("com.whatsapp");

        if(listaContatos.isEmpty() || editMensagem.getText().toString().equals("")){
            Toast.makeText(getActivity(),"Adicione um contato à lista ou digite uma mensagem!",Toast.LENGTH_SHORT).show();
        }
        else if(true){
            for(int i=0; i<listaContatos.size(); i++){
                String numero = listaContatos.get(i).getTelefone();
                String mensagem = editMensagem.getText().toString();

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+numero+"&text="+mensagem));
                startActivity(intent);
            }

        }else{
            Toast.makeText(getActivity(), "Whatsapp não instalado no seu dispositivo.", Toast.LENGTH_SHORT).show();
        }
    }

    // Verificar se o aplicativo do whatsapp está instalado
    /*
    public boolean appInstalado(String url){
        PackageManager packageManager = getPackageManager();
        boolean app_instalado;
        try {
            packageManager.getPackageInfo(url,PackageManager.GET_ACTIVITIES);
            app_instalado = true;
        }catch(PackageManager.NameNotFoundException e){
            app_instalado = false;
        }
        return app_instalado;
    }

     */

    public boolean checarPermissaoSMS(String permissao){
        int check = ContextCompat.checkSelfPermission(getActivity(),permissao);
        return (check == PackageManager.PERMISSION_GRANTED);
    }

    // PEGANDO UM CONTATO DA LISTA DE TELEFONE DO CELULAR
    private void selecionarContato() {
        Intent selecionarContatoItem = new Intent(Intent.ACTION_PICK, Uri.parse("content://contatos"));
        selecionarContatoItem.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(selecionarContatoItem,PICK_CONTATO_REQUEST);
    }

    // PROBLEMA COM O getContentResolver()
    /*
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CONTATO_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);

                if (cursor.moveToFirst()) {
                    int colunaNome = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    int colunaNumero = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                    String nome = cursor.getString(colunaNome);
                    String numero = cursor.getString(colunaNumero);

                    if (!listaContatos.isEmpty()) {
                        for (int i = 0; i < listaContatos.size(); i++) {
                            if (listaContatos.get(i).getTelefone().equals(numero)) {
                                Toast.makeText(getActivity(), "Contato já adicionado!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }

                    Contato contato = new Contato("Pedro", "(79) 88888-8888", R.drawable.ic_pessoa);
                    //Contato contato = new Contato();
                    contato.setNome(nome);
                    contato.setTelefone(numero);
                    contato.setImagem(R.drawable.ic_pessoa);
                    listaContatos.add(contato);

                    adapter.notifyDataSetChanged();

                }
            }
        }
    }
    */
}