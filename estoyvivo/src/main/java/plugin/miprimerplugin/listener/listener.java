package plugin.miprimerplugin.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class listener implements Listener {

    // 1. La anotación @EventHandler es la clave.
    // Le dice a bukkit: Este método es una reacción a un evento.
    @EventHandler
    public void alEntrarUnJugador(PlayerJoinEvent event) {
        // 2. El parámetro 'event' contiene toda la información sobre lo que acaba de pasar.

        // 3. Del evento, podemos obtener al jugador que se unió.
        Player jugador = event.getPlayer();

        // 4. Ahora que tenemos el objeto 'jugador', podemos hacer lo que queramos con él.
        String nombreDelJugador = jugador.getName();

        // Le enviamos un mensaje de bienvenida personalizado.
        jugador.sendMessage("§e¡Bienvenido a nuestro servidor, " + nombreDelJugador + "! §a¡Disfruta tu estancia!");

        // También podemos modificar el mensaje de entrada que ven todos.
        event.setJoinMessage("§a[+] §7" + nombreDelJugador);
    }
}