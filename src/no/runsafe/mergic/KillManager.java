package no.runsafe.mergic;

import no.runsafe.framework.api.event.entity.IEntityDamageByEntityEvent;
import no.runsafe.framework.minecraft.entity.RunsafeEntity;
import no.runsafe.framework.minecraft.entity.RunsafeLivingEntity;
import no.runsafe.framework.minecraft.entity.RunsafeProjectile;
import no.runsafe.framework.minecraft.event.entity.RunsafeEntityDamageByEntityEvent;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KillManager implements IEntityDamageByEntityEvent
{
	@Override
	public void OnEntityDamageByEntity(RunsafeEntityDamageByEntityEvent event)
	{
		RunsafeEntity entity = event.getEntity();
		if (entity instanceof RunsafePlayer)
		{
			RunsafePlayer victim = (RunsafePlayer) entity;
			RunsafeEntity attackingEntity = event.getDamageActor();

			if (attackingEntity instanceof RunsafePlayer)
			{
				RunsafePlayer attacker = (RunsafePlayer) attackingEntity;
				this.registerAttack(victim, attacker);
			}
			else if (attackingEntity instanceof RunsafeProjectile)
			{
				RunsafeProjectile projectile = (RunsafeProjectile) attackingEntity;
				RunsafeLivingEntity shooter = projectile.getShooter();

				if (shooter instanceof RunsafePlayer)
				{
					RunsafePlayer shooterPlayer = (RunsafePlayer) shooter;
					this.registerAttack(victim, shooterPlayer);
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
			this.killCount.put(this.lastDamage.get(playerName), this.getPlayerKills(player) + 1);
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
}
