package com.example.myapplicationpruebacaludefinal;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

//Logica
//1)Te pide el nombre
//2)Apretar Jugar para iniciar GameActivity.java


public class MainActivity extends AppCompatActivity {

   //Nombre de usuario
    public static final String EXTRA_PLAYER_NAME = "EXTRA_PLAYER_NAME";
    private EditText etPlayerName;

    //Boton Jugar
    private Button btnPlay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final View rootLayout = findViewById(R.id.rootLayout);

        final int basePadding = rootLayout.getPaddingLeft();

        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, windowInsets) -> {

            Insets bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());

            v.setPadding(basePadding + bars.left, basePadding + bars.top,
                    basePadding + bars.right, basePadding + bars.bottom);

            return WindowInsetsCompat.CONSUMED;
        });

        etPlayerName = findViewById(R.id.etPlayerName);
        btnPlay = findViewById(R.id.btnPlay);


        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = etPlayerName.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(MainActivity.this,
                            "Por favor ingresá tu nombre para jugar",
                            Toast.LENGTH_SHORT).show();

                    return;
                }

                Intent intent = new Intent(MainActivity.this, GameActivity.class);

                intent.putExtra(EXTRA_PLAYER_NAME, name);

                startActivity(intent);
            }
        });
    }
}
