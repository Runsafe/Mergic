package no.runsafe.mergic;

import no.runsafe.framework.api.event.plugin.IPluginDisabled;
import no.runsafe.framework.minecraft.RunsafeServer;
import no.runsafe.framework.minecraft.RunsafeWorld;
import no.runsafe.framework.minecraft.entity.RunsafeEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ControlledEntityCleaner implements IPluginDisabled
{
	@Override
	public void OnPluginDisabled()
	{
		for (Map.Entry<String, List<Integer>> node : entities.entrySet())
		{
			RunsafeWorld world = RunsafeServer.Instance.getWorld(node.getKey());
			if (world != null)
			{
				for (Integer entityID : node.getValue())
				{
					RunsafeEntity entity = world.getEntityById(entityID);
					if (entity != null)
						entity.remove(); // Remove entity from the world.
				}
			}
		}
	}

	public static void registerEntity(RunsafeEntity entity)
	{
		RunsafeWorld world = entity.getWorld();
		if (world != null)
		{
			String worldName = world.getName();
			if (!entities.containsKey(worldName)) // Check if we have a container for this world.
				entities.put(worldName, new ArrayList<Integer>()); // Create one if not.

			entities.get(worldName).add(entity.getEntityId()); // Add the entity ID to the world object.
		}
	}

	public static void unregisterEntity(RunsafeEntity entity)
	{
		RunsafeWorld world = entity.getWorld();
		if (world != null)
		{
			String worldName = world.getName();
			if (entities.containsKey(worldName)) // Check if we have a container for this world.
				entities.get(worldName).remove(entity.getEntityId()); // Remove the entity.
		}
	}

	private static ConcurrentHashMap<String, List<Integer>> entities = new ConcurrentHashMap<String, List<Integer>>();
}
