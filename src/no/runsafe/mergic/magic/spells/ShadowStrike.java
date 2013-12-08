package no.runsafe.mergic.magic.spells;

import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.Sound;
import no.runsafe.framework.minecraft.entity.ProjectileEntity;
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
	public void onCast(IPlayer player)
	{
		player.Fire(ProjectileEntity.WitherSkull);
		ILocation loc = player.getLocation();
		loc.playSound(Sound.Creature.Wither.Hurt, 2, 2);
		loc.playSound(Sound.Creature.Wither.Shoot, 2, -1);
	}
}
