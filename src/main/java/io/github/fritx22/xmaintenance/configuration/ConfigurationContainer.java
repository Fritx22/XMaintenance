package io.github.fritx22.xmaintenance.configuration;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class ConfigurationContainer<C> {
    private final AtomicReference<C> config;
    private final Class<C> clazz;
    private final HoconConfigurationLoader loader;
    private CommentedConfigurationNode root;
    private final Logger logger;
    private final Path filePath;

    private ConfigurationContainer(
            final C config,
            final Class<C> clazz,
            final HoconConfigurationLoader loader,
            final CommentedConfigurationNode root,
            final Logger logger,
            final Path filePath
    ) {
        this.config = new AtomicReference<>(config);
        this.clazz = clazz;
        this.loader = loader;
        this.root = root;
        this.logger = logger;
        this.filePath = filePath;
    }

    public static <C> ConfigurationContainer<C> load(
            final Class<C> clazz,
            final Logger logger,
            final Path filePath,
            final String header
    ) {
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder().defaultOptions(
                options -> options.header(header)
        ).path(filePath).build();

        try {
            CommentedConfigurationNode rootNode = loader.load();
            if (Files.notExists(filePath)) loader.save(rootNode);
            C config = rootNode.get(clazz);
            return new ConfigurationContainer<>(config, clazz, loader, rootNode, logger, filePath);
        } catch (ConfigurateException exception) {
            logger.severe("An exception occurred while loading configuration named " + filePath.getFileName());
            exception.printStackTrace();
            return null;
        }
    }

    public CompletableFuture<Void> reload() {
        return CompletableFuture.runAsync(() -> {
            try {
                CommentedConfigurationNode rootNode = this.loader.load();
                this.root = rootNode;
                C newConfig = rootNode.get(this.clazz);
                this.config.set(newConfig);
            } catch (ConfigurateException exception) {
                logger.severe(
                        "An exception occurred while reloading the configuration named " +
                                this.filePath.getFileName()
                );
                exception.printStackTrace();
                throw new CompletionException(exception);
            }
        });
    }

    public CompletableFuture<Void> save() {
        return CompletableFuture.runAsync(() -> {
           try {
               this.loader.save(this.root.set(this.clazz, this.config.get()));
           } catch (ConfigurateException exception) {
               this.logger.severe("An exception occurred while saving the configuration named " +
                       this.filePath.getFileName());
               exception.printStackTrace();
               throw new CompletionException(exception);
           }
        });
    }

    public C getConfig() {
        return this.config.get();
    }

}
