## El motivo de esta práctica fue conocer el desarrollo básico en Android, se experimenta por primera vez con la interfaz y con la lógica detrás de ella. Todo esto mediante el desarrollo de un temporizador para llevar el control de sesiones de pomodoro.

### 1. ¿Cuál fue el mayor reto al gestionar el CountDownTimer y cómo evitaste que se crearan múltiples instancias al presionar el botón repetidamente?

Se creo una lista de estados para tener mejor control del temporizador, al momento de crearlo se verificaba que no se encontrara corriendo.

### 2. ¿Por qué es preferible usar un LinearLayout con addView para los puntos de progreso en lugar de declarar 4 ImageViews estáticos en el XML?

Es preferible usar un LinearLayout para tener un mejor control sobre esto, se podrá modificar fácilmente en un futuro.

### 3. Si quisiéramos añadir una función para que el usuario personalice sus propios tiempos de enfoque, ¿qué parte de la lógica actual tendría que cambiar y cómo lo abordarías?

Convertiriamos las constantes que tenemos definidas a unas variables que se puedan modificar por el usuario.

### 4. ¿Cómo harían para que el tiempo del temporizador se mantenga si el usuario minimiza la app?

Llevar un conteo por fuera que me permita seguir llevando la cuenta pero con toda la interfaz detenida.
