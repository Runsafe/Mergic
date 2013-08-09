package no.runsafe.mergic.magic.spells;

import no.runsafe.framework.minecraft.entity.RunsafeEntity;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.mergic.magic.MagicSchool;
import no.runsafe.mergic.magic.Spell;
import no.runsafe.mergic.magic.SpellHandler;
import no.runsafe.mergic.magic.SpellType;

public class SkyStrikes implements Spell
{
	@Override
	public int getCooldown()
	{
		return 20;
	}

	@Override
	public String getName()
	{
		return "Sky Strikes";
	}

	@Override
	public MagicSchool getSchool()
	{
		return MagicSchool.EARTH;
	}

	@Override
	public SpellType getType()
	{
		return SpellType.GENERIC;
	}

	@Override
	public void onCast(RunsafePlayer player)
	{
		for (RunsafeEntity entity : player.getNearbyEntities(20, 20, 20))
		{
			if (entity instanceof RunsafePlayer)
			{
				entity.strikeWithLightning(false); // Hit the player with lightning.
				SpellHandler.killManager.registerAttack((RunsafePlayer) entity, player); // Register the attack.
			}
		}
	}
}
