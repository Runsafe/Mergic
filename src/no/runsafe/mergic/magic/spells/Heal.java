package no.runsafe.mergic.magic.spells;

import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.WorldEffect;
import no.runsafe.mergic.magic.MagicSchool;
import no.runsafe.mergic.magic.Spell;
import no.runsafe.mergic.magic.SpellHandler;
import no.runsafe.mergic.magic.SpellType;

public class Heal implements Spell
{
	@Override
	public int getCooldown()
	{
		return 10;
	}

	@Override
	public String getName()
	{
		return "Heal";
	}

	@Override
	public MagicSchool getSchool()
	{
		return MagicSchool.GENERIC;
	}

	@Override
	public SpellType getType()
	{
		return SpellType.HEAL;
	}

	@Override
	public String getDescription()
	{
		return "Heals you for 7 hearts over 7 seconds.";
	}

	@Override
	public void onCast(final IPlayer player)
	{
		int current = 0;
		while (current < 7)
		{
			SpellHandler.scheduler.startSyncTask(new Runnable()
			{
				@Override
				public void run()
				{
					player.heal(2); // Heal one heart of damage
					player.getEyeLocation().playEffect(WorldEffect.HEART, 1, 2, 50); // Play heart effect.
				}
			}, current);
			current++;
		}
	}
}
