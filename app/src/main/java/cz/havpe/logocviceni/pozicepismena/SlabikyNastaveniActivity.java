package cz.havpe.logocviceni.pozicepismena;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import cz.havpe.logocviceni.R;

public class SlabikyNastaveniActivity extends Activity {

    ImageButton imageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_slabiky_nastaveni);

        File image = new  File(Environment.getExternalStorageDirectory().toString() + "//LogoCviceni//Obrazky//Abeceda//had.png");
        if(image.exists()){
            imageBtn= (ImageButton) findViewById(R.id.imgTest);
            imageBtn.setImageBitmap(BitmapFactory.decodeFile(image.getAbsolutePath()));

            imageBtn.setOnClickListener(new View.OnClickListener() {
                                                       @Override
                                                       public void onClick(View v) {
                                                           Toast.makeText(SlabikyNastaveniActivity.this, "Klik na hada.", Toast.LENGTH_LONG).show();
                                                       }
                                                   }

            );
        }
    }

}
