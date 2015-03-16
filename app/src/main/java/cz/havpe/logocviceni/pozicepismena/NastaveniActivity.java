package cz.havpe.logocviceni.pozicepismena;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import cz.havpe.logocviceni.R;


public class NastaveniActivity extends Activity {

    EditText txtSlovnik;

    public static final String MY_PREFERENCES = "MyPrefs" ;
    public static final String GENEROVAT_NAHODNE_KEY = "generovatNahodneKey";
    public static final String ZKOUSENE_PISMENO_KEY = "zkousenePismenoKey";

    SharedPreferences sharedpreferences;
    CheckBox generovatNahodne;
    Context ctx = (Context) this;
    Button btnSlovnkSDkarta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_pozice_nastaveni);

        //příprava keše slovníku
        SlovnikDao dao = new SlovnikDao(ctx);
        dao.inicializaceKurzoruSlov();
        String slova = dao.nactiVsechnaSlova();

        txtSlovnik = ((EditText)findViewById(R.id.slovnik));
        btnSlovnkSDkarta = (Button) findViewById(R.id.slovnkSDkarta);

        txtSlovnik.setText(slova);
        generovatNahodne = (CheckBox) findViewById(R.id.chkGenerovatNahodne);

        sharedpreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);

        if (sharedpreferences.contains(GENEROVAT_NAHODNE_KEY))
        {
            generovatNahodne.setChecked(sharedpreferences.getBoolean(GENEROVAT_NAHODNE_KEY, false));
        }

        btnSlovnkSDkarta.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    _importujSlovnk();
                                                }
                                            }

        );

        btnSlovnkSDkarta.requestFocus();
    }

    public void btnVytvoreniSlovnikuClicked(View obj) {
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
        //editor.commit();
        editor.apply();

        SlovnikDao.poradiSlova = 0; //začnu vždy od začátku
        PozicePismenaActivity.pouzitaSlova = new HashSet<>();
    }

    public void btnZacitNoveZkouseniClicked(View obj) {
        PozicePismenaActivity.pouzitaSlova = new HashSet<>();
        SlovnikDao.poradiSlova = 0;

        Toast.makeText(ctx, "Je možné začít nové zkoušení slov.", Toast.LENGTH_SHORT).show();
    }

    private void _importujSlovnk() {

        File file = Environment.getExternalStorageDirectory();
        File textFile = new File(file + File.separator +  "LogoCviceni" + File.separator + "UrceniPismena" + File.separator + "UrceniPismena.txt");

        StringBuilder slova2 = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(textFile));
            String line;

            while ((line = br.readLine()) != null) {
                slova2.append(line);
                slova2.append('\n');
            }
            br.close();

            txtSlovnik.setText(slova2);

            SlovnikDao dao = new SlovnikDao(ctx);

            dao.smazSlova();

            String[] slova = txtSlovnik.getText().toString().split("\\n");

            _vlozSlova(dao, slova);
            dao.inicializaceKurzoruSlov();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
        Toast.makeText(ctx, "Import slov z SD karty se zdařil.", Toast.LENGTH_SHORT).show();
    }
}
