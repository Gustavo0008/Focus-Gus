package com.ciencias.focus_gus;

import java.util.Scanner;
import java.util.InputMismatchException;

public class TaskManager {

    public static void main(String[] args) {

        boolean salir = false;
        Scanner sc = new Scanner(System.in);
        Crud lista = new Crud();

        while (!salir) {

            try {

                System.out.println("---------------------------------------------");
                System.out.println("¿Qué deseas realizar?\n");
                System.out.println(
                        "[ 1 ]   Ver tareas\n[ 2 ]   Crear una nueva tarea\n[ 3 ]   Mover una tarea\n[ 4 ]   Eliminar una tarea\n[ 5 ]   Salir");

                int eleccion = sc.nextInt();

                switch (eleccion) {
                    case 1:

                        lista.mostrarTareas();

                        break;

                    case 2:

                        lista.crearTarea();

                        break;

                    case 3:

                        lista.moverTarea();

                        break;

                    case 4:

                        lista.eliminarTarea();

                        break;

                    case 5:

                        salir = true;

                        break;

                    default:

                        System.out.println("Elige una opción válida");
                        break;
                }

            } catch (InputMismatchException noValido) {
                System.out.println("Error: Ingresa solo números");
                sc.next();
            }

        }

    }

}