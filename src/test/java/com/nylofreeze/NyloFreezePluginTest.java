package com.nylofreeze;


import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class NyloFreezePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(NyloFreezePlugin.class);
		RuneLite.main(args);
	}
}