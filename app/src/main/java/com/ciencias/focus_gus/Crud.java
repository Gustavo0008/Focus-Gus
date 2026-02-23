package com.ciencias.focus_gus;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class Crud {

    // Lista donde se guardan todas las tareas
    ArrayList<Tarea> listaTareas = new ArrayList<Tarea>();
    Scanner sc = new Scanner(System.in);

    public Crud() {

    }

    // Funcion auxiliar para obtener las tareas por estado
    private List<Tarea> obtenerTareas(String estado) {
        List<Tarea> filtradas = new ArrayList<>();

        for (Tarea t : listaTareas) {
            if (t.getEstado().equalsIgnoreCase(estado)) {
                filtradas.add(t);
            }
        }
        return filtradas;
    }

    // Obtener todas las tareas organizadas por estado

    public void mostrarTareas() {

        // Creacion de los posibles encabezados e indentificadores
        String[] estados = { "pendiente", "proceso", "finalizada" };
        String[] titulos = { "TAREAS PENDIENTES:", "TAREAS EN PROCESO:", "TAREAS FINALIZADAS:" };

        // Separamos las tareas
        for (int i = 0; i < estados.length; i++) {
            System.out.println("---------------------------------------------");
            System.out.println(titulos[i]);
            List<Tarea> listaIndividual = obtenerTareas(estados[i]);

            // Verificar que no se encuentre vacia la lista
            if (listaIndividual.isEmpty()) {
                System.out.println("No hay tareas disponibles");

            } else {

                // Imprimir solo las tareas que se obtienen con un cierto estado
                for (int j = 0; j < listaIndividual.size(); j++) {
                    System.out.println((j + 1) + ". " + listaIndividual.get(j).getNombreT());
                }
            }
        }
    }

    // Creación de tareas, se asigna automaticamente como una tarea pendiente

    public void crearTarea() {

        System.out.println("Ingresa la tarea que deseas agregar");
        String ingresarT = sc.nextLine();

        Tarea t = new Tarea("pendiente", ingresarT);
        listaTareas.add(t);

        System.out.println("Tarea guardada con éxito");

    }

    // Eliminación de tareas con cualquier estado

    public void eliminarTarea() {

        System.out.println("¿De qué sección deseas borrar la tarea?");
        System.out.println("[ 1 ] Tareas pendientes\n[ 2 ] Tareas en proceso\n[ 3 ] Tareas terminadas");
        int seccion = sc.nextInt();

        // Obtener la lista dependiendo del estado seleccionado
        String estado = (seccion == 1) ? "pendiente" : (seccion == 2) ? "proceso" : "finalizada";
        List<Tarea> listaIndividual = obtenerTareas(estado);

        // Verificar que no este vacia la lista
        if (listaIndividual.isEmpty()) {
            System.out.println("No hay tareas disponibles");
            return;
        }

        // Mostrar las tareas con ese estado
        for (int i = 0; i < listaIndividual.size(); i++) {
            System.out.println((i + 1) + ". " + listaIndividual.get(i).getNombreT());
        }

        System.out.println("Ingresa el número de la tarea a eliminar:");
        int indice = sc.nextInt() - 1;

        // Eliminar tarea por el indice que tiene en la lista
        if (indice >= 0 && indice < listaIndividual.size()) {
            Tarea tareaEliminar = listaIndividual.get(indice);
            listaTareas.remove(tareaEliminar);
            System.out.println("Tarea eliminada con éxito.");
        } else {
            System.out.println("Ingresa un número válido");
        }
    }

    // Actualizar estado de cualquier tarea
    public void moverTarea() {

        // Seleccionar lista de origen
        System.out.println("¿Desde qué sección deseas mover la tarea?");
        System.out.println("[ 1 ] Pendientes\n[ 2 ] En proceso\n[ 3 ] Terminadas");
        int origen = sc.nextInt();
        String estadoOrigen = (origen == 1) ? "pendiente" : (origen == 2) ? "proceso" : "finalizada";

        // Obtener una lista con el estado solicitado
        List<Tarea> listaIndividual = obtenerTareas(estadoOrigen);
        if (listaIndividual.isEmpty()) {
            System.out.println("No hay tareas disponibles");
            return;
        }

        // Mostrar y elegir tarea
        for (int i = 0; i < listaIndividual.size(); i++) {
            System.out.println((i + 1) + ". " + listaIndividual.get(i).getNombreT());
        }
        System.out.print("Elige el número de tarea: ");
        int indice = sc.nextInt() - 1;

        // Elegir lista de destino
        if (indice >= 0 && indice < listaIndividual.size()) {
            System.out.println("¿A qué sección la quieres mover?");
            System.out.println("[ 1 ] Pendiente\n[ 2 ] Proceso\n[ 3 ] Finalizada");
            int destino = sc.nextInt();

            // Actualizar el estado de la tarea
            if (destino < 4) {
                String nuevoEstado = (destino == 1) ? "pendiente" : (destino == 2) ? "proceso" : "finalizada";
                listaIndividual.get(indice).setEstado(nuevoEstado);
                System.out.println("Tarea movida con éxito ");

            } else {
                System.out.println("Ingresa un número válido");
            }

        } else {
            System.out.println("Elige una opción válida");
        }
    }

}