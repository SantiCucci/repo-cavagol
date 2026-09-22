package com.example.myapplicationpruebacaludefinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

//Pantalla final
//Muestra nombre, puntaje, Cantidad de rondas
//Volver a jugar
//Volver al Inicio

public class ResultActivity extends AppCompatActivity {

    private TextView tvResult;
    private Button btnPlayAgain;
    private Button btnBackToHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Habilita la flecha de "volver" en la barra superior
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final View rootLayout = findViewById(R.id.rootLayout);
        final int basePadding = rootLayout.getPaddingLeft();
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, windowInsets) -> {
            Insets bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(basePadding + bars.left, basePadding + bars.top,
                    basePadding + bars.right, basePadding + bars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        tvResult = findViewById(R.id.tvResult);
        btnPlayAgain = findViewById(R.id.btnPlayAgain);
        btnBackToHome = findViewById(R.id.btnBackToHome);


        final String playerName = getIntent().getStringExtra(GameActivity.EXTRA_PLAYER_NAME);


        int score = getIntent().getIntExtra(GameActivity.EXTRA_SCORE, 0);
        int totalRounds = getIntent().getIntExtra(GameActivity.EXTRA_TOTAL_ROUNDS, 10);

        //Resultados
        tvResult.setText("Juego terminado, " + playerName + ".\nTu puntaje es: "
                + score + "/" + totalRounds);

        //Boton jugar de nuevo
        btnPlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, GameActivity.class);
                intent.putExtra(GameActivity.EXTRA_PLAYER_NAME, playerName);
                startActivity(intent);
                finish();
            }
        });

        // Botón Volver al inicio
        btnBackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
