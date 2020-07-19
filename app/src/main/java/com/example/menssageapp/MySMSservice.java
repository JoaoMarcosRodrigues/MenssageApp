package com.example.menssageapp;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.wafflecopter.multicontactpicker.ContactResult;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MySMSservice extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_WHATSAPP = "com.example.menssageapp.action.WHATSAPP";

    // TODO: Rename parameters
    private static final String MENSAGEM = "com.example.menssageapp.extra.PARAM1";
    private static final String NUMEROS = "com.example.menssageapp.extra.PARAM2";

    public MySMSservice() {
        super("MySMSservice");
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionWHATSAPP(Context context, String mensagem, List<ContactResult> mobile_numbers) {
        List<String> numeros = new ArrayList<String>();

        for(int i=0; i<mobile_numbers.size(); i++){
            numeros.add(mobile_numbers.get(i).getPhoneNumbers().get(0).getNumber());
        }

        String[] numbersArray = numeros.toArray(new String[0]);

        Intent intent = new Intent(context, MySMSservice.class);
        intent.setAction(ACTION_WHATSAPP);
        intent.putExtra(MENSAGEM, mensagem);
        intent.putExtra(NUMEROS, numbersArray);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_WHATSAPP.equals(action)) {
                final String mensagem = intent.getStringExtra(MENSAGEM);
                final String[] numeros = intent.getStringArrayExtra(NUMEROS);
                handleActionWHATSAPP(mensagem, numeros);
            }
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionWHATSAPP(String mensagem, String[] numeros) {
        // TODO: Handle action Baz
        try{
            PackageManager packageManager = getApplicationContext().getPackageManager();
            if(numeros.length!=0) {
                for (int i = 0; i < numeros.length; i++) {
                    String numero = numeros[i];
                    String url = "https://api.whatsapp.com/send?phone=" + numero + "&text=" + URLEncoder.encode(mensagem, "UTF-8");
                    Intent whatsappIntent = new Intent(Intent.ACTION_VIEW);
                    whatsappIntent.setPackage("com.whatsapp");
                    whatsappIntent.setData(Uri.parse(url));
                    whatsappIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (whatsappIntent.resolveActivity(packageManager) != null) {
                        getApplicationContext().startActivity(whatsappIntent);
                        Thread.sleep(2000);
                    } else {
                        sendBroadcastMessage("Whatsapp não instalado.");
                    }
                }
            }
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Falha: "+e.toString(),Toast.LENGTH_SHORT).show();
        }
    }

    private void sendBroadcastMessage(String message){
        Intent localIntent = new Intent("my.own.broadcast");
        localIntent.putExtra("result",message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }
}
