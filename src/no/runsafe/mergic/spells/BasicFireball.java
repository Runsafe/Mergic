package no.runsafe.mergic.spells;

import no.runsafe.framework.minecraft.entity.ProjectileEntity;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

public class BasicFireball implements Spell
{
	@Override
	public int getCooldown()
	{
		return 2;
	}

	@Override
	public String getName()
	{
		return "Basic Fireball";
	}

	@Override
	public MagicSchool getSchool()
	{
		return MagicSchool.FIRE;
	}

	@Override
	public void onCast(RunsafePlayer player)
	{
		player.Fire(ProjectileEntity.SmallFireball);
	}
}
