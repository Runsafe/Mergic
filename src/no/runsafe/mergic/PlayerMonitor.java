package no.runsafe.mergic;

import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.event.player.*;
import no.runsafe.framework.api.event.plugin.IPluginDisabled;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.event.player.*;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;
import no.runsafe.mergic.magic.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayerMonitor implements IPlayerCustomEvent, IPlayerJoinEvent, IPlayerInteractEvent, IPluginDisabled, IPlayerQuitEvent, IPlayerDropItemEvent
{
	public PlayerMonitor(Graveyard graveyard, Arena arena, Game game, Lobby lobby, SpellHandler spellHandler, CooldownManager cooldownManager, KillManager killManager, MagicClassHandler classHandler, IScheduler scheduler)
	{
		this.graveyard = graveyard;
		this.arena = arena;
		this.game = game;
		this.lobby = lobby;
		this.spellHandler = spellHandler;
		this.cooldownManager = cooldownManager;
		this.killManager = killManager;
		this.classHandler = classHandler;
		this.scheduler = scheduler;
	}

	@Override
	public void OnPlayerQuit(RunsafePlayerQuitEvent event)
	{
		IPlayer player = event.getPlayer();
		if (arena.playerIsInGame(player))
			game.removePlayerFromGame(player);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void OnPlayerCustomEvent(RunsafeCustomEvent event)
	{
		// Did we trigger a region leave event?
		if (event.getEvent().equals("region.leave"))
		{
			// See if the region the player left is the arena region.
			Map<String, String> data = (Map<String, String>) event.getData();
			if (arena.getArenaRegionString().equals(String.format("%s-%s", data.get("world"), data.get("region"))))
			{
				final IPlayer player = event.getPlayer();

				// Check if the player is actually in the game.
				if (arena.playerIsInGame(player))
				{
					scheduler.runNow(new Runnable()
					{
						@Override
						public void run()
						{
							game.removePlayerFromGame(player); // Throw them from the game.
						}
					});
				}
			}
		}
		else if (event.getEvent().equals("region.enter")) // Or maybe an enter?
		{
			// See if the region the player entered is the arena region.
			Map<String, String> data = (Map<String, String>) event.getData();
			if (lobby.getLobbyRegionString().equals(String.format("%s-%s", data.get("world"), data.get("region"))))
			{
				final IPlayer player = event.getPlayer();

				scheduler.runNow(new Runnable()
				{
					@Override
					public void run()
					{
						player.getInventory().clear(); // Clear the players inventory.
						classHandler.applyRandomClass(player); // Set a random school of magic for the player.
						spellHandler.givePlayerAllSpells(player); // Give the player all spells.
						EquipmentManager.givePlayerWizardBoots(player); // Give the player some magic boots!
						player.setLevel(killManager.getPlayerKills(player)); // Update the players level.
					}
				});
			}
		}
	}

	@Override
	public void OnPlayerJoinEvent(RunsafePlayerJoinEvent event)
	{
		IPlayer player = event.getPlayer();

		// Check if the player is inside the arena when they shouldn't be.
		if (!arena.playerIsInGame(player) && arena.playerIsInPhysicalArena(player))
			lobby.teleportPlayerToLobby(player); // Teleport them to the lobby!
	}

	@Override
	public void OnPlayerInteractEvent(RunsafePlayerInteractEvent event)
	{
		IPlayer player = event.getPlayer();

		// Check the player is registered as playing the game.
		if (isDebugging(player) || (arena.playerIsInGame(player) && !graveyard.playerIsInGraveyard(player)))
		{
			RunsafeMeta item = event.getItemStack();
			if (item == null)
				return;

			Spell spell = spellHandler.getSpellByName(item.getDisplayName()); // Grab the spell.
			if (spell != null)
			{
				SpellType type = spell.getType(); // Get the spell type.

				// If we want a left click but we're not getting it, return to cancel processing here.
				if (type.getInteractType() == InteractType.LEFT_CLICK && !event.isLeftClick())
					return;

				// If we want a right click but we're not getting it, return to cancel processing here.
				if (type.getInteractType() == InteractType.RIGHT_CLICK && !event.isRightClick())
					return;

				// Check if we have the right item and are not on cooldown for that school.
				if (item.is(type.getCastItem()) && cooldownManager.canCastSpell(player, spell))
				{
					spell.onCast(player); // Make the player cast the spell.
					cooldownManager.applySchoolCooldown(player, spell); // Apply school cooldown.
				}
			}
		}
	}

	@Override
	public void OnPluginDisabled()
	{
		// If the server shuts down, we should cancel the game just to make sure.
		game.cancelGame();
	}

	@Override
	public void OnPlayerDropItem(RunsafePlayerDropItemEvent event)
	{
		IPlayer player = event.getPlayer();
		if (arena.playerIsInPhysicalArena(player) || lobby.playerIsInLobby(player))
			event.getItem().remove(); // Remove the item.
	}

	public boolean isDebugging(IPlayer player)
	{
		return debuggers.contains(player);
	}

	public boolean toggleDebugging(IPlayer player)
	{
		boolean isDebugging = isDebugging(player);

		if (isDebugging)
			debuggers.remove(player);
		else
			debuggers.add(player);

		return !isDebugging;
	}

	private Graveyard graveyard;
	private Arena arena;
	private Game game;
	private Lobby lobby;
	private SpellHandler spellHandler;
	private CooldownManager cooldownManager;
	private KillManager killManager;
	private final MagicClassHandler classHandler;
	private final List<IPlayer> debuggers = new ArrayList<>();
	private final IScheduler scheduler;
}
