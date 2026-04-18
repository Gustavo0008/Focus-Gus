/*
@author <a href=gustavo008@ciecias.unam.mx> Gustavo Angel Ortiz Vasquez - @Gustavo0008 </a>
 */

package com.ciencias.focus_gus;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.ColorStateList;

import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;

import android.view.View;

import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.ciencias.focus_gus.model.Session;
import com.ciencias.focus_gus.model.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {


    // Estados del temporizador y sesion.
    enum TimerState {IDLE, RUNNING, PAUSED}

    enum SessionMode {FOCUS, BREAK, REST}

    // Constantes de tiempo en milisegundos.
    private static final long FOCUS_DURATION_MS = 25 * 60 * 1000L;
    private static final long BREAK_DURATION_MS = 5 * 60 * 1000L;
    private static final long REST_DURATION_MS = 15 * 60 * 1000L;
    private static final int SESSIONS_BEFORE_REST = 4;

    // Elementos para el registro de una sesión.
    private Session newSession;
    private SessionManager sessionManager;
    private boolean currentSessionIsCompleted = true;



    // Elementos de la IU.

    private TextView tvAppTittle;
    private ImageButton btnStats, btnSettings, btnReset, btnSkip;
    private ChipGroup chipGroupMode;
    private Chip chipFocus, chipBreak, chipRest;
    private MaterialButton btnStartStop;
    private LinearLayout sessionDotsContainer;

    private TextView tvTimerDisplay;
    private TextView tvSessionStatus;
    private TextView tvSessionCount;
    private TextView tvMotivation;




    // Elementos para el funcionamiento del temporizador.
    private CountDownTimer countDownTimer;
    private TimerState timerState = TimerState.IDLE;
    private SessionMode currentMode = SessionMode.FOCUS;
    private long timeLeftMillis = FOCUS_DURATION_MS;
    private int focusSessionsCompleted = 0;

    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();


    /**
     *
     * Inicializa la actividad junto con todos sus componentes
     * @param savedInstanceState Estado guardado de la aplicación
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Leer preferencias guardadas antes de dibujar la pantalla
        android.content.SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);

        // Leemos el tema
        String themeValue = prefs.getString(getString(R.string.theme_preference_key), "system");

        // Aplicamos el tema
        if ("light".equals(themeValue)) {
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
        } else if ("dark".equals(themeValue)) {
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        EdgeToEdge.enable(this);
        // Inflamos nuestra vista.
        setContentView(R.layout.activity_main);

        // Inicializamos los elementos de la IU.
        bindViews();
        // Llamamos al controlador.
        sessionManager = new SessionManager(this);
        // Asignamos los escuchas.
        setupClickListeners();
        // Actualizamos la IU.
        updateTimerDisplay(timeLeftMillis);
    }

    /**
     * Destruye la actividad.
     * Se cancela el temporizador para evitar fugas de memoria y liberar recursos
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimer();
    }


    /**
     * Vincula las variables con los identificadores definidos en la interfaz
     */
    private void bindViews() {
        btnStats = findViewById(R.id.btnStats);
        btnSettings = findViewById(R.id.btnSettings);
        chipGroupMode = findViewById(R.id.chipGroupMode);
        chipFocus = findViewById(R.id.chipFocus);
        chipBreak = findViewById(R.id.chipBreak);
        chipRest = findViewById(R.id.chipRest);
        tvTimerDisplay = findViewById(R.id.tvTimerDisplay);
        btnStartStop = findViewById(R.id.btnStartStop);
        sessionDotsContainer = findViewById(R.id.sessionDotsContainer);
        btnReset = findViewById(R.id.btnReset);
        btnSkip = findViewById(R.id.btnSkip);
        tvSessionStatus = findViewById(R.id.tvSessionStatus);
        tvSessionCount = findViewById(R.id.tvSessionCount);

    }

    /**
     * Asigna los clics a los botones definidos en la interfaz
     */
    private void setupClickListeners() {
        // Asignamos un escucha al boton que controla nuestro temporizador.
        btnStartStop.setOnClickListener(v -> {

            // Llamamos a los metodos correspondientes segun el estado del temporizador.
            if (timerState == TimerState.RUNNING) pauseTimer();
            else startTimer();
        });

        // Botones de reiniciar y saltar
        btnReset.setOnClickListener(v -> resetTimer());
        btnSkip.setOnClickListener(v -> skipToNextSession());

        // Boton de historial
        btnStats.setOnClickListener(v -> {
            // Cambiar de la pantalla original al historial
            android.content.Intent intent = new android.content.Intent(MainActivity.this, com.ciencias.focus_gus.view.SessionHistoryActivity.class);
            startActivity(intent);
        });

        // Botón de Ajustes
        btnSettings.setOnClickListener(v -> {
            // Cambiar de la pantalla original a la de ajustes
            android.content.Intent intent = new android.content.Intent(MainActivity.this, com.ciencias.focus_gus.view.PreferencesActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Inicia el temporizador con el tiempo restante actual.
     * Se actualiza la interfaz y se cambia de estado
     */
    private void startTimer() {
        // Actualizamos el estado del temporizador.
        timerState = TimerState.RUNNING;
        // Asignamos una texto mas adecuado al boton que controla nuestro temporizador.
        btnStartStop.setText(R.string.btn_pause);

        // Registramos una nueva sesión.
        newSession = new Session();
        if (currentMode == SessionMode.FOCUS) {
            newSession.setType(getString(R.string.mode_focus));
            newSession.setDuration(25);
        } else if (currentMode == SessionMode.BREAK) {
            newSession.setType(getString(R.string.mode_break));
            newSession.setDuration(5);
        } else {
            newSession.setType(getString(R.string.mode_long_break));
            newSession.setDuration(15);
        }

        newSession.setDate(new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()).format(new Date()));
        newSession.setDate(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));


        // Creamos e inicializamos un contador.
        countDownTimer = new CountDownTimer(timeLeftMillis, 1000) {

            /**
             * Actualiza la vista del temporizador
             * @param millisUntilFinished Milisegundos restantes
             */
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftMillis = millisUntilFinished;
                updateTimerDisplay(millisUntilFinished);
            }

            /**
             * Notifica que el tiempo se ha terminado
             */
            @Override
            public void onFinish() {
                onSessionFinished();
            }
        }.start();
    }

    /**
     * Se detiene el temporizador guardando el tiempo restante.
     * Se cambia de estado y se actualiza el boton
     */
    private void pauseTimer() {
        // Detenemos nuestro contador.
        if (countDownTimer != null) countDownTimer.cancel();
        // Actualizamos el estado de nuestro temporizador.
        timerState = TimerState.PAUSED;
        // Actualizamos el texto del boton que controla el temporizador.
        btnStartStop.setText(R.string.btn_resume);
    }

    /**
     * Gestiona el cambio en el modo de la sesión.
     * Se generan los puntos dependiendo del número de sesiones.
     * Se notifica el usuario el fin de una sesión
     */
    private void onSessionFinished() {
        // Actualizamos el estado de nuestro temporizador.
        timerState = TimerState.IDLE;

        // Actualizamos el estado de la sesion por su sucesora.
        if (currentMode == SessionMode.FOCUS) {
            focusSessionsCompleted++;
            if (focusSessionsCompleted >= SESSIONS_BEFORE_REST) {
                focusSessionsCompleted = 0;
                currentMode = SessionMode.REST;
            } else {
                currentMode = SessionMode.BREAK;
            }
        } else {
            currentMode = SessionMode.FOCUS;
        }


        currentSessionIsCompleted = true;
        newSession.setCompleted(currentSessionIsCompleted);

        // Guardamos en el hilo de fondo
        databaseExecutor.execute(() -> {
            sessionManager.saveSession(newSession);
        });

        // Actualizamos los puntos al cambiar de estado
        updateSessionDots();

        // Limpieza de texto
        Toast.makeText(this, "Sesión guardada en el historial", Toast.LENGTH_SHORT).show();

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        }

        // Actualizamos el temporizador y el texto del boton que lo controla.
        resetModeTime();
        btnStartStop.setText(R.string.btn_start);
    }

    /**
    * Se dibujan la cantidad de sesiones de enfoque terminadas mediante puntos en la interfaz
    */
    private void updateSessionDots() {
        // Borramos todos los puntos
        sessionDotsContainer.removeAllViews();

        // Insertamos los puntos necesarios
        for (int i = 0; i < focusSessionsCompleted; i++) {
            addDot();
        }
    }

    /**
     * Se configura la vista de un punto y se agrega al contenedor
     */
    private void addDot() {
        // Creamos la vista del punto.
        View dot = new View(this);
        // Definimos su tamano (10dp convertido a pixeles).
        int dotSize = (int) (10 * getResources().getDisplayMetrics().density);
        // Creamos un contenedor para el punto.
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dotSize, dotSize);
        // Agregamos un margen de separacion a la derecha (8dp).
        params.setMarginEnd((int) (8 * getResources().getDisplayMetrics().density));
        // Aplicamos el layout a la vista.
        dot.setLayoutParams(params);
        // Asignamos la figura de nuestro punto (drawable).
        dot.setBackground(getDrawable(R.drawable.dot_session_completed));
        // Agregamos el punto creado al contenedor.
        sessionDotsContainer.addView(dot);
    }

    /**
     * Restaura el tiempo del temporizador actual
     */
    private void resetModeTime() {
        // Reasignamos la duracion de la sesion segun el estado actual.
        if (currentMode == SessionMode.FOCUS) timeLeftMillis = FOCUS_DURATION_MS;
        else if (currentMode == SessionMode.BREAK) timeLeftMillis = BREAK_DURATION_MS;
        else timeLeftMillis = REST_DURATION_MS;
        // Actualizamos la IU.
        updateTimerDisplay(timeLeftMillis);
    }

    /**
     * Detiene de manera segura el temporizador
     */
    private void cancelTimer() {
        // Si el temporizador esta activo:
        if (countDownTimer != null) {
            // Detemos el tiempo.
            countDownTimer.cancel();
            // Anulamos el temporizador.
            countDownTimer = null;
        }
    }

    /**
     * Resetea el tiempo del estado actual y actualiza la interfaz
     */
    private void resetTimer() {

        if (timerState == TimerState.RUNNING && newSession != null) {
            newSession.setCompleted(false);
            databaseExecutor.execute(() -> {
                sessionManager.saveSession(newSession);
            });
            Toast.makeText(this, "Sesión interrumpida guardada.", Toast.LENGTH_SHORT).show();
        }

        cancelTimer();
        timerState = TimerState.IDLE;
        resetModeTime();
        btnStartStop.setText(R.string.btn_start);
    }

    /**
     * Se fuerza la finalización de una sesión y salta a la siguiente
     */
    private void skipToNextSession() {

        // Guardamos la sesión como fallida
        if (timerState == TimerState.RUNNING && newSession != null) {
            newSession.setCompleted(false);
            databaseExecutor.execute(() -> {
                sessionManager.saveSession(newSession);
            });
        }

        cancelTimer();

        // Avanza el ciclo de los puntos
        if (currentMode == SessionMode.FOCUS) {
            // Sumamos la sesión al contador visual
            focusSessionsCompleted++;

            // Evaluamos si toca descanso largo
            if (focusSessionsCompleted >= SESSIONS_BEFORE_REST) {
                focusSessionsCompleted = 0; // Limpiamos los puntos
                currentMode = SessionMode.REST;
            } else {
                currentMode = SessionMode.BREAK;
            }
        } else {
            // Regresamos a enfocarnos
            currentMode = SessionMode.FOCUS;
        }

        timerState = TimerState.IDLE;
        resetModeTime();

        // Dibujamos los puntos en la pantalla
        updateSessionDots();

        btnStartStop.setText(R.string.btn_start);

    }

    /**
     * Convierte el tiempo de milisegundos a un formato MM:SS
     * @param millis El tiempo restante en milisegundos
     */
    private void updateTimerDisplay(long millis) {
        // Resaltamos el chip correspondiente al estado actual del temporizador.
        selectChipForMode(currentMode);
        int minutes = (int) (millis / 1000) / 60;
        int seconds = (int) (millis / 1000) % 60;
        // Actualizamos el texto del temporizador.
        tvTimerDisplay.setText(String.format("%02d:%02d", minutes, seconds));
    }


    /**
     * Marca visualmente el modo de la sesión
     * @param mode Modo de la sesión actual
     */
    private void selectChipForMode(SessionMode mode) {
        // El identificador del chip a seleccionar.
        int chipId;
        switch (mode) {
            case BREAK:
                // Asignamos el elemento en el layout (el chip).
                chipId = R.id.chipBreak;
                // Resaltamos el chip seleccionado.
                highlightChip(chipBreak);
                break;
            case REST:
                chipId = R.id.chipRest;
                highlightChip(chipRest);
                break;
            default:
                chipId = R.id.chipFocus;
                highlightChip(chipFocus);
                break;
        }
        chipGroupMode.check(chipId);
        // La agrupacion sabe que chip hemos seleccionado.
    }

    /**
     * Personalización de los bordes
     * @param activeChip El chip del estado actual
     */
    private void highlightChip(Chip activeChip) {
        // Obtenemos la densidad de pantalla necesaria para construir el borde de nuestros chips.
        float density = getResources().getDisplayMetrics().density;
        // Enlistamos los chips disponibles para manipularlos facilmente.
        Chip[] allChips = {chipFocus, chipBreak, chipRest};

        // Quitamos el borde de todos los chips.
        for (Chip chip : allChips) {
            chip.setChipStrokeWidth(0);
        }

        // Resaltamos el chip activo modificando el grosor del borde.
        activeChip.setChipStrokeWidth(2 * density);
        // Recuperamos el color para resaltar el borde del chip de los recursos de nuestra app.
        int colorAccent = ContextCompat.getColor(this, R.color.color_border_accent);
        // Asignamos el color del borde para resaltar al chip activo.
        activeChip.setChipStrokeColor(ColorStateList.valueOf(colorAccent));
    }
}