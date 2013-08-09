package no.runsafe.mergic.magic.spells;

import no.runsafe.framework.internal.packets.PacketHelper;
import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.RunsafeWorld;
import no.runsafe.framework.minecraft.WorldEffect;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.mergic.magic.MagicSchool;
import no.runsafe.mergic.magic.Spell;
import no.runsafe.mergic.magic.SpellHandler;
import no.runsafe.mergic.magic.SpellType;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class ArcaneWave implements Spell
{
	@Override
	public int getCooldown()
	{
		return 5;
	}

	@Override
	public String getName()
	{
		return "Arcane Wave";
	}

	@Override
	public MagicSchool getSchool()
	{
		return MagicSchool.ARCANE;
	}

	@Override
	public SpellType getType()
	{
		return SpellType.PROJECTILE;
	}

	@Override
	public void onCast(RunsafePlayer player)
	{
		final RunsafeLocation location = player.getLocation();
		if (location == null)
			return;

		try
		{
			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("a", WorldEffect.CRIT.getName());
			data.put("b", (float) location.getX());
			data.put("c", (float) location.getY());
			data.put("d", (float) location.getZ());
			data.put("e", 0F);
			data.put("f", 0F);
			data.put("g", 0F);
			data.put("h", 1F);
			data.put("i", 0);

			player.sendPacket(PacketHelper.stuffPacket(PacketHelper.getPacket("Packet63WorldParticles"), data));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return;
		int current = 1;
		while (current < 11)
		{
			final int number = current;
			SpellHandler.scheduler.startSyncTask(new Runnable() {
				@Override
				public void run() {
					createSpellLine(location, number);
				}
			}, 5 * current);
			current++;
		}
	}

	private void createSpellLine(RunsafeLocation location, int step)
	{
		RunsafeWorld world = location.getWorld();
		for (int[] node : this.offsets)
		{
			RunsafeLocation position = new RunsafeLocation(
					location.getDirection().add(
							new Vector(node[0] * step, 0, node[1] * step)
					).toLocation(world.getRaw())
			);
			//world.playEffect(position, Effect.);
		}
	}

	private int[][] offsets = {
			{1, 0}, {0, 1}, {0, 1}
	};
}
