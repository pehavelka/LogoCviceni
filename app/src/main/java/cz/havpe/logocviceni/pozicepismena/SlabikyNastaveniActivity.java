package cz.havpe.logocviceni.pozicepismena;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import cz.havpe.logocviceni.R;

public class SlabikyNastaveniActivity extends Activity {

    ImageButton imageBtn;
    MediaPlayer player = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_slabiky_nastaveni);

        File image = new  File(Environment.getExternalStorageDirectory().toString() + "//LogoCviceni//Obrazky//Abeceda//čepice.png");
        if(image.exists()){
            imageBtn = (ImageButton) findViewById(R.id.imgTest);
            imageBtn.setImageBitmap(BitmapFactory.decodeFile(image.getAbsolutePath()));

            imageBtn.setOnClickListener(new View.OnClickListener() {
                                                       @Override
                                                       public void onClick(View v) {
                                                           _zvuk();
                                                           Toast.makeText(SlabikyNastaveniActivity.this, "Klik na hada.", Toast.LENGTH_LONG).show();
                                                       }
                                                   }

            );
        }

    }

    private void _zvuk()  {
        MediaPlayer mp = new MediaPlayer();
        try {
            mp.setDataSource(Environment.getExternalStorageDirectory().toString() + "//LogoCviceni//Zvuky//Abeceda//č.mp3");
            mp.prepare();
            mp.start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getMessage());
        }

    }
}
