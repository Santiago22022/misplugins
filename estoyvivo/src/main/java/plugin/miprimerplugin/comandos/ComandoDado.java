package plugin.miprimerplugin.comandos; // O tu ruta de paquete

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public abstract class ComandoDado implements CommandExecutor{
    // El método onCommand que antes tenías en la clase principal, ahora vive aquí.
    @Override
    public boolean onComand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String [] args)  {
        return false;

        // 1. Definimos una variable para las caras del dado.
        //    Por defecto, un dado normal de 6 caras.
        int caras = 6;

        // 2. Comprobamos si el jugador nos dio un argumento (un número de caras).
        //    args.length > 0 significa que la lista de "palabras extra" no está vacía.
        if (args.length > 0) {

            // 3. Usamos un 'try-catch'. Esto es una red de seguridad.
            //    Intentamos (try) convertir el texto a número. Si falla (catch),
            //    el programa no crashea, sino que ejecuta el bloque 'catch'.
            try {
                // Intentamos convertir el primer argumento (ej: "20") a un número entero.
                caras = Integer.parseInt(args[0]);

                // 4. Añadimos una validación. No tiene sentido un dado de 0 o menos caras.
                if (caras <= 0) {
                    sender.sendMessage("§c¡El número de caras debe ser un número positivo!");
                    return true; // Devolvemos 'true' porque el comando se usó, aunque mal.
                }

            } catch (NumberFormatException e) {
                // 5. Si Integer.parseInt falla (ej: el jugador escribió /dado hola),
                //    se ejecuta este bloque.
                sender.sendMessage("§c'" + args[0] + "' no es un número válido.");
                return true; // También es un uso válido del comando.
            }
        }

        // 6. Generamos el número aleatorio.
        // Math.random() devuelve un número entre 0.0 y 0.999...
        // (Math.random() * caras) nos da un número entre 0.0 y 5.999... (si caras es 6)
        // Le sumamos 1 para que el rango sea de 1.0 a 6.999...
        // (int) lo convierte a entero, cortando los decimales. El resultado es un número entre 1 y 6.
        int resultado = (int) (Math.random() * caras) + 1;

        // 7. Enviamos el mensaje final al que ejecutó el comando.
        // §a es color verde, §l es negrita.
        sender.sendMessage("§aHas lanzado un dado de " + caras + " caras y ha salido un... §l" + resultado + "§a!");

        // 8. Devolvemos 'true' para decirle al servidor que manejamos el comando exitosamente.
        return true;
    }
}