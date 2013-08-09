package no.runsafe.mergic;

import no.runsafe.framework.api.IOutput;
import no.runsafe.framework.api.event.entity.IEntityDamageByEntityEvent;
import no.runsafe.framework.minecraft.RunsafeServer;
import no.runsafe.framework.minecraft.entity.RunsafeEntity;
import no.runsafe.framework.minecraft.entity.RunsafeProjectile;
import no.runsafe.framework.minecraft.event.entity.RunsafeEntityDamageByEntityEvent;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

import java.util.HashMap;
import java.util.Map;

public class KillManager implements IEntityDamageByEntityEvent
{
	public KillManager(IOutput output)
	{
		this.output = output;
	}

	@Override
	public void OnEntityDamageByEntity(RunsafeEntityDamageByEntityEvent event)
	{
		RunsafeEntity entity = event.getEntity();

		// DEBUG
		int entityID = entity.getEntityId();
		this.output.fine("[%d] Took damage from another entity.", entityID);

		if (entity instanceof RunsafePlayer)
		{
			// DEBUG
			this.output.fine("[%d] Confirmed to be a player.", entityID);

			RunsafePlayer victim = (RunsafePlayer) entity;
			RunsafeEntity attackingEntity = event.getDamageActor();

			// DEBUG
			int attackingEntityID = attackingEntity.getEntityId();
			this.output.fine("[%d] Attacked by %d", entityID, attackingEntityID);

			if (attackingEntity instanceof RunsafePlayer)
			{
				RunsafePlayer attacker = (RunsafePlayer) attackingEntity;
				this.registerAttack(victim, attacker);

				// DEBUG
				this.output.fine("[%d] Confirmed to be a player", attackingEntityID);
			}
			else if (attackingEntity instanceof RunsafeProjectile)
			{
				RunsafeProjectile projectile = (RunsafeProjectile) attackingEntity;
				RunsafePlayer shooterPlayer = projectile.getShooterPlayer();

				// DEBUG
				this.output.fine("[%d] Confirmed to be a projectile.", attackingEntityID);
				if (shooterPlayer != null)
				{
					// DEBUG
					this.output.fine("[%d] Projectile was shot by a player.", attackingEntityID);

					this.registerAttack(victim, shooterPlayer);
					this.output.fine("[%d] Projectile shooter was %s", attackingEntityID, shooterPlayer.getName());
				}
			}
		}
	}

	public void wipeAllData()
	{
		this.lastDamage.clear();
	}

	public void wipePlayerData(RunsafePlayer player)
	{
		this.lastDamage.remove(player.getName());
	}

	private void registerAttack(RunsafePlayer victim, RunsafePlayer attacker)
	{
		this.lastDamage.put(victim.getName(),  attacker.getName());
	}

	public void OnPlayerKilled(RunsafePlayer player)
	{
		String playerName = player.getName();
		if (this.lastDamage.containsKey(playerName))
		{
			String killerName = this.lastDamage.get(playerName);
			RunsafePlayer killer = RunsafeServer.Instance.getPlayerExact(killerName);

			if (killer != null)
			{
				int newKills = this.getPlayerKills(killer) + 1;
				this.killCount.put(killerName, newKills);
				killer.setLevel(newKills);
			}
		}
	}

	public int getPlayerKills(RunsafePlayer player)
	{
		String playerName = player.getName();
		return this.killCount.containsKey(playerName) ? this.killCount.get(playerName) : 0;
	}

	public Map<String, Integer> getTopWinners(int amount)
	{
		return MapUtil.limitMap(MapUtil.sortByValue(this.killCount), amount);
	}

	private HashMap<String, String> lastDamage = new HashMap<String, String>();
	private HashMap<String, Integer> killCount = new HashMap<String, Integer>();
	private IOutput output;
}
