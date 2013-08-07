package no.runsafe.mergic.magic.spells;

import no.runsafe.framework.api.minecraft.RunsafeEntityType;
import no.runsafe.framework.minecraft.entity.LivingEntity;
import no.runsafe.framework.minecraft.entity.RunsafeEntity;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.mergic.magic.MagicSchool;
import no.runsafe.mergic.magic.Spell;
import no.runsafe.mergic.magic.SpellHandler;
import no.runsafe.mergic.magic.SpellType;

public class VolatileCreature implements Spell
{
	@Override
	public int getCooldown()
	{
		return 5;
	}

	@Override
	public String getName()
	{
		return "Conjure Volatile Creature";
	}

	@Override
	public MagicSchool getSchool()
	{
		return MagicSchool.ARCANE;
	}

	@Override
	public SpellType getType()
	{
		return SpellType.PROJECTILE;
	}

	@Override
	public void onCast(RunsafePlayer player)
	{
		final RunsafeEntity entity = player.Launch(this.getRandomEntityType()); // Spawn random creature.
		if (entity != null)
		{
			// Create a timer to blow the entity up five seconds later.
			SpellHandler.scheduler.startSyncTask(new Runnable() {
				@Override
				public void run() {
					entity.getWorld().createExplosion(entity.getLocation(), 5, false, false); // Blow the entity up.
					entity.remove(); // Remove the entity.
				}
			}, 5);
		}
	}

	private RunsafeEntityType getRandomEntityType()
	{
		return this.types[(int)(Math.random() * types.length)];
	}

	private RunsafeEntityType[] types = {
			LivingEntity.Cow, LivingEntity.Sheep, LivingEntity.Chicken, LivingEntity.Pig
	};
}
