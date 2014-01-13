package no.runsafe.mergic.magic.spells;

import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.mergic.KillManager;
import no.runsafe.mergic.magic.MagicSchool;

public class IceBolt extends Bolt
{
	public IceBolt(IScheduler scheduler, KillManager manager)
	{
		super(Item.BuildingBlock.Ice, scheduler, manager);
	}

	@Override
	public String getName()
	{
		return "Ice Bolt";
	}

	@Override
	public MagicSchool getSchool()
	{
		return MagicSchool.FROST;
	}
}
