package com.lumaa.ynyd;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YnydMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("ynyd");

	@Override
	public void onInitialize() {
		LOGGER.info("Your Name Your Death activated");
	}
}
