package no.runsafe.mergic.magic.spells;

import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.internal.wrapper.BukkitLocation;
import no.runsafe.framework.minecraft.Sound;
import no.runsafe.mergic.magic.MagicSchool;
import no.runsafe.mergic.magic.Spell;
import no.runsafe.mergic.magic.SpellType;

public class WindJump implements Spell
{
	@Override
	public int getCooldown()
	{
		return 5;
	}

	@Override
	public String getName()
	{
		return "Wind Jump";
	}

	@Override
	public MagicSchool getSchool()
	{
		return MagicSchool.WIND;
	}

	@Override
	public SpellType getType()
	{
		return SpellType.GENERIC;
	}

	@Override
	public String getDescription()
	{
		return "Rush forward with the power of the wind.";
	}

	@Override
	public void onCast(IPlayer player)
	{
		ILocation playerLoc = player.getLocation();
		player.setVelocity(((BukkitLocation) playerLoc).getDirection().multiply(3));
		playerLoc.playSound(Sound.Player.Breath, 2, -1);
	}
}
