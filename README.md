## Práctica 1

<div align="justify">
Esta primera práctica se centró en el uso de git, una herramienta indispensable al momento de desarrollar algún proyecto, sobre todo si es en equipo, por lo que se hizo énfasis  en la resolución de conflictos en el momento en que dos personas hacen modificaciones similares, todo esto sirvió para recordarnos como es el flujo en git y tener una pequeña noción acerca de los conflictos que se podrían presentar en proyectos posteriores. Adicional a esto se realizó una actividad en Java que consistía en crear las operaciones CRUD reflejadas en una lista de tareas, este tipo de operaciones son indispensables al momento de desarrollar software, debido a esto es importante conocer su implementación.


#### Funcionalidades
* **Crear:** Permite añadir tareas pendientes
* **Leer:** Permite mostrar la lista de tareas organizada en *Pendiente*, *Proceso* y *Finalizada*
* **Actualizar:** Cambia el estado entre *Pendiente*, *Proceso* y *Finalizada*
* **Eliminar:** Elimina tareas de cualquier sección

#### Requisitos
* Java 8 o superior
* Android Studio


### Aceleración de hardware

<div align="justify">
Tuve problemas en cuanto la aceleración de software en mi laptop, ya que no tenía activada la virtualización por lo que fue necesario activarla desde la BIOS, sin embargo fue lo único que se tuvo que modificar para poder trabajar con el entorno de Android Studio.
</div>

### Elección de ArrayList
<div align="justify">
Se escogió el uso de ArrayList para la implementación de la lista de tareas debido a su “flexibilidad”, es decir, dado que tienen tamaño dinámico no me tuve que preocupar por cuidar los espacios para generar nuevas tareas, además su manipulación no tuvo complicaciones.
</div>

### Servidor remoto
<div align="justify">
Si estas listas de tareas se tuvieran en un servidor remoto se tendrían que considerar las posibles excepciones que se generan como un fallo en la red o un problema con la base de datos, ya que esto queda un poco lejos de nuestro control, solo quedaría estar preparados contra posibles fallos.
</div>
