package com.example.myapplicationpruebacaludefinal;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {


    // Constantes que se usan para guardar y leer datos entre Activities.
    public static final String EXTRA_PLAYER_NAME = "EXTRA_PLAYER_NAME";
    public static final String EXTRA_SCORE = "EXTRA_SCORE";
    public static final String EXTRA_TOTAL_ROUNDS = "EXTRA_TOTAL_ROUNDS";

    // Milisegundos  del cartel de"Correcto/Incorrecto"

    private static final long FEEDBACK_DELAY_MS = 1300;


    //Textos
    private VideoView videoView;
    private TextView tvPlayerName;
    private TextView tvRoundCounter;
    private TextView tvFeedback;

    //Botones
    //OPciones
    //Volver a reproducir
    private Button btnOptionA, btnOptionB, btnOptionC, btnOptionD;
    private Button[] optionButtons;
    private ImageButton btnRewind;
    private Button btnNextRound;


    //"Pantalla completa":
    //Contenido
    //Pantalla
    //Contenedor Pantalla COmpleta
    //Expandir
    //Cerrar

    private View mainContent;
    private FrameLayout videoFrame;
    private FrameLayout fullscreenContainer;
    private ImageButton btnExpand;
    private ImageButton btnCollapse;
    private boolean isFullscreen = false;


    //Estado del juego
    //Lista de Preguntas
    //En que jugada vamos
    //Puntos acumulados
    //Nombre del usuario

    private List<Question> questions;
    private int currentRoundIndex = 0;
    private int score = 0;
    private String playerName;

    private boolean waitingForAnswer = false;

    // true desde que el usuario responde hasta que toca "Siguiente ronda"
    // mientras vale true, la jugada completa (parte 1 + parte 2) se repite
    // en loop
    private boolean isLoopingFullPlay = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        playerName = getIntent().getStringExtra(EXTRA_PLAYER_NAME);
        if (playerName == null) {
            playerName = "Jugador/a";
        }

        videoView = findViewById(R.id.videoView);
        tvPlayerName = findViewById(R.id.tvPlayerName);
        tvRoundCounter = findViewById(R.id.tvRoundCounter);
        tvFeedback = findViewById(R.id.tvFeedback);
        btnOptionA = findViewById(R.id.btnOptionA);
        btnOptionB = findViewById(R.id.btnOptionB);
        btnOptionC = findViewById(R.id.btnOptionC);
        btnOptionD = findViewById(R.id.btnOptionD);


        optionButtons = new Button[]{btnOptionA, btnOptionB, btnOptionC, btnOptionD};

        btnRewind = findViewById(R.id.btnRewind);

        btnRewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRewindClicked();
            }
        });

        btnNextRound = findViewById(R.id.btnNextRound);
        btnNextRound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextRoundClicked();
            }
        });

        mainContent = findViewById(R.id.mainContent);
        videoFrame = findViewById(R.id.videoFrame);
        fullscreenContainer = findViewById(R.id.fullscreenContainer);
        btnExpand = findViewById(R.id.btnExpand);
        btnCollapse = findViewById(R.id.btnCollapse);

        btnExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterFullscreen();
            }
        });
        btnCollapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitFullscreen();
            }
        });

        View topBar = findViewById(R.id.topBar);
        final int topBarBasePadding = topBar.getPaddingTop(); // el padding original (14dp) del XML
        ViewCompat.setOnApplyWindowInsetsListener(topBar, (v, windowInsets) -> {
            Insets bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft() + bars.left, topBarBasePadding + bars.top,
                    v.getPaddingRight() + bars.right, v.getPaddingBottom());
            return windowInsets;
        });

        tvPlayerName.setText(playerName);

        buildQuestions();
        setOptionClickListeners();
        playCurrentRoundPart1();
    }

    // Pantalla Completa
    private void enterFullscreen() {
        if (isFullscreen) return;
        isFullscreen = true;

        videoFrame.removeView(videoView);

        fullscreenContainer.addView(videoView, 0, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mainContent.setVisibility(View.GONE);
        fullscreenContainer.setVisibility(View.VISIBLE);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
    }

  //Volver a Pantalla normal
    private void exitFullscreen() {
        if (!isFullscreen) return;
        isFullscreen = false;

        fullscreenContainer.removeView(videoView);
        videoFrame.addView(videoView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        fullscreenContainer.setVisibility(View.GONE);
        mainContent.setVisibility(View.VISIBLE);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @Override
    public void onBackPressed() {
        if (isFullscreen) {
            exitFullscreen();
        } else {
            super.onBackPressed();
        }
    }

    //Preguntas
    //videos parte 1 y 2
    //String de opciones
    //Opcion correcta(0-3)

    private void buildQuestions() {

        questions = new ArrayList<>();


        questions.add(new Question(
                R.raw.jugada1_parte1, R.raw.jugada1_parte2,
                new String[]{"Remata de zurda y es gol", "Remata de zurda y ataja el arquero",
                        "Engancha, remata de derecha y córner", "Engancha y pierde la pelota"},
                0
        ));

        questions.add(new Question(
                R.raw.jugada2_parte1, R.raw.jugada2_parte2,
                new String[]{"Pierde la pelota", "Remata en el area chica y gol", "Remate de zurda y falla"
                        , "Remate de zurda y ataja el arquero"},
                1
        ));

        questions.add(new Question(
                R.raw.jugada3_parte1, R.raw.jugada3_parte2,
                new String[]{"Remata de zurda y gol", "Le roban la pelota", "Remata de derecha y corner",
                        "Se complica solo y la pierde"},
                3
        ));

        questions.add(new Question(
                R.raw.jugada4_parte1, R.raw.jugada4_parte2,
                new String[]{"No llega a cabecear", "Chilena y golazo", "Cabecea y ataja el arquero",
                        "Cabecea al palo"},
                1
        ));

        questions.add(new Question(
                R.raw.jugada5_parte1, R.raw.jugada5_parte2,
                new String[]{"Tira raso por el piso y gol", "Pase y gol de Gimenez", "Remata y ataja el arquero",
                        "La pasa horrible y pierden la contra"},
                3
        ));

        questions.add(new Question(
                R.raw.jugada6_parte1, R.raw.jugada6_parte2,
                new String[]{"La pica y golazo", "Remata y ataja el arquero",
                        "La tira al segundo palo y golazo", "Juega para atras en final de libertadores"},
                3
        ));

        questions.add(new Question(
                R.raw.jugada7_parte1, R.raw.jugada7_parte2,
                new String[]{"Remata y ataja el arquero", "Pierde la pelota", "Remata y firma" +
                        "la pecheada mas linda", "Remata y afuera"},
                2
        ));

        questions.add(new Question(
                R.raw.jugada8_parte1, R.raw.jugada8_parte2,
                new String[]{"Le pega al palo y desciende riber", "La vuela y desciende riber ",
                        "Travesaño y desciende riber", "Ataja OlaBe"},
                3
        ));
    }


    private void setOptionClickListeners() {
        for (int i = 0; i < optionButtons.length; i++) {
            final int optionIndex = i;
            optionButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOptionSelected(optionIndex);
                }
            });
        }
    }


    // Flujo de Ronda
    //Primero se reproduce la parte 1

    private void playCurrentRoundPart1() {
        waitingForAnswer = false;
        isLoopingFullPlay = false;
        setOptionsVisible(false);
        setOptionsEnabled(false);
        btnRewind.setVisibility(View.INVISIBLE);
        btnRewind.setEnabled(false);
        btnNextRound.setVisibility(View.INVISIBLE);
        btnNextRound.setEnabled(false);
        tvFeedback.setVisibility(View.INVISIBLE);
        tvFeedback.setBackgroundResource(R.drawable.bg_feedback);


        Question current = questions.get(currentRoundIndex);
        tvRoundCounter.setText("Jugada " + (currentRoundIndex + 1) + "/" + questions.size());

        String path = "android.resource://" + getPackageName() + "/" + current.getVideoPart1ResId();
        videoView.setVideoURI(android.net.Uri.parse(path));

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                showOptionsForCurrentRound();
            }
        });
    }

    //Termina y se muestran las opciones
    private void showOptionsForCurrentRound() {
        Question current = questions.get(currentRoundIndex);
        String[] options = current.getOptions();

        for (int i = 0; i < optionButtons.length; i++) {
            optionButtons[i].setText(options[i]);
        }

        setOptionsVisible(true);
        setOptionsEnabled(true);
        btnRewind.setVisibility(View.VISIBLE);
        btnRewind.setEnabled(true);

        waitingForAnswer = true;
    }

    //Boton rebobinar
    private void onRewindClicked() {

        if (!waitingForAnswer) {
            return;
        }
        waitingForAnswer = false;
        setOptionsVisible(false);
        setOptionsEnabled(false);
        btnRewind.setVisibility(View.INVISIBLE);
        btnRewind.setEnabled(false);

        videoView.seekTo(0);
        videoView.start();
    }

    //Seleccion de respuesta
    private void onOptionSelected(int selectedIndex) {

        if (!waitingForAnswer) {
            return;
        }
        waitingForAnswer = false;
        setOptionsEnabled(false); // deshabilita los 4 botones para que no se pueda tocar otra opción
        btnRewind.setVisibility(View.INVISIBLE);
        btnRewind.setEnabled(false);

        Question current = questions.get(currentRoundIndex);
        int correctIndex = current.getCorrectOptionIndex();

        // Respuesta correcta
        boolean isCorrect = selectedIndex == correctIndex;

        //Cambio de color de fondo Correcto/Incorrecto
        optionButtons[selectedIndex].setBackgroundResource(
                isCorrect ? R.drawable.bg_option_correct : R.drawable.bg_option_incorrect);

        //Suma de puntos
        if (isCorrect) {
            score++;
            tvFeedback.setText("¡CORRECTO!");
            tvFeedback.setBackgroundResource(R.drawable.bg_feedback_correct);
        } else {
            tvFeedback.setText("INCORRECTO");
            tvFeedback.setBackgroundResource(R.drawable.bg_feedback_incorrect);
        }
        tvFeedback.setVisibility(View.VISIBLE);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                playFinalOutcomeOnce();
            }
        }, FEEDBACK_DELAY_MS);
    }

    private void playFinalOutcomeOnce() {
        Question current = questions.get(currentRoundIndex);
        String path = "android.resource://" + getPackageName() + "/" + current.getVideoPart2ResId();
        videoView.setVideoURI(android.net.Uri.parse(path));

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                startFullPlayLoop();
            }
        });
    }

    // A partir de acá la jugada se repite para que el usuario la pueda analizar tranquilo
    // hasta que clickee "siguiente ronda"
    private void startFullPlayLoop() {
        isLoopingFullPlay = true;
        btnNextRound.setVisibility(View.VISIBLE);
        btnNextRound.setEnabled(true);

        playPart1InLoop();
    }

    private void playPart1InLoop() {
        Question current = questions.get(currentRoundIndex);
        String path = "android.resource://" + getPackageName() + "/" + current.getVideoPart1ResId();
        videoView.setVideoURI(android.net.Uri.parse(path));

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playPart2InLoop();
            }
        });
    }

    private void playPart2InLoop() {
        Question current = questions.get(currentRoundIndex);
        String path = "android.resource://" + getPackageName() + "/" + current.getVideoPart2ResId();
        videoView.setVideoURI(android.net.Uri.parse(path));

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (isLoopingFullPlay) {
                    playPart1InLoop();
                }
            }
        });
    }
    private void onNextRoundClicked() {
        if (!isLoopingFullPlay) {
            return;
        }
        isLoopingFullPlay = false;
        btnNextRound.setVisibility(View.INVISIBLE);
        btnNextRound.setEnabled(false);

        goToNextRoundOrFinish();
    }

    //Siguiente ronda y terminar juego
    private void goToNextRoundOrFinish() {
        currentRoundIndex++;

        if (currentRoundIndex < questions.size()) {
            playCurrentRoundPart1();
        } else {
            finishGame();
        }
    }

    //Se manda a ResultActivity, se muestran el nombre, el puntaje y las rondas jugadas
    private void finishGame() {
        Intent intent = new Intent(GameActivity.this, ResultActivity.class);
        intent.putExtra(EXTRA_PLAYER_NAME, playerName);
        intent.putExtra(EXTRA_SCORE, score);
        intent.putExtra(EXTRA_TOTAL_ROUNDS, questions.size());
        startActivity(intent);

        finish();
    }


    //Muestra u oculta los 4 botones de opciones al mismo tiempo
    private void setOptionsVisible(boolean visible) {

        int visibility = visible ? View.VISIBLE : View.INVISIBLE;
        for (Button b : optionButtons) {
            b.setVisibility(visibility);
        }
    }

    //Habilita o deshabilita que se puedan tocar los 4 botones
    private void setOptionsEnabled(boolean enabled) {
        for (Button b : optionButtons) {
            b.setEnabled(enabled);
        }
    }

    //Flecha de volver
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
