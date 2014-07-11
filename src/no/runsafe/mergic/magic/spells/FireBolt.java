package no.runsafe.mergic.magic.spells;

import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.mergic.KillManager;
import no.runsafe.mergic.magic.MagicSchool;

public class FireBolt extends Bolt
{
	public FireBolt(IScheduler scheduler, KillManager manager)
	{
		super(Item.Unavailable.StationaryLava, scheduler, manager);
	}

	@Override
	public String getName()
	{
		return "Fire Bolt";
	}

	@Override
	public MagicSchool getSchool()
	{
		return MagicSchool.FIRE;
	}
}
