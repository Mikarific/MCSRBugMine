package com.mikarific.mcsrbugmine.config.integrations;

import com.mikarific.mcsrbugmine.MCSRBugMine;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return MCSRBugMine.config::createConfigScreen;
    }
}
