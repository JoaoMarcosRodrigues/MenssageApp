package com.example.menssageapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {
    private static String NOME_BANCO = "messageapp.db";
    private static final String NOME_TABELA = "horario";
    private static final String COL1 = "id";
    private static final String COL2 = "horario";
    private static final String COL3 = "mensagem";
    private static final String COL4 = "status";

    private static int VERSAO = 3;

    public DBHelper(Context context){
        super(context,NOME_BANCO,null,VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE [horario] (\n" +
                COL1+ " INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                COL2+ " VARCHAR(5)  NOT NULL,\n" +
                COL3+ " VARCHAR(255)  NOT NULL,\n" +
                COL4+ " BOOLEAN DEFAULT 'true' NOT NULL\n" +
                ")";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS "+NOME_TABELA;
        db.execSQL(sql);
        onCreate(db);
    }

    public boolean addHorario(String horario, String mensagem, boolean status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL2,horario);
        contentValues.put(COL3,mensagem);
        contentValues.put(COL4,status);

        long result = db.insert(NOME_TABELA,null,contentValues);

        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public Cursor getAllHorarios(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+NOME_TABELA,null);

        return cursor;
    }

    // OK
    public boolean redefinirStatus(String id, boolean status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL4,status);

        int result = db.update(NOME_TABELA,contentValues,"id = ?",new String[]{id});

        if(result == -1)
            return false;
        else
            return true;
    }
}
