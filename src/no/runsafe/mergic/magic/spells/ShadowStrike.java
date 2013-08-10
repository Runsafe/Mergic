package no.runsafe.mergic.magic.spells;

import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.Sound;
import no.runsafe.framework.minecraft.entity.ProjectileEntity;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.mergic.magic.MagicSchool;
import no.runsafe.mergic.magic.Spell;
import no.runsafe.mergic.magic.SpellType;

public class ShadowStrike implements Spell
{
	@Override
	public int getCooldown()
	{
		return 1;
	}

	@Override
	public String getName()
	{
		return "Shadow Strike";
	}

	@Override
	public MagicSchool getSchool()
	{
		return MagicSchool.SHADOW;
	}

	@Override
	public SpellType getType()
	{
		return SpellType.PROJECTILE;
	}

	@Override
	public String getDescription()
	{
		return "A basic shadow-based attack.";
	}

	@Override
	public void onCast(RunsafePlayer player)
	{
		player.Fire(ProjectileEntity.WitherSkull);
		RunsafeLocation loc = player.getLocation();
		loc.Play(Sound.Creature.Wither.Hurt, 10, 2);
		loc.Play(Sound.Creature.Wither.Shoot, 10, -1);
	}
}
