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


public class PozicePismenaActivity extends Activity {

    MediaPlayer player = new MediaPlayer();
    final int POCET_POKUSU = 1000;
    int pocetObrazku = 0;
    ImageView imgAno;
    ImageView imgNe;
    TextView txtHadaneSlovo;
    EditText txtZkousenePismeno;
    Button btnJine;
    ImageView imgZacatek;
    ImageView imgKonec;
    ImageView imgStred;

    ImageView imgVysledek1;
    ImageView imgVysledek2;
    ImageView imgVysledek3;
    ImageView imgVysledek4;
    ImageView imgVysledek5;
    ImageView imgVysledek6;
    ImageView imgVysledek7;
    ImageView imgVysledek8;
    ImageView imgVysledek9;
    ImageView imgVysledek10;

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

        setContentView(R.layout.activity_pozice_pismena);


        imgAno = (ImageView) findViewById(R.id.imgOdpovedAno);
        imgNe = (ImageView) findViewById(R.id.imgOdpovedNe);
        txtHadaneSlovo = (TextView)findViewById(R.id.hadaneSlovo);
        txtZkousenePismeno = (EditText)findViewById(R.id.zkousenePismeno);
        btnJine = (Button)findViewById(R.id.btnJine);
        imgZacatek = (ImageView) findViewById(R.id.imgZacatek);
        imgKonec = (ImageView) findViewById(R.id.imgKonec);
        imgStred = (ImageView) findViewById(R.id.imgStred);

        imgVysledek1 = (ImageView) findViewById(R.id.imgVysledek1);
        imgVysledek2 = (ImageView) findViewById(R.id.imgVysledek2);
        imgVysledek3 = (ImageView) findViewById(R.id.imgVysledek3);
        imgVysledek4 = (ImageView) findViewById(R.id.imgVysledek4);
        imgVysledek5 = (ImageView) findViewById(R.id.imgVysledek5);
        imgVysledek6 = (ImageView) findViewById(R.id.imgVysledek6);
        imgVysledek7 = (ImageView) findViewById(R.id.imgVysledek7);
        imgVysledek8 = (ImageView) findViewById(R.id.imgVysledek8);
        imgVysledek9 = (ImageView) findViewById(R.id.imgVysledek9);
        imgVysledek10 = (ImageView) findViewById(R.id.imgVysledek10);

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

        //obnovení zkoušeného písmene
        sharedpreferences = getSharedPreferences(PoziceNastaveniActivity.MY_PREFERENCES, Context.MODE_PRIVATE);
        if (sharedpreferences.contains(PoziceNastaveniActivity.ZKOUSENE_PISMENO_KEY))
        {
            txtZkousenePismeno.setText(sharedpreferences.getString(PoziceNastaveniActivity.ZKOUSENE_PISMENO_KEY, null));
        }

        btnJine.requestFocus();

        if (pouzitaSlova.size() == 0) {
            _incicializaceOdpovedi();
        }
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

        if (pouzitaSlova.size() == 3) {
            _incicializaceOdpovedi();
            SlovnikDao dao = new SlovnikDao(this);
            dao.inicializaceKurzoruSlov();
        }

        String zkousenePismeno = txtZkousenePismeno.getText().toString();

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PoziceNastaveniActivity.ZKOUSENE_PISMENO_KEY, zkousenePismeno);
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

        if (sharedpreferences.contains(PoziceNastaveniActivity.GENEROVAT_NAHODNE_KEY))
        {
            generovatNahodne = sharedpreferences.getBoolean(PoziceNastaveniActivity.GENEROVAT_NAHODNE_KEY, false);
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

        if (nahodneSlovo.trim().length() > 0) {
            pouzitaSlova.add(nahodneSlovo);
            txtHadaneSlovo.setText(nahodneSlovo);
            _nastavMoznostKlikani(true);
        }
        dao.close();
        btnJine.setClickable(false);
    }

    private void _vyhodnotSlovo(VyskytPismena aVyskytPismena) {
        _nastavMoznostKlikani(false);

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

        if (player.isPlaying()) {
            player.stop();
            player.release();
        }

        if (nalezenaPozice.equals(aVyskytPismena)) {
            _nastavOdpoved(OdpovedAnoNe.ANO);
            player = MediaPlayer.create(this,  R.raw.zvuk_spravne);
            player.start();
            imgAno.setVisibility(View.VISIBLE);

            imgAno.setImageResource(mapObrazekAno.get(nahodaObrazek));

        } else {
            _nastavOdpoved(OdpovedAnoNe.NE);
            player = MediaPlayer.create(this,  R.raw.zvuk_spatne);
            player.start();
            imgNe.setVisibility(View.VISIBLE);
            imgNe.setImageResource(mapObrazekNe.get(nahodaObrazek));
        }

        //listenery
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                txtHadaneSlovo.setText("");
                btnJine.setClickable(true);
            }
        });


    }

    private void _schovejOdpovedi() {
        imgAno.setVisibility(View.INVISIBLE);
        imgNe.setVisibility(View.INVISIBLE);
    }

    private void _nastavMoznostKlikani(Boolean aKlikat) {
        imgZacatek.setClickable(aKlikat);
        imgKonec.setClickable(aKlikat);
        imgStred.setClickable(aKlikat);
    }

    private void _incicializaceOdpovedi(){
        imgVysledek1.setVisibility(View.INVISIBLE);
        imgVysledek2.setVisibility(View.INVISIBLE);
        imgVysledek3.setVisibility(View.INVISIBLE);
        imgVysledek4.setVisibility(View.INVISIBLE);
        imgVysledek5.setVisibility(View.INVISIBLE);
        imgVysledek6.setVisibility(View.INVISIBLE);
        imgVysledek7.setVisibility(View.INVISIBLE);
        imgVysledek8.setVisibility(View.INVISIBLE);
        imgVysledek9.setVisibility(View.INVISIBLE);
        imgVysledek10.setVisibility(View.INVISIBLE);
    }

    private void _nastavOdpoved(OdpovedAnoNe aOdpovedAno) {
        switch (pouzitaSlova.size()) {
            case 1:
                if (aOdpovedAno.equals(OdpovedAnoNe.ANO)) {
                    imgVysledek1.setImageResource(R.drawable.odpoved_spravne);
                } else {
                    imgVysledek1.setImageResource(R.drawable.odpoved_spatne);
                }
                imgVysledek1.setVisibility(View.VISIBLE);
                break;
            case 2:
                if (aOdpovedAno.equals(OdpovedAnoNe.ANO)) {
                    imgVysledek2.setImageResource(R.drawable.odpoved_spravne);
                } else {
                    imgVysledek2.setImageResource(R.drawable.odpoved_spatne);
                }
                imgVysledek2.setVisibility(View.VISIBLE);
                break;
            case 3:
                if (aOdpovedAno.equals(OdpovedAnoNe.ANO)) {
                    imgVysledek3.setImageResource(R.drawable.odpoved_spravne);
                } else {
                    imgVysledek3.setImageResource(R.drawable.odpoved_spatne);
                }
                imgVysledek3.setVisibility(View.VISIBLE);
                break;
            case 4:
                if (aOdpovedAno.equals(OdpovedAnoNe.ANO)) {
                    imgVysledek4.setImageResource(R.drawable.odpoved_spravne);
                } else {
                    imgVysledek4.setImageResource(R.drawable.odpoved_spatne);
                }
                imgVysledek4.setVisibility(View.VISIBLE);
                break;
            case 5:
                if (aOdpovedAno.equals(OdpovedAnoNe.ANO)) {
                    imgVysledek5.setImageResource(R.drawable.odpoved_spravne);
                } else {
                    imgVysledek5.setImageResource(R.drawable.odpoved_spatne);
                }
                imgVysledek5.setVisibility(View.VISIBLE);
                break;
            case 6:
                if (aOdpovedAno.equals(OdpovedAnoNe.ANO)) {
                    imgVysledek6.setImageResource(R.drawable.odpoved_spravne);
                } else {
                    imgVysledek6.setImageResource(R.drawable.odpoved_spatne);
                }
                imgVysledek6.setVisibility(View.VISIBLE);
                break;
            case 7:
                if (aOdpovedAno.equals(OdpovedAnoNe.ANO)) {
                    imgVysledek7.setImageResource(R.drawable.odpoved_spravne);
                } else {
                    imgVysledek7.setImageResource(R.drawable.odpoved_spatne);
                }
                imgVysledek7.setVisibility(View.VISIBLE);
                break;
            case 8:
                if (aOdpovedAno.equals(OdpovedAnoNe.ANO)) {
                    imgVysledek8.setImageResource(R.drawable.odpoved_spravne);
                } else {
                    imgVysledek8.setImageResource(R.drawable.odpoved_spatne);
                }
                imgVysledek8.setVisibility(View.VISIBLE);
                break;
            case 9:
                if (aOdpovedAno.equals(OdpovedAnoNe.ANO)) {
                    imgVysledek9.setImageResource(R.drawable.odpoved_spravne);
                } else {
                    imgVysledek9.setImageResource(R.drawable.odpoved_spatne);
                }
                imgVysledek9.setVisibility(View.VISIBLE);
                break;
            case 10:
                if (aOdpovedAno.equals(OdpovedAnoNe.ANO)) {
                    imgVysledek10.setImageResource(R.drawable.odpoved_spravne);
                } else {
                    imgVysledek10.setImageResource(R.drawable.odpoved_spatne);
                }
                imgVysledek10.setVisibility(View.VISIBLE);
                break;
        }
    }
}
