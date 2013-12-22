package no.runsafe.mergic.magic.spells;


import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.internal.wrapper.BukkitLocation;
import no.runsafe.framework.minecraft.Sound;
import no.runsafe.mergic.magic.MagicSchool;
import no.runsafe.mergic.magic.Spell;
import no.runsafe.mergic.magic.SpellHandler;
import no.runsafe.mergic.magic.SpellType;
import org.bukkit.util.Vector;

public class WindLeap implements Spell
{
	@Override
	public int getCooldown()
	{
		return 20;
	}

	@Override
	public String getName()
	{
		return "Wind Leap";
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
		return "Use the wind to throw yourself forth.";
	}

	@Override
	public void onCast(final IPlayer player)
	{
		// Throw the player in the air to begin.
		ILocation loc = player.getLocation();
		player.setVelocity(((BukkitLocation) loc).getDirection().add(new Vector(0, 2, 0)));
		loc.playSound(Sound.Creature.Golem.Death, 2, -1);

		// Throw the player forward two seconds later.
		SpellHandler.scheduler.startSyncTask(new Runnable()
		{
			@Override
			public void run()
			{
				ILocation playerLoc = player.getLocation();
				player.setVelocity(((BukkitLocation) playerLoc).getDirection().multiply(3));
				//playerLoc.playSound(Sound.Player.Breath, 2, -1);
			}
		}, 1);
	}
}
