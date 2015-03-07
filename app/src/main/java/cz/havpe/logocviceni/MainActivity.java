package cz.havpe.logocviceni;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import cz.havpe.logocviceni.pozicepismena.NastaveniActivity;
import cz.havpe.logocviceni.pozicepismena.PozicePismenaActivity;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


    }

    public void btnPozicePismenaClicked(View obj) {
        Intent intent = new Intent(this, PozicePismenaActivity.class);
        startActivity(intent);
    }

    public void btnPozicePismenaNastaveniClicked(View obj) {
        Intent intent = new Intent(this, NastaveniActivity.class);
        startActivity(intent);
    }


}
