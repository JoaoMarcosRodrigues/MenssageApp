package com.example.menssageapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.LimitColumn;
import com.wafflecopter.multicontactpicker.MultiContactPicker;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {

    private static final int CONTACT_PICKER_REQUEST = 202;
    ImageButton imageBtnSelecionarContato;
    ImageButton btnEnviarSMS;
    ImageButton btnEnviarWhats;
    ImageButton btnEnviarFacebook;
    ListView listView;
    MultiAutoCompleteTextView editMensagem;

    // POSSÍVEL MÁSCARA
    MaskTextWatcher mtw;
    SimpleMaskFormatter smf;

    ArrayList<Contato> listaContatos = new ArrayList<Contato>();
    List<ContactResult> results = new ArrayList<>();

    ArrayAdapter<Contato> adapter;

    final int SEND_SMS_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnEnviarSMS = findViewById(R.id.btnEnviarSMS);
        btnEnviarWhats = findViewById(R.id.btnEnviarWhats);
        btnEnviarFacebook = findViewById(R.id.btnEnviarFacebook);
        listView = findViewById(R.id.listView);
        editMensagem = findViewById(R.id.mensagem);
        imageBtnSelecionarContato = findViewById(R.id.imageAdicionarContato);


        // Checando permissão para enviar SMS
        if(!checarPermissaoSMS(Manifest.permission.SEND_SMS)){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},SEND_SMS_PERMISSION_REQUEST_CODE);
        }

        // Checando permissão de acessibilidade
        if(!isAccessibilityOn(getApplicationContext())){
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        // Adicionando máscara no campo Telefone
        //smf = new SimpleMaskFormatter("(NN) NNNNN-NNNN");
        //mtw = new MaskTextWatcher(editTelefone,smf);
        //editTelefone.addTextChangedListener(mtw);
        // FIM MÁSCARA

        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.READ_CONTACTS
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();

        // Função para enviar o SMS
        btnEnviarSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarSMS();
            }
        });

        // Função para enviar o Whatsapp
        btnEnviarWhats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarWhats();
            }
        });

        // Função para enviar mensagem pelo Facebook
        btnEnviarFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarFacebook();
            }
        });

        // Função para excluir um contato na lista ao pressionar
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int posicao = position;
                AlertDialog.Builder d = new AlertDialog.Builder(Home.this);
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
                                Toast.makeText(Home.this,"Operação cancelada.",Toast.LENGTH_SHORT).show();
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
                AlertDialog.Builder d = new AlertDialog.Builder(Home.this);
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
                                Toast.makeText(Home.this,"Operação cancelada.",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
            }
        });

        imageBtnSelecionarContato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //selecionarContato();
                new MultiContactPicker.Builder(Home.this) //Activity/fragment context
                        .hideScrollbar(false) //Optional - default: false
                        .showTrack(true) //Optional - default: true
                        .searchIconColor(Color.WHITE) //Option - default: White
                        .setChoiceMode(MultiContactPicker.CHOICE_MODE_MULTIPLE) //Optional - default: CHOICE_MODE_MULTIPLE
                        .handleColor(ContextCompat.getColor(Home.this, R.color.colorPrimary)) //Optional - default: Azure Blue
                        .bubbleColor(ContextCompat.getColor(Home.this, R.color.colorPrimary)) //Optional - default: Azure Blue
                        .bubbleTextColor(Color.WHITE) //Optional - default: White
                        .setTitleText("Select Contacts") //Optional - default: Select Contacts
                        .setLoadingType(MultiContactPicker.LOAD_ASYNC) //Optional - default LOAD_ASYNC (wait till all loaded vs stream results)
                        .limitToColumn(LimitColumn.NONE) //Optional - default NONE (Include phone + email, limiting to one can improve loading time)
                        .setActivityAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                                android.R.anim.fade_in,
                                android.R.anim.fade_out) //Optional - default: No animation overrides
                        .showPickerForResult(CONTACT_PICKER_REQUEST);
            }
        });

        // Tratando o List View
        adapter = new ContatoAdapter(this,listaContatos);
        listView.setAdapter(adapter);
    }



    // ------------------------- FUNÇÕES -------------------------
    private boolean isAccessibilityOn(Context context) {
        int accessibilityEnabled = 0;
        final String service = context.getPackageName () + "/" + WhatsAppAccessibilityService.class.getCanonicalName ();
        try {
            accessibilityEnabled = Settings.Secure.getInt (context.getApplicationContext ().getContentResolver (), Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException ignored) {  }

        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter (':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString (context.getApplicationContext ().getContentResolver (), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                colonSplitter.setString (settingValue);
                while (colonSplitter.hasNext ()) {
                    String accessibilityService = colonSplitter.next ();

                    if (accessibilityService.equalsIgnoreCase (service)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void enviarFacebook() {

    }

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
        MySMSservice.startActionWHATSAPP(getApplicationContext(),editMensagem.getText().toString(),
                results);
        /*
        // Verificar se o aplicativo do whatsapp está instalado
        boolean instalado = appInstalado("com.whatsapp");
        String mensagem = editMensagem.getText().toString();

        try{
            PackageManager packageManager = getApplicationContext().getPackageManager();
            if(listaContatos.isEmpty() || editMensagem.getText().toString().equals("")){
                Toast.makeText(this,"Adicione um contato à lista ou digite uma mensagem!",Toast.LENGTH_SHORT).show();
            }
            else if(instalado){
                for(int i=0; i<listaContatos.size(); i++){
                    String numero = listaContatos.get(i).getTelefone();
                    String url = "https://api.whatsapp.com/send?phone=" + numero + "&text=" + URLEncoder.encode(mensagem, "UTF-8");
                    Intent whatsappIntent = new Intent(Intent.ACTION_VIEW);
                    whatsappIntent.setPackage("com.whatsapp");
                    whatsappIntent.setData(Uri.parse(url));
                    whatsappIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startService(whatsappIntent);
                    if(whatsappIntent.resolveActivity(packageManager)!=null) {
                        startActivity(whatsappIntent);
                        Thread.sleep(10000);
                    }
                }
            }else{
                Toast.makeText(this, "Whatsapp não instalado no seu dispositivo.", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

         */
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

    // onActivityResult antigo
    /*
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
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONTACT_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                results = MultiContactPicker.obtainResult(data);
                List<String> nomes = new ArrayList<>();
                List<String> numeros = new ArrayList<>();
                try{
                    for(int i=0; i<results.size(); i++) {
                        nomes.add(results.get(i).getDisplayName());
                        numeros.add(results.get(i).getPhoneNumbers().get(0).getNumber());
                        //Contato contato = new Contato();
                        Contato contato = new Contato("Pedro", "(79) 88888-8888", R.drawable.ic_pessoa);
                        contato.setNome(nomes.get(i));
                        contato.setTelefone(numeros.get(i));
                        contato.setImagem(R.drawable.ic_pessoa);
                        listaContatos.add(contato);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                adapter.notifyDataSetChanged();
            } else if (resultCode == RESULT_CANCELED) {
                System.out.println("User closed the picker without selecting items.");
            }
        }
    }
}