package com.tudominio.foliabans.util.tabcompletion;

import java.util.List;

@FunctionalInterface
public interface TabCompleter {
    List<String> onTabComplete(Object user, String[] args);
}