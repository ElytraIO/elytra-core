package fr.elytra.processor.plugins;

import java.util.List;

public record PluginDefinitionData(
        String mainClassPath,
        String pluginName,
        String pluginVersion,
        String pluginDescription,
        List<String> pluginAuthors) {

}
