package no.runsafe.mergic.magic.spells;


import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.Sound;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
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
	public void onCast(final RunsafePlayer player)
	{
		// Throw the player in the air to begin.
		RunsafeLocation loc = player.getLocation();
		player.setVelocity(loc.getDirection().add(new Vector(0, 2, 0)));
		loc.Play(Sound.Creature.Golem.Death, 2, -1);

		// Throw the player forward two seconds later.
		SpellHandler.scheduler.startSyncTask(new Runnable() {
			@Override
			public void run() {
				RunsafeLocation playerLoc = player.getLocation();
				player.setVelocity(playerLoc.getDirection().multiply(3));
				playerLoc.Play(Sound.Player.Breath, 2, -1);
			}
		}, 1);
	}
}
