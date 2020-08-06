package com.example.menssageapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.facebook.share.model.ShareMessengerGenericTemplateContent;
import com.facebook.share.model.ShareMessengerGenericTemplateElement;
import com.facebook.share.model.ShareMessengerURLActionButton;
import com.facebook.share.widget.MessageDialog;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.material.animation.MatrixEvaluator;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.LimitColumn;
import com.wafflecopter.multicontactpicker.MultiContactPicker;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Home extends AppCompatActivity {

    private static final int CONTACT_PICKER_REQUEST = 202;
    TextView txtHora;
    TextView viewLimpar;
    ImageButton imageBtnSelecionarContato;
    ImageButton btnEnviarSMS;
    ImageButton btnEnviarWhats;
    //ImageButton btnEnviarFacebook;
    ImageButton btnHora;
    ListView listView;
    MultiAutoCompleteTextView editMensagem;

    private int hr=100;
    private int min=100;
    private int sec=100;
    private int days=1;
    private int dia=1;
    private int mes=1;
    private int ano=1;

    ArrayList<Contato> listaContatos = new ArrayList<Contato>();
    List<ContactResult> results = new ArrayList<>();

    ArrayAdapter<Contato> adapter;

    final int SEND_SMS_PERMISSION_REQUEST_CODE = 1;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_MOVE){
            listView.dispatchTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        viewLimpar = findViewById(R.id.viewLimparLista);
        txtHora = findViewById(R.id.viewHora);
        btnEnviarSMS = findViewById(R.id.btnEnviarSMS);
        btnEnviarWhats = findViewById(R.id.btnEnviarWhats);
        //btnEnviarFacebook = findViewById(R.id.btnEnviarFacebook);
        btnHora = findViewById(R.id.imageAgendar);
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


        viewLimpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listaContatos.clear();
                adapter.notifyDataSetChanged();
            }
        });

        btnHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR,year);
                        calendar.set(Calendar.MONTH,month);
                        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                        dia = dayOfMonth;
                        mes = month;
                        ano = year;

                        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                                calendar.set(Calendar.MINUTE,minute);

                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy - HH:mm");
                                txtHora.setText(simpleDateFormat.format(calendar.getTime()));
                                hr = hourOfDay;
                                min = minute;
                            }
                        };
                        new TimePickerDialog(Home.this,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
                    }
                };

                new DatePickerDialog(Home.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // Função para enviar o SMS
        btnEnviarSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarSMS();
            }
        });

        // Função para enviar o Whatsapp
        btnEnviarWhats.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                enviarWhats();
            }
        });

        /*
        // Função para enviar mensagem pelo Facebook
        btnEnviarFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarFacebook();
            }
        });
         */

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

    // CORRIGIR - NÃO ESTÁ FUNCIONANDO
    /*
    private void enviarFacebook() {
        String mensagem = editMensagem.getText().toString();
        int app_id = R.string.facebook_app_id;
        String user_id = "1991047964360713";

        if(listaContatos.isEmpty() || editMensagem.getText().toString().equals("")){
            Toast.makeText(this,"Adicione um contato ou digite uma mensagem. Tente novamente.",Toast.LENGTH_SHORT).show();
        }
        else {
            if (isPackageExisted("com.facebook.orca")) {
                for(int i=0; i<listaContatos.size(); i++) {
                    String numero = listaContatos.get(i).getTelefone();
                    String url = "fb-messenger://user/";

                    Uri uri = Uri.parse(url);
                    uri = ContentUris.withAppendedId(uri, Long.parseLong(user_id));
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.setData(uri);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(this, "Por favor instale o facebook messenger", Toast.LENGTH_LONG).show();
            }
        }

        /*
        ShareMessengerURLActionButton actionButton =
                new ShareMessengerURLActionButton.Builder()
                        .setTitle("Visit Facebook")
                        .setUrl(Uri.parse("https://www.facebook.com"))
                        .build();
        ShareMessengerGenericTemplateElement genericTemplateElement =
                new ShareMessengerGenericTemplateElement.Builder()
                        .setTitle("Visit Facebook")
                        .setSubtitle("Visit Messenger")
                        .setImageUrl(Uri.parse("heeps://Your/Image/Url"))
                        .setButton(actionButton)
                        .build();
        ShareMessengerGenericTemplateContent genericTemplateContent =
                new ShareMessengerGenericTemplateContent.Builder()
                        .setPageId(String.valueOf(R.string.facebook_app_id)) // Your page ID, required
                        .setGenericTemplateElement(genericTemplateElement)
                        .build();

        MessageDialog.show(Home, genericTemplateContent);


    }
     */

    // OK
    public void enviarSMS(){

        if(editMensagem.getText().toString().equals("")){
            Toast.makeText(this,"Digite uma mensagem.",Toast.LENGTH_SHORT).show();
        }else if(listaContatos.isEmpty()){
            Toast.makeText(this,"Adicione um contato na lista.",Toast.LENGTH_SHORT).show();
        }else if(checarPermissaoSMS(Manifest.permission.SEND_SMS)){
            List<String> numbersList = new ArrayList<String>();
            for(int i=0; i<results.size(); i++){
                numbersList.add(results.get(i).getPhoneNumbers().get(0).getNumber());
            }
            String[] numbers = numbersList.toArray(new String[0]);
            long flexTime = calculateFlex(hr,min,sec,days,dia,mes,ano);

            Data messageData = new Data.Builder()
                    .putString("message", editMensagem.getText().toString())
                    .putStringArray("contacts",numbers)
                    .build();

            PeriodicWorkRequest sendSMSWorker = new PeriodicWorkRequest.Builder(sendSMSWorker.class,days,
                    TimeUnit.DAYS,
                    flexTime, TimeUnit.MILLISECONDS)
                    .addTag("send_sms_work")
                    .setInputData(messageData)
                    .build();

            WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork("send_sms_work",
                    ExistingPeriodicWorkPolicy.REPLACE,sendSMSWorker);
            Toast.makeText(this, "SMS agendado!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Permissão negada!", Toast.LENGTH_SHORT).show();
        }
    }

    // OK
    public void enviarWhats(){
        if(!results.isEmpty()){
            if(!editMensagem.getText().toString().isEmpty()){
                if(appInstalado("com.whatsapp")) {
                    List<String> numbersList = new ArrayList<String>();
                    for (int i = 0; i < results.size(); i++) {
                        numbersList.add(results.get(i).getPhoneNumbers().get(0).getNumber());
                    }
                    String[] numbers = numbersList.toArray(new String[0]);
                    long flexTime = calculateFlex(hr, min, sec, days, dia, mes, ano);

                    Data messageData = new Data.Builder()
                            .putString("message", editMensagem.getText().toString())
                            .putStringArray("contacts", numbers)
                            .build();

                    PeriodicWorkRequest sendMessagework = new PeriodicWorkRequest.Builder(sendMessageWorker.class, days,
                            TimeUnit.DAYS,
                            flexTime, TimeUnit.MILLISECONDS)
                            .setInputData(messageData)
                            .addTag("send_message_work")
                            .build();

                    WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork("send_message_work",
                            ExistingPeriodicWorkPolicy.REPLACE, sendMessagework);
                    Toast.makeText(Home.this, "Whatsapp agendado!", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(Home.this, "Whatsapp não instalado!", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(Home.this, "Por favor adicione uma mensagem!", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(Home.this, "Selecione um número de contato.", Toast.LENGTH_SHORT).show();
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

    // Verificando se o Facebook está instalado
    public boolean isPackageExisted(String targetPackage) {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(targetPackage, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

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
                System.out.println("Usuário fechou lista sem selecionar nenhum contato.");
            }
        }
    }

    private long calculateFlex(int hourOfTheDay,int minute,int sec, int periodInDays, int day, int month, int year) {

        // Initialize the calendar with today and the preferred time to run the job.
        Calendar cal1 = Calendar.getInstance();
        cal1.set(Calendar.DAY_OF_MONTH,day);
        cal1.set(Calendar.MONTH,month);
        cal1.set(Calendar.YEAR,year);
        cal1.set(Calendar.HOUR_OF_DAY, hourOfTheDay);
        cal1.set(Calendar.MINUTE, minute);
        cal1.set(Calendar.SECOND, sec);

        // Initialize a calendar with now.
        Calendar cal2 = Calendar.getInstance();

        if (cal2.getTimeInMillis() < cal1.getTimeInMillis()) {
            // Add the worker periodicity.
            cal2.setTimeInMillis(cal2.getTimeInMillis() + TimeUnit.DAYS.toMillis(periodInDays));
        }

        long delta = (cal2.getTimeInMillis() - cal1.getTimeInMillis());

        return ((delta > PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS) ? delta
                : PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS);
    }
}