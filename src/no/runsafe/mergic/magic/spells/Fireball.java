package no.runsafe.mergic.magic.spells;

import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.Sound;
import no.runsafe.framework.minecraft.entity.ProjectileEntity;
import no.runsafe.mergic.magic.MagicSchool;
import no.runsafe.mergic.magic.Spell;
import no.runsafe.mergic.magic.SpellType;

public class Fireball implements Spell
{
	@Override
	public int getCooldown()
	{
		return 2;
	}

	@Override
	public String getName()
	{
		return "Fireball";
	}

	@Override
	public MagicSchool getSchool()
	{
		return MagicSchool.FIRE;
	}

	@Override
	public SpellType getType()
	{
		return SpellType.PROJECTILE;
	}

	@Override
	public String getDescription()
	{
		return "Fires a normal fireball.";
	}

	@Override
	public void onCast(IPlayer player)
	{
		player.Fire(ProjectileEntity.Fireball);
		player.getLocation().playSound(Sound.Creature.Ghast.Fireball, 2, 0); // Play a slow-fireball sound.
	}
}
