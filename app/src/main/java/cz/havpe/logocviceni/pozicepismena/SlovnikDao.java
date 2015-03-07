package cz.havpe.logocviceni.pozicepismena;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashSet;


public class SlovnikDao {


    public static Cursor curSlova = null;
    public static Integer pocetSlov = null;
    public static Integer poradiSlova = 0;

    protected static final String DATABASE_NAME = "slovnik";
    protected static final int DATABASE_VERSION = 1;

    private SQLiteOpenHelper openHelper;

    static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE slova  ("
                    + " _id INTEGER PRIMARY KEY,"
                    + " slovo TEXT NOT NULL"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }

    public SlovnikDao(Context ctx) {
        openHelper = new DatabaseHelper(ctx);
    }


    public void smazSlova() {
        SQLiteDatabase db = openHelper.getWritableDatabase();

        db.delete("slova", null, null);
        db.close();
    }
    public long vlozSlovo(String aSlovo) {
        SQLiteDatabase db = openHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("slovo", aSlovo);

        long id = db.insert("slova", null, values);
        db.close();
        return id;
    }

    public String nactiNahodneSlovo() {
        String slovo = "";
        Integer pocet = 1;

        Integer nahoda = Utils.randInt(1, pocetSlov);

        curSlova.moveToFirst();

        do  {
            if (pocet.equals(nahoda)) {
                slovo = curSlova.getString(0);
            }
            pocet++;
        } while (curSlova.moveToNext());

        return slovo;
    }

    public String nactiVPoradiSlovo() {
        String slovo = "";
        Integer pocet = 0;

        curSlova.moveToFirst();

        do {
            if (pocet.equals(poradiSlova)) {
                slovo = curSlova.getString(0);
            }
            pocet++;
        }while (curSlova.moveToNext());

        poradiSlova++;

        if (poradiSlova > pocetSlov) {
            poradiSlova = 0;
            PozicePismenaActivity.pouzitaSlova = new HashSet<>();
        }
        return slovo;
    }

    public String nactiVsechnaSlova() {
        String slova = "";

        if (curSlova != null) {
            curSlova.moveToFirst();
            curSlova.moveToPrevious();

            while (curSlova.moveToNext()) {
                slova = slova + "\n" + curSlova.getString(0);
            }

            if (slova.length() > 0) {
                return slova.substring(1);
            } else {
                return "";
            }
        }

        return null;

    }

    public void close() {
        openHelper.close();
    }

    public void inicializaceKurzoruSlov() {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor cur;
        String[] columns;

        //počet slov ve slovníku
        columns = new String[]{"count(*)"};
        cur = db.query("slova", columns, null, null, null, null, null);
        cur.moveToNext();
        pocetSlov = cur.getInt(0);

        //načtení slova
        columns = new String[]{"slovo"};
        curSlova= db.query("slova", columns, null, null, null, null, null);

        poradiSlova = 0;
        PozicePismenaActivity.pouzitaSlova = new HashSet<>();
    }
}
