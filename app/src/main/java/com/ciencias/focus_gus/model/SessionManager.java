package com.ciencias.focus_gus.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona el ciclo de vida de las tareas sugeridas dentro de la aplicación.
 * Implementa las operaciones básicas de persistencia en memoria (CRUD).
 */
public class SessionManager extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "FocusGus.db";
    private static final int DATABASE_VERSION = 1;

    // Definición de los NOMBRES de las columnas de la tabla.
    public static final String TABLE_SESSIONS = "sessions";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_START = "startTime";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_COMPLETED = "completed";


    /**
     * Inicializa la conexion con la base de datos.
     * @param context Contexto de la aplicacion
     */
    public SessionManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Construye la estructura de la tabla de las sesiones.
     * @param db Instancia de la base de datos
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_SESSIONS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TYPE + " TEXT, "
                + COLUMN_DATE + " TEXT, "
                + COLUMN_START + " TEXT, "
                + COLUMN_DURATION + " INTEGER, "
                + COLUMN_COMPLETED + " INTEGER"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    /**
     * Inserta un nuevo registro.
     * @param session La sesion con los datos a guardar
     */
    public void saveSession(Session session) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Mapeamos los atributos del objeto Session a las columnas de la DB
        values.put(COLUMN_TYPE, session.getType());
        values.put(COLUMN_DATE, session.getDate());
        values.put(COLUMN_START, session.getStartTime());
        values.put(COLUMN_DURATION, session.getDuration());

        // Convertimos el boolean 'completed' a un entero (1 o 0) para SQLite
        values.put(COLUMN_COMPLETED, session.isCompleted() ? 1 : 0);

        // Insertamos la fila
        db.insert(TABLE_SESSIONS, null, values);

    }


    /**
     * Metodo auxiliar para agilizar la lectura de datos.
     * Extrae los valores de la fila actual y los convierte en un objeto Session.
     */
    private Session extractSessionFromCursor(Cursor cursor) {
        Session session = new Session();
        session.setType(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)));
        session.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)));
        session.setStartTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START)));
        session.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DURATION)));
        session.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COMPLETED)) == 1);
        return session;
    }

    /**
     * Recupera el historial de todas las sesiones.
     * @return Lista de sesiones
     */
    public List<Session> getAllSessions() {
        List<Session> sessionList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        // Consultamos toda la tabla, ordenando por ID descendente (dejando la sesión más reciente primero)
        try {
            cursor = db.query(TABLE_SESSIONS, null, null, null, null, null, COLUMN_ID + " DESC");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    sessionList.add(extractSessionFromCursor(cursor));
                } while (cursor.moveToNext());
            }
        } finally {
            // Se ejecuta SIEMPRE, previniendo fugas de memoria
            if (cursor != null) {
                cursor.close();
            }
        }
        return sessionList;
    }



    /**
     * Recupera las sesiones del dia de hoy.
     * @return Lista de sesiones que coinciden con el dia actual
     */
    public List<Session> getSessionsToday() {
        List<Session> sessionList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        // Se define el formato y obtenemos la fecha
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        String today = sdf.format(new java.util.Date());

        String selection = COLUMN_DATE + " = ?";
        String[] selectionArgs = {today};

        // Consultamos toda la tabla, ordenando por ID descendente (dejando la sesión más reciente primero)
        try {
            cursor = db.query(TABLE_SESSIONS, null, selection, selectionArgs, null, null, COLUMN_ID + " DESC");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    sessionList.add(extractSessionFromCursor(cursor));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return sessionList;
    }

    /**
     * Muestra las sesiones realizadas en la semana.
     * @return Lista de sesiones de la ultima semana.
     */
    public List<Session> getSessionsThisWeek() {
        List<Session> sessionList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        // Calculamos el inicio y fin de la semana actual

        // Buscamos cuando inicia la semana
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());

        // Se le da formato a la fecha
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());

        // Obtenemos el inico de la semana
        String startOfWeek = sdf.format(calendar.getTime());

        // Completamos la semana agregando 6 dias
        calendar.add(java.util.Calendar.DAY_OF_WEEK, 6);

        // Se extrae el final de la semana
        String endOfWeek = sdf.format(calendar.getTime());

        String selection = COLUMN_DATE + " BETWEEN ? AND ?";
        String[] selectionArgs = { startOfWeek, endOfWeek };

        // Consultamos toda la tabla, ordenando por ID descendente (dejando la sesión más reciente primero)
            try {
                cursor = db.query(TABLE_SESSIONS, null, selection, selectionArgs, null, null, COLUMN_ID + " DESC");
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        sessionList.add(extractSessionFromCursor(cursor));
                    } while (cursor.moveToNext());
                }
            } finally {
                if (cursor != null) cursor.close();
            }
            return sessionList;
        }


    /**
     * Maneja las actualizaciones de la base de datos
     * @param db Instancia de la base de datos.
     * @param oldVersion Número de la version anterior.
     * @param newVersion Número de la nueva version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 1. Eliminamos la tabla si ya existe
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSIONS);

        // 2. Volvemos a crearla llamando al metodo onCreate
        onCreate(db);

        Log.d("SQLite", "Base de datos actualizada de la versión " + oldVersion + " a la " + newVersion);
    }

    /**
     * Elimina todos los registros de la tabla de sesiones.
     * Se utiliza para limpiar el historial del usuario
     */
    public void deleteAllSessions() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Borramos todas las filas de la tabla
        db.delete(TABLE_SESSIONS, null, null);

    }
}