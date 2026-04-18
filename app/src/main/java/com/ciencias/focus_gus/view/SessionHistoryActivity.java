package com.ciencias.focus_gus.view;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.ciencias.focus_gus.R;
import com.ciencias.focus_gus.model.Session;
import com.ciencias.focus_gus.model.SessionManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

/**
 * Actividad que visualiza el historial cronológico de las sesiones de enfoque y descanso.
 * Se utiliza como práctica para el manejo de RecyclerView, adaptadores y filtrado de datos.
 */
public class SessionHistoryActivity extends AppCompatActivity {

    // Componentes de la Interfaz de Usuario.
    private Toolbar toolbar;
    private TextView tvResultCount;
    private ConstraintLayout layoutEmpty;
    private RecyclerView recyclerView;

    private ChipGroup chipGroupFilter;
    private Chip chipAll, chipToday, chipWeek;

    // Lógica y Datos.
    private SessionHistoryAdapter adapter;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_session);

        bindViews();
        setupToolbar();
        setupRecyclerView();
        updateHistoryDisplay();
    }

    /**
     * Vincula las variables con los componentes del XML.
     */
    private void bindViews() {
        toolbar = findViewById(R.id.history_toolbar);
        tvResultCount = findViewById(R.id.tvResultCount);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        recyclerView = findViewById(R.id.recyclerViewHistory);

        // Vincula Chips mediante findViewById

        chipGroupFilter = findViewById(R.id.chipGroupFilter);
        chipAll = findViewById(R.id.chipAll);
        chipToday = findViewById(R.id.chipToday);
        chipWeek = findViewById(R.id.chipWeek);

        sessionManager = new SessionManager(this);
    }


    /**
     * Configura la Toolbar como ActionBar de la actividad.
     * Habilita el botón de retroceso y asigna el título
     * desde los recursos de cadena para soporte multi-idioma.
     */
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_history);
        }
    }

    /**
     * Inicializa el RecyclerView con su LayoutManager y Adaptador.
     * Vincula la lista de sesiones obtenida del SessionManager con la
     * interfaz visual mediante el SessionHistoryAdapter.
     */
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Obtenemos los datos iniciales.
        List<Session> history = sessionManager.getAllSessions();

        // Inicializamos el adaptador.
        adapter = new SessionHistoryAdapter(history, getResources());
        recyclerView.setAdapter(adapter);
    }

    /**
     * Gestiona la visibilidad de la UI y actualiza el contador.
     */
    private void updateHistoryDisplay() {
        // Obtenemos el filtro quiere el usuario
        int checkedChipId = chipGroupFilter.getCheckedChipId();

        //  Consultar la base de datos
        java.util.concurrent.Executors.newSingleThreadExecutor().execute(() -> {

            // Guardar resultados
            List<Session> sessions;

            // Le pedimos al modelo la información
            if (checkedChipId == R.id.chipToday) {
                sessions = sessionManager.getSessionsToday();
            } else if (checkedChipId == R.id.chipWeek) {
                sessions = sessionManager.getSessionsThisWeek();
            } else {
                sessions = sessionManager.getAllSessions();
            }

            // Volvemos al hilo principal para actualizar la pantalla.
            runOnUiThread(() -> {

                // Evaluamos si hay datos para mostrar u ocultar la lista
                boolean isEmpty = (sessions == null || sessions.isEmpty());
                layoutEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
                recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);

                // Inyectamos los nuevos datos al adaptador para que la lista se redibuje
                if (adapter != null) {
                    adapter.updateData(sessions);
                }

                // Actualizamos el texto con los Plurales
                int size = sessions != null ? sessions.size() : 0;
                String countText = getResources().getQuantityString(R.plurals.session_count, size, size);
                tvResultCount.setText(countText);

            });
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
