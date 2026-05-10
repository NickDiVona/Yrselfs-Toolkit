package com.yrselfs;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class YrselfsToolkitPluginTest {
	public static void main(String[] args) throws Exception {
		ExternalPluginManager.loadBuiltin(YrselfsToolkitPlugin.class);
		RuneLite.main(args);
	}
}
