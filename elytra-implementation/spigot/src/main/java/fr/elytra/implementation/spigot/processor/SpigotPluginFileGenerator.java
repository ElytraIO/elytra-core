package fr.elytra.implementation.spigot.processor;

import fr.elytra.dependency_injection.filter.ClassSubsetData;
import fr.elytra.dependency_injection.filter.ClassSubsetData.AnnotationData;
import fr.elytra.implementation.spigot.annotations.SpigotPlugin;
import fr.elytra.processor.plugins.PluginDefinitionData;
import fr.elytra.processor.plugins.PluginFileDefinitionGenerator;

public class SpigotPluginFileGenerator implements PluginFileDefinitionGenerator {

    @Override
    public String getFileName() {
        return "plugin.yml";
    }

    @Override
    public String generateFile(PluginDefinitionData data, ClassSubsetData lazyClass) {
        StringBuilder builder = new StringBuilder();

        builder.append("main: ").append(data.mainClassPath()).append("\n");
        builder.append("name: ").append(data.pluginName()).append("\n");
        builder.append("version: ").append(data.pluginVersion()).append("\n");
        builder.append("description: ").append(data.pluginDescription()).append("\n");

        // Authors
        builder.append("authors: ").append("\n");
        for (String author : data.pluginAuthors()) {
            builder.append("  - ").append(author).append("\n");
        }

        // Spigot specific data
        AnnotationData spigotPluginData = lazyClass.getAnnotation(SpigotPlugin.class);
        if (spigotPluginData != null) {
            builder.append("api-version: ").append(spigotPluginData.values().get("apiVersion")).append("\n");
        }

        return builder.toString();
    }

}
