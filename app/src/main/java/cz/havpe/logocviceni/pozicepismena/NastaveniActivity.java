package cz.havpe.logocviceni.pozicepismena;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashSet;

import cz.havpe.logocviceni.R;


public class NastaveniActivity extends Activity {

    EditText txtSlovnik;

    public static final String MY_PREFERENCES = "MyPrefs" ;
    public static final String GENEROVAT_NAHODNE_KEY = "generovatNahodneKey";
    SharedPreferences sharedpreferences;
    CheckBox generovatNahodne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Context ctx = (Context) this;
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_nastaveni);

        //příprava keše slovníku
        SlovnikDao dao = new SlovnikDao(ctx);
        dao.inicializaceKurzoruSlov();
        String slova = dao.nactiVsechnaSlova();

        txtSlovnik = ((EditText)findViewById(R.id.slovnik));

        txtSlovnik.setText(slova);
        generovatNahodne = (CheckBox) findViewById(R.id.chkGenerovatNahodne);

        sharedpreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);

        if (sharedpreferences.contains(GENEROVAT_NAHODNE_KEY))
        {
            generovatNahodne.setChecked(sharedpreferences.getBoolean(GENEROVAT_NAHODNE_KEY, false));
        }
    }

    public void btnVytvoreniSlovnikuClicked(View obj) {
        Context ctx = (Context) this;

        if (txtSlovnik.getText().length() > 0){
            SlovnikDao dao = new SlovnikDao(ctx);

            dao.smazSlova();

            String[] slova = txtSlovnik.getText().toString().split("\\n");

            _vlozSlova(dao, slova);
            dao.inicializaceKurzoruSlov();
            //dao.close();
            Toast.makeText(ctx, "Slovník byl vytvořen ze zadaných slov.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(ctx, "Zadejte slovník!", Toast.LENGTH_SHORT).show();
        }
    }

    public void btnVytvoreniSlovnikuInterniClicked(View obj) {
        Context ctx = (Context)this;
        SlovnikDao dao = new SlovnikDao(ctx);

        dao.smazSlova();

        String slovnik = Utils.readStringFromResource(ctx, R.raw.slovnik_maly);
        String[] slova = slovnik.split("\\n");
        _vlozSlova(dao, slova);
        dao.inicializaceKurzoruSlov();
        //dao.close();

        String slova2 = dao.nactiVsechnaSlova();
        txtSlovnik.setText(slova2);


        Toast.makeText(ctx, "Slovník byl vytvořen z interní databáze.", Toast.LENGTH_SHORT).show();
    }

    private void _vlozSlova(SlovnikDao aDao, String[] aSlova) {
        for (String slovo:aSlova) {
            aDao.vlozSlovo(slovo);
        }
    }

    public void btnVycistitTextSlovnikuClicked(View obj) {
        txtSlovnik.setText("");
    }

    public void chkGenerovatNahodneChecked(View obj){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(GENEROVAT_NAHODNE_KEY,generovatNahodne.isChecked());
        editor.commit();

        SlovnikDao.poradiSlova = 0; //začnu vždy od začátku
        PozicePismena.pouzitaSlova = new HashSet<String>();
    }

    public void btnZacitNoveZkouseniClicked(View obj) {
        Context ctx = (Context)this;
        PozicePismena.pouzitaSlova = new HashSet<String>();
        SlovnikDao.poradiSlova = 0;

        Toast.makeText(ctx, "Je možné začít nové zkoušení slov.", Toast.LENGTH_SHORT).show();
    }
}
