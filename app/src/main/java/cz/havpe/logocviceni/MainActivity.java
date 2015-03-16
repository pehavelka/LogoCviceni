package cz.havpe.logocviceni;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import cz.havpe.logocviceni.pozicepismena.PoziceNastaveniActivity;
import cz.havpe.logocviceni.pozicepismena.PozicePismenaActivity;
import cz.havpe.logocviceni.pozicepismena.SlabikyNastaveniActivity;


public class MainActivity extends Activity {

    Button btnSlabikyNastaveni;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        btnSlabikyNastaveni = (Button) findViewById(R.id.btnSlabikyNastaveni);

        btnSlabikyNastaveni.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(MainActivity.this, SlabikyNastaveniActivity.class);
                                                    startActivity(intent);
                                                }
                                            }

        );


    }

    public void btnPozicePismenaClicked(View obj) {
        Intent intent = new Intent(this, PozicePismenaActivity.class);
        startActivity(intent);
    }

    public void btnPozicePismenaNastaveniClicked(View obj) {
        Intent intent = new Intent(this, PoziceNastaveniActivity.class);
        startActivity(intent);
    }


}
