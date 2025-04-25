package com.mikarific.mcsrbugmine.config;

import com.mikarific.mcsrbugmine.MCSRBugMine;
import com.mojang.logging.LogUtils;
import me.contaria.speedrunapi.config.SpeedrunConfigContainer;
import me.contaria.speedrunapi.config.api.SpeedrunConfig;
import me.contaria.speedrunapi.config.api.annotations.Config.Access;
import me.contaria.speedrunapi.config.api.annotations.Config.Category;
import me.contaria.speedrunapi.config.api.annotations.Config.Ignored;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.GameInstance;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;

public class Config implements SpeedrunConfig {
    @Ignored
    private SpeedrunConfigContainer<?> container;

    @Category("crashfix")
    public boolean preventCtrlQFreeze = true;

    @Category("crashfix")
    public boolean preventPacketDisconnect = true;

    @Category("crashfix")
    public boolean preventSoulLinkCrash = true;

    @Category("ami")
    public boolean obtainableNoDrops = false;

    @Category("aa")
    public boolean allowSoulLinkOnLanServers = false;

    @Category("aa")
    public boolean functionalShields = false;

    @Category("aa")
    public boolean obtainableDragonFire = false;

    @Category("aa") @Access(setter = "setObtainableInItTogether")
    public boolean obtainableInItTogether = false;

    {
        MCSRBugMine.config = this;
    }

    @SuppressWarnings("unused")
    public void setObtainableInItTogether(boolean obtainableInItTogether) {
        this.obtainableInItTogether = obtainableInItTogether;
        triggerReload();
    }

    @SuppressWarnings("resource")
    public void triggerReload() {
        GameInstance gameInstance = MinecraftClient.getInstance().method_70242();
        if (gameInstance != null) gameInstance.reloadResources(gameInstance.getDataPackManager().getEnabledIds()).exceptionally((throwable) -> {
            LogUtils.getLogger().warn("Failed to execute reload", throwable);
            return null;
        });
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public void save() {
        try {
            this.container.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String modID() {
        return "mcsrbugmine";
    }

    @Override
    public void finishInitialization(SpeedrunConfigContainer<?> container) {
        this.container = container;
    }

    public static String[] getOptions() {
        return Arrays.stream(Config.class.getFields()).filter((field) -> !field.isAnnotationPresent(Ignored.class)).map(Field::getName).toArray(String[]::new);
    }
}
