package cz.havpe.logocviceni.pozicepismena;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cz.havpe.logocviceni.R;


public class PozicePismena extends Activity {

    final int POCET_POKUSU = 1000;
    int pocetObrazku = 0;
    ImageView imgAno;
    ImageView imgNe;
    TextView txtHadaneSlovo;
    EditText txtZkousenePismeno;
    Button btnJine;

    SharedPreferences sharedpreferences;

    public static Set<String> pouzitaSlova = new HashSet<>();
    Map<Integer, Integer> mapObrazekAno = new HashMap<>();
    Map<Integer, Integer> mapObrazekNe = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.pozice_pismena);

        sharedpreferences = getSharedPreferences(NastaveniActivity.MY_PREFERENCES, Context.MODE_PRIVATE);

        imgAno = (ImageView) findViewById(R.id.imgOdpovedAno);
        imgNe = (ImageView) findViewById(R.id.imgOdpovedNe);
        txtHadaneSlovo = (TextView)findViewById(R.id.hadaneSlovo);
        txtZkousenePismeno = (EditText)findViewById(R.id.zkousenePismeno);
        btnJine = (Button)findViewById(R.id.btnJine);

        SlovnikDao dao = new SlovnikDao(this);
        dao.inicializaceKurzoruSlov();

        //keš odpovědí
        mapObrazekAno.put(1, R.drawable.odpoved_ano1);
        mapObrazekAno.put(2, R.drawable.odpoved_ano2);
        mapObrazekAno.put(3, R.drawable.odpoved_ano3);
        mapObrazekAno.put(4, R.drawable.odpoved_ano4);
        mapObrazekAno.put(5, R.drawable.odpoved_ano5);

        mapObrazekNe.put(1, R.drawable.odpoved_ne1);
        mapObrazekNe.put(2, R.drawable.odpoved_ne2);
        mapObrazekNe.put(3, R.drawable.odpoved_ne3);
        mapObrazekNe.put(4, R.drawable.odpoved_ne4);
        mapObrazekNe.put(5, R.drawable.odpoved_ne5);

        pocetObrazku = mapObrazekAno.size();

        if (sharedpreferences.contains(NastaveniActivity.ZKOUSENE_PISMENO_KEY))
        {
            txtZkousenePismeno.setText(sharedpreferences.getString(NastaveniActivity.ZKOUSENE_PISMENO_KEY, null));
        }

        btnJine.requestFocus();
    }

    public void imgZacatekClicked(View obj) {
        _vyhodnotSlovo(VyskytPismena.ZACATEK);
    }

    public void imgStredClicked(View obj) {
        _vyhodnotSlovo(VyskytPismena.STRED);
    }

    public void imgKonecClicked(View obj) {
        _vyhodnotSlovo(VyskytPismena.KONEC);
    }

    public void btnJineClicked(View obj) {

        _schovejOdpovedi();

        String zkousenePismeno = txtZkousenePismeno.getText().toString();

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(NastaveniActivity.ZKOUSENE_PISMENO_KEY, zkousenePismeno);
        editor.commit();

        if (zkousenePismeno.length() == 0) {
            Toast.makeText(this,
                    "Nezadáno zkoušené písmeno!!!",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Boolean hledat = true;
        Integer pocet = 0;
        Boolean generovatNahodne = false;

        SlovnikDao dao = new SlovnikDao(this);
        String nahodneSlovo;

        if (sharedpreferences.contains(NastaveniActivity.GENEROVAT_NAHODNE_KEY))
        {
            generovatNahodne = sharedpreferences.getBoolean(NastaveniActivity.GENEROVAT_NAHODNE_KEY, false);
        }

        do {
            if (generovatNahodne) {
                nahodneSlovo = dao.nactiNahodneSlovo();
            } else {
                nahodneSlovo = dao.nactiVPoradiSlovo();
            }
            pocet++;

            //když je zkoušený znak vícekrát
            //, nebo když nové slovo je stejné s aktiálním
            //, nebo když slovo bylo použito
            //, tak se ignoruje
            Integer pocetZnaku = nahodneSlovo.length() - nahodneSlovo.replace(zkousenePismeno, "").length();

            if (pocetZnaku > 1
                || txtHadaneSlovo.getText().toString().equals(nahodneSlovo)
                || pouzitaSlova.contains(nahodneSlovo) ) {
                if (pocet == POCET_POKUSU) {
                    nahodneSlovo = "";
                    hledat = false;
                    Toast.makeText(this,
                            "Vhodné slovo nenalezeno!!!",
                            Toast.LENGTH_SHORT).show();
                }
                continue;
            }

            //ošetření proti zacyklení
            if (pocet == POCET_POKUSU
                || nahodneSlovo.contains(zkousenePismeno)) {
                hledat = false;

                if (pocet == POCET_POKUSU) {
                    nahodneSlovo = "";
                    Toast.makeText(this,
                            "Vhodné slovo nenalezeno!!!",
                            Toast.LENGTH_SHORT).show();
                }
            }

        } while(hledat);

        pouzitaSlova.add(nahodneSlovo);
        txtHadaneSlovo.setText(nahodneSlovo);
        dao.close();
    }

    private void _vyhodnotSlovo(VyskytPismena aVyskytPismena) {
        _schovejOdpovedi();


        MediaPlayer player;
        String zkousenePismeno = txtZkousenePismeno.getText().toString();
        if (zkousenePismeno.trim().length() ==0) {
            Toast.makeText(this,
                    "Není zadané zkoušené písmeno!!!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        VyskytPismena nalezenaPozice;

        //zjistím, kde se nachází zkoušený znak
        String hadaneSlovo = txtHadaneSlovo.getText().toString();
        if (hadaneSlovo.trim().length() ==0) {
            Toast.makeText(this,
                    "Není zadané slovo!!!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //počet znaků ve slově
        Integer pocetZnaku = hadaneSlovo.length() - hadaneSlovo.replace(zkousenePismeno, "").length();

        if (pocetZnaku >1) {
            Toast.makeText(this,
                    "Slovo obsahuje více zkoušených písmen!!!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //ověření pozice
        int pozice = hadaneSlovo.indexOf(zkousenePismeno);

        if (pozice == -1) {
            Toast.makeText(this,
                    "Zkoušené písmeno není ve slově!!!",
                    Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (pozice == 0) {
                nalezenaPozice = VyskytPismena.ZACATEK;
            } else if (pozice == (hadaneSlovo.length() - 1)) {
                nalezenaPozice = VyskytPismena.KONEC;
            } else {
                nalezenaPozice = VyskytPismena.STRED;
            }
        }

        Integer nahodaObrazek = Utils.randInt(1, pocetObrazku);

        if (nalezenaPozice.equals(aVyskytPismena)) {
            player = MediaPlayer.create(this,  R.raw.zvuk_spravne);
            player.start();
            imgAno.setVisibility(View.VISIBLE);

            imgAno.setImageResource(mapObrazekAno.get(nahodaObrazek));
            //imgAno.setImageResource(ctx.getResources().getIdentifier("drawable/odpoved_ano" + nah + ".png", null, ctx.getPackageName()));
        } else {
            player = MediaPlayer.create(this,  R.raw.zvuk_spatne);
            player.start();
            imgNe.setVisibility(View.VISIBLE);

            imgNe.setImageResource(mapObrazekNe.get(nahodaObrazek));
        }
    }

    private void _schovejOdpovedi() {
        imgAno.setVisibility(View.INVISIBLE);
        imgNe.setVisibility(View.INVISIBLE);
    }

}
