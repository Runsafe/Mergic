package no.runsafe.mergic.magic.spells;

import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.mergic.KillManager;
import no.runsafe.mergic.magic.MagicSchool;

public class NatureBolt extends Bolt
{
	public NatureBolt(IScheduler scheduler, KillManager manager)
	{
		super(Item.BuildingBlock.Wood.Oak, scheduler, manager);
	}

	@Override
	public String getName()
	{
		return "Nature Bolt";
	}

	@Override
	public MagicSchool getSchool()
	{
		return MagicSchool.NATURE;
	}
}
