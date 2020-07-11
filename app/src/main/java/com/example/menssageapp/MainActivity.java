package com.example.menssageapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.View;
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

public class MainActivity extends AppCompatActivity {

    ImageButton imageBtnSelecionarContato;
    ImageButton btnEnviarSMS;
    ImageButton btnEnviarWhats;
    ListView listView;
    MultiAutoCompleteTextView editMensagem;
    ImageView imageMenu;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    NavController navController;

    // POSSÍVEL MÁSCARA
    MaskTextWatcher mtw;
    SimpleMaskFormatter smf;

    ArrayList<Contato> listaContatos = new ArrayList<Contato>();

    ArrayAdapter<Contato> adapter;

    final int SEND_SMS_PERMISSION_REQUEST_CODE = 1;
    static final int PICK_CONTATO_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // MENU
        drawerLayout = findViewById(R.id.drawerLayout);
        imageMenu = findViewById(R.id.imageMenu);
        navigationView = findViewById(R.id.navigationView);

        imageMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setItemIconTintList(null);

        /*
        navController = Navigation.findNavController(this, R.id.navHostFragment);
        NavigationUI.setupWithNavController(navigationView,navController);

         */
        // FIM MENU

        btnEnviarSMS = findViewById(R.id.btnEnviarSMS);
        btnEnviarWhats = findViewById(R.id.btnEnviarWhats);
        listView = findViewById(R.id.listView);
        editMensagem = findViewById(R.id.mensagem);
        imageBtnSelecionarContato = findViewById(R.id.imageAdicionarContato);

        // Checando permissão para enviar SMS
        if(!checarPermissaoSMS(Manifest.permission.SEND_SMS)){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},SEND_SMS_PERMISSION_REQUEST_CODE);
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
                AlertDialog.Builder d = new AlertDialog.Builder(MainActivity.this);
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
                                Toast.makeText(MainActivity.this,"Operação cancelada.",Toast.LENGTH_SHORT).show();
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
                AlertDialog.Builder d = new AlertDialog.Builder(MainActivity.this);
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
                                Toast.makeText(MainActivity.this,"Operação cancelada.",Toast.LENGTH_SHORT).show();
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
        adapter = new ContatoAdapter(this,listaContatos);
        listView.setAdapter(adapter);

    }



    // ------------------------- FUNÇÕES -------------------------
    public void enviarSMS(){

        if(editMensagem.getText().toString().equals("") || listaContatos.isEmpty()){
            Toast.makeText(this,"Adicione um contato ou digite uma mensagem. Tente novamente.",Toast.LENGTH_SHORT).show();
        }else if(checarPermissaoSMS(Manifest.permission.SEND_SMS)){
            for(int i=0; i<listaContatos.size(); i++){
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(listaContatos.get(i).getTelefone(), null, editMensagem.getText().toString(), null, null);
            }
            Toast.makeText(this, "SMS enviado!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Permissão negada!", Toast.LENGTH_SHORT).show();
        }
    }

    public void enviarWhats(){
        // Verificar se o aplicativo do whatsapp está instalado
        boolean instalado = appInstalado("com.whatsapp");

        if(listaContatos.isEmpty() || editMensagem.getText().toString().equals("")){
            Toast.makeText(this,"Adicione um contato à lista ou digite uma mensagem!",Toast.LENGTH_SHORT).show();
        }
        else if(instalado){
            for(int i=0; i<listaContatos.size(); i++){
                String numero = listaContatos.get(i).getTelefone();
                String mensagem = editMensagem.getText().toString();

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+numero+"&text="+mensagem));
                startActivity(intent);
            }

        }else{
            Toast.makeText(this, "Whatsapp não instalado no seu dispositivo.", Toast.LENGTH_SHORT).show();
        }
    }

    // Verificar se o aplicativo do whatsapp está instalado
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

    public boolean checarPermissaoSMS(String permissao){
        int check = ContextCompat.checkSelfPermission(this,permissao);
        return (check == PackageManager.PERMISSION_GRANTED);
    }

    // PEGANDO UM CONTATO DA LISTA DE TELEFONE DO CELULAR
    private void selecionarContato() {
        Intent selecionarContatoItem = new Intent(Intent.ACTION_PICK, Uri.parse("content://contatos"));
        selecionarContatoItem.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(selecionarContatoItem,PICK_CONTATO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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

                    if(!listaContatos.isEmpty()) {
                        for (int i = 0; i < listaContatos.size(); i++) {
                            if (listaContatos.get(i).getTelefone().equals(numero)) {
                                Toast.makeText(this, "Contato já adicionado!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }

                    Contato contato = new Contato("Pedro","(79) 88888-8888",R.drawable.ic_pessoa);
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
}