package no.runsafe.mergic;

import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.IServer;
import no.runsafe.framework.api.event.entity.IEntityDamageByEntityEvent;
import no.runsafe.framework.api.event.player.IPlayerDamageEvent;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.minecraft.WorldBlockEffect;
import no.runsafe.framework.minecraft.WorldBlockEffectType;
import no.runsafe.framework.minecraft.entity.RunsafeEntity;
import no.runsafe.framework.minecraft.entity.RunsafeProjectile;
import no.runsafe.framework.minecraft.event.entity.RunsafeEntityDamageByEntityEvent;
import no.runsafe.framework.minecraft.event.entity.RunsafeEntityDamageEvent;
import no.runsafe.mergic.achievements.TouchOfDeath;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class KillManager implements IEntityDamageByEntityEvent, IPlayerDamageEvent
{
	public KillManager(IServer server, Graveyard graveyard, Arena arena)
	{
		this.server = server;
		this.graveyard = graveyard;
		this.arena = arena;
	}

	@Override
	public void OnEntityDamageByEntity(RunsafeEntityDamageByEntityEvent event)
	{
		RunsafeEntity entity = event.getEntity();

		if (entity instanceof IPlayer)
		{
			IPlayer victim = (IPlayer) entity;
			RunsafeEntity attackingEntity = event.getDamageActor();

			if (attackingEntity instanceof IPlayer)
			{
				IPlayer attacker = (IPlayer) attackingEntity;
				registerAttack(victim, attacker);
			}
			else if (attackingEntity instanceof RunsafeProjectile)
			{
				RunsafeProjectile projectile = (RunsafeProjectile) attackingEntity;
				IPlayer shooterPlayer = projectile.getShootingPlayer();

				if (shooterPlayer != null)
					registerAttack(victim, shooterPlayer);
			}
		}
	}

	@Override
	public void OnPlayerDamage(IPlayer player, RunsafeEntityDamageEvent event)
	{
		if (arena.playerIsInGame(player))
		{
			if (!graveyard.playerIsInGraveyard(player))
				handlePlayerDamage(player, event.getDamage());

			event.setDamage(0);
		}
	}

	public void attackPlayer(IPlayer player, IPlayer attacker, double damage)
	{
		ILocation playerLocation = player.getLocation();
		if (playerLocation != null)
			playerLocation.playEffect(bloodEffect, 0.3F, 100, 50); // Blood splash!

		registerAttack(player, attacker);
		handlePlayerDamage(player, damage);
	}

	private void handlePlayerDamage(IPlayer player, double damage)
	{
		if (player.getHealth() - damage <= 0D)
		{
			graveyard.teleportPlayerToGraveyard(player); // Teleport player to graveyard.
			player.setHealth(20D); // Heal the player to full.
			player.setFireTicks(0); // Stop the fire from burning if they are.
			player.setFoodLevel(20); // Fill the hunger bar back to full.
			EquipmentManager.repairBoots(player); // Repair the players boots.

			// If we can confirm they were killed, tell them who by, otherwise default message.
			IPlayer killer = getKiller(player);
			if (killer == null)
			{
				player.sendColouredMessage("&cYou have died! You will respawn shortly.");
			}
			else
			{
				player.sendColouredMessage("&cYou were killed by %s! You will respawn shortly.", killer.getName());
				killer.sendColouredMessage("&aYou killed %s, +1 point.", player.getName());

				if (graveyard.playerIsInGraveyard(killer)) // If the killer is in the graveyard..
					new TouchOfDeath(killer).Fire(); // Award this achievement.
			}
			OnPlayerKilled(player); // Trigger event in kill manager to tally score.
		}
		else
		{
			player.damage(damage);
		}
	}

	public void wipeAllData()
	{
		lastDamage.clear();
		killCount = new ConcurrentHashMap<UUID, Integer>(0);
	}

	public void wipePlayerData(IPlayer player)
	{
		lastDamage.remove(player.getUniqueId());
		killCount.remove(player.getUniqueId());
	}

	public void registerAttack(IPlayer victim, IPlayer attacker)
	{
		if (!attacker.isVanished() && !victim.isVanished())
			lastDamage.put(victim.getUniqueId(), attacker.getUniqueId());
	}

	public void OnPlayerKilled(IPlayer player)
	{
		if (lastDamage.containsKey(player.getUniqueId()))
		{
			IPlayer killer = server.getPlayer(lastDamage.get(player.getUniqueId()));

			if (killer != null)
			{
				int newKills = getPlayerKills(killer.getUniqueId()) + 1;
				killCount.put(killer.getUniqueId(), newKills);
				killer.setLevel(newKills);
			}
		}
	}

	public IPlayer getKiller(IPlayer player)
	{
		return server.getPlayer(lastDamage.get(player.getUniqueId()));
	}

	public int getPlayerKills(UUID player)
	{
		return killCount.containsKey(player) ? killCount.get(player) : 0;
	}

	public ConcurrentHashMap<UUID, Integer> getScoreList()
	{
		return killCount;
	}

	private final IServer server;
	private final Graveyard graveyard;
	private final Arena arena;
	private ConcurrentHashMap<UUID, UUID> lastDamage = new ConcurrentHashMap<UUID, UUID>();
	private ConcurrentHashMap<UUID, Integer> killCount = new ConcurrentHashMap<UUID, Integer>();
	private final static WorldBlockEffect bloodEffect = new WorldBlockEffect(WorldBlockEffectType.BLOCK_DUST, Item.BuildingBlock.Wool.Red);
}
