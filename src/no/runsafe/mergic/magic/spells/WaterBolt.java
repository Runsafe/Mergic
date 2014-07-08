package no.runsafe.mergic.magic.spells;

import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.mergic.KillManager;
import no.runsafe.mergic.magic.MagicSchool;

public class WaterBolt extends Bolt
{
	public WaterBolt(IScheduler scheduler, KillManager manager)
	{
		super(Item.Unavailable.Water, scheduler, manager);
	}

	@Override
	public String getName()
	{
		return "Water Bolt";
	}

	@Override
	public MagicSchool getSchool()
	{
		return MagicSchool.WATER;
	}
}
