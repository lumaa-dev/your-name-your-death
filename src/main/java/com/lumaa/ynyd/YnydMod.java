package com.lumaa.ynyd;

import com.lumaa.ynyd.items.ModItems;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YnydMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("ynyd");
	public static final String id = "ynyd";

	@Override
	public void onInitialize() {
		ModItems.registerItems();
		LOGGER.info("Your Name Your Death - Initiated");
	}

	public class Preferences {
		public static final float healthAfterTotem = 20.0f;
		public static final int maxTotemCount = 5;
	}
}
