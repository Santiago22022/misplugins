public class ControlDeTiempoDeJuego {

    public static void main(String[] args) throws InterruptedException {

        // --- 1. La Configuración (Nuestra Regla) ---
        int tiempoLimiteEnSegundos = 10; // Límite de 10 segundos para este ejemplo
        System.out.println("Regla del servidor: Límite de tiempo de " + tiempoLimiteEnSegundos + " segundos.");

        // --- 2. El Jugador Entra ---
        long tiempoDeConexion = System.currentTimeMillis();
        System.out.println("Un jugador se ha conectado. El cronómetro empieza a correr...");

        // --- 3. Primera Revisión (después de 5 segundos) ---
        Thread.sleep(5000); // Simulamos que pasan 5 segundos

        System.out.println("\n--- HACIENDO LA PRIMERA REVISION ---");

        // Calculamos cuánto tiempo ha pasado
        long tiempoTranscurridoSegundos = (System.currentTimeMillis() - tiempoDeConexion) / 1000;
        System.out.println("El jugador lleva conectados: " + tiempoTranscurridoSegundos + " segundos.");

        // Hacemos la pregunta clave con el operador relacional '>'
        boolean haExcedidoElTiempo = (tiempoTranscurridoSegundos > tiempoLimiteEnSegundos);
        System.out.println("¿Ha excedido el límite de tiempo? " + haExcedidoElTiempo); // Imprimirá 'false'

        // --- 4. Segunda Revisión (después de 6 segundos más) ---
        Thread.sleep(6000); // Simulamos que pasan 6 segundos más (11 en total)

        System.out.println("\n--- HACIENDO LA SEGUNDA REVISION ---");

        // Recalculamos el tiempo transcurrido
        tiempoTranscurridoSegundos = (System.currentTimeMillis() - tiempoDeConexion) / 1000;
        System.out.println("El jugador lleva conectados: " + tiempoTranscurridoSegundos + " segundos.");

        // Hacemos la misma pregunta clave de nuevo
        haExcedidoElTiempo = (tiempoTranscurridoSegundos > tiempoLimiteEnSegundos);
        System.out.println("¿Ha excedido el límite de tiempo? " + haExcedidoElTiempo); // ¡Ahora imprimirá 'true'!

        // Aquí podríamos añadir la lógica para echar al jugador
        // que veremos en módulos futuros.
    }
}
