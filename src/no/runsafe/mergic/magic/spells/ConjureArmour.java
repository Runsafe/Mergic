package no.runsafe.mergic.magic.spells;

import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.mergic.EquipmentManager;
import no.runsafe.mergic.magic.MagicSchool;
import no.runsafe.mergic.magic.Spell;
import no.runsafe.mergic.magic.SpellHandler;
import no.runsafe.mergic.magic.SpellType;

public class ConjureArmour implements Spell
{
	@Override
	public int getCooldown()
	{
		return 10;
	}

	@Override
	public String getName()
	{
		return "Conjure Armour";
	}

	@Override
	public MagicSchool getSchool()
	{
		return MagicSchool.ARCANE;
	}

	@Override
	public SpellType getType()
	{
		return SpellType.WARD;
	}

	@Override
	public String getDescription()
	{
		return "Protect yourself in some magical armour for 5 seconds.";
	}

	@Override
	public void onCast(final IPlayer player)
	{
		EquipmentManager.applyFullProtection(player);
		SpellHandler.scheduler.startSyncTask(new Runnable()
		{
			@Override
			public void run()
			{
				EquipmentManager.removeFullProtection(player);
			}
		}, 5);
	}
}
