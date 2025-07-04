package com.tu.servidor.vigilante.modules;

import org.bukkit.event.Listener;

// Todas nuestras clases de detección implementarán esta interfaz
public interface ICheck extends Listener {
    String getCheckName();
}
