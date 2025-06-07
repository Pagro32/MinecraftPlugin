package org.example.com.serverPerformance.WorldControlling;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.example.com.serverPerformance.commands.Commands;

import java.util.*;

import static org.example.com.serverPerformance.ServerPerformance.*;

public class EventListener implements Listener {
    @EventHandler
    public void PlayerBedEnterEvent(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();

        if (world.getEnvironment() == World.Environment.NETHER || world.getEnvironment() == World.Environment.THE_END) {
            if (PlayerAbilityTwo.contains(player.getName())) {
                event.setUseBed(Event.Result.ALLOW);
            }
        }
    }
    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (PlayerAbilityOne.contains(player.getName())) {
            Commands.abilityOne(player);
        }
    }

    private final HashMap<String, Integer> playerModes = new HashMap<>();
    private final Random random = new Random();
    @EventHandler
    public void PlayerInteractEntityEvent(PlayerInteractEntityEvent event) {

        Player player = event.getPlayer();
        String playerName = player.getName();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (event.getRightClicked().getType() == EntityType.WOLF && item.getType() == Material.BONE || event.getRightClicked().getType() == EntityType.CAT && (item.getType() == Material.COD || item.getType() == Material.SALMON)) {
           // Absicherung falls bereits getamed
            if (event.getRightClicked() instanceof Tameable) {
               Tameable tameable = (Tameable) event.getRightClicked();
               if (tameable.isTamed()) return;
           }

            if (PlayerAbilityThree.contains(playerName)) {
                if (playerModes.get(playerName) == null) {

                    if (random.nextInt(2) == 0) {
                        playerModes.put(playerName, 1);
                    } else {
                        playerModes.put(playerName, 2);
                    }
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            playerModes.remove(playerName);
                        }
                    }.runTaskLater(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("ServerPerformance")), 300L);
                }

                if (playerModes.get(playerName) != null) {
                    if (playerModes.get(playerName) == 1) {
                        event.setCancelled(true);
                        event.getRightClicked().getWorld().spawnParticle(Particle.SMOKE, event.getRightClicked().getLocation().add(0,1,0), 5, 0.2, 0.2, 0.2, 0.05);
                        // Knochen oder Fisch verbrauchen
                        if (!player.getGameMode().toString().equals("CREATIVE")) { // nur wenn der Spieler nicht im Creative ist
                            item.setAmount(item.getAmount() - 1);
                        }
                    } else {
                        if (event.getRightClicked() instanceof Tameable tameable) {
                            event.setCancelled(false);
                            EntityTameEvent tameEvent = new EntityTameEvent(tameable, player);


                            tameable.getServer().getPluginManager().callEvent(tameEvent);


                            if (!tameEvent.isCancelled()) { // Tier tamen und Animationszeug
                                tameable.setTamed(true);
                                tameable.setOwner(player);
                                event.getRightClicked().getWorld().spawnParticle(Particle.HEART, event.getRightClicked().getLocation(), 2, 0.5, 0.5, 0.5);
                                event.getRightClicked().getWorld().spawnParticle(Particle.HEART, event.getRightClicked().getLocation(), 2, 0.5, 0.5, 0.5);
                                event.getRightClicked().getWorld().spawnParticle(Particle.HEART, event.getRightClicked().getLocation(), 2, 0.5, 0.5, 0.5);
                                event.getRightClicked().getWorld().spawnParticle(Particle.HEART, event.getRightClicked().getLocation(), 2, 0.5, 0.5, 0.5);
                            }
                        }
                    }
                }

            }
        }

    }

    HashMap<String, Double> jumping = new HashMap<String, Double>();
    @EventHandler
    public void PlayerMoveEvent(PlayerMoveEvent event){
        Player player = event.getPlayer();

        if (PlayerAbilityFour.contains(player.getName())) {

            double getY = event.getFrom().getY();
            double getYAfter = Objects.requireNonNull(event.getTo()).getY();

            if (getYAfter > getY) {
                jumping.computeIfAbsent(player.getName(), k -> Math.floor(getY));
            } else if (getYAfter == getY) {
                jumping.remove(player.getName());
            }


            if (jumping.get(player.getName()) != null) {
                Block blockUnderPlayer = player.getLocation().clone().subtract(0, 1, 0).getBlock();
                if (!(getYAfter <= jumping.get(player.getName()) + 0.15) || blockUnderPlayer.isPassable()) {
                    //
                }
                else {
                    jumping.remove(player.getName());

                    if (blockUnderPlayer.getType() == Material.GRASS_BLOCK || blockUnderPlayer.getType() == Material.DIRT) {
                        // 10% Chance (erstmals)
                        if (Math.random() < 0.1) {
                            blockUnderPlayer.setType(Material.AIR); // zerstört den Block
                        }
                    }
                }
            }
        }
        if (PlayerAbilitySix.contains(player.getName())) {
            Location playerLocation = player.getLocation();

            // Katzen finden
            List<LivingEntity> nearbyEntities = player.getWorld().getNearbyEntities(playerLocation, 5, 5, 5)
                    .stream()
                    .filter(entity -> entity instanceof LivingEntity && entity.getType() == EntityType.CAT)
                    .map(entity -> (LivingEntity) entity)
                    .toList();

            for (LivingEntity entity : nearbyEntities) {
                Location mobLocation = entity.getLocation();

                // Richtung von Spieler
                Vector fleeDirection = mobLocation.toVector().subtract(playerLocation.toVector()).normalize();

                // Bewegung
                entity.setVelocity(fleeDirection);

                // Blickrichtung
                entity.teleport(entity.getLocation().setDirection(fleeDirection));

            }
        }
        /*
        if (PlayerAbilitySeven.contains(player.getName())) {
            Location playerLocation = player.getLocation();

            // Alle CAT im Umkreis finden (du kannst den Mob-Typ ändern)
            List<LivingEntity> nearbyEntities = player.getWorld().getNearbyEntities(playerLocation, 15, 15, 15)
                    .stream()
                    .filter(entity -> entity instanceof LivingEntity && entity.getType() == EntityType.CAT) // Hier den Mob-Typ ändern
                    .map(entity -> (LivingEntity) entity)
                    .toList();

            for (LivingEntity entity : nearbyEntities) {
                Location mobLocation = entity.getLocation();
                if (entity instanceof Mob) {  // Sicherstellen, dass es sich um ein Mob handelt
                    Mob mob = (Mob) entity;

                    if (entity instanceof Sittable sittable) {
                        if (!sittable.isSitting()) {
                            // Berechne die Richtung vom Spieler weg
                            Location fleeLocation = mobLocation.clone().add(
                                    mobLocation.getX() - playerLocation.getX(),
                                    0,
                                    mobLocation.getZ() - playerLocation.getZ()
                            );

                            fleeLocation.setY(entity.getWorld().getHighestBlockYAt(fleeLocation));

                            // Schrittweise Bewegung des Mobs in die neue Richtung
                            new BukkitRunnable() {
                                double stepX = (fleeLocation.getX() - mobLocation.getX()) / 20; // 20 Schritte bis zum Ziel
                                double stepY = 0;
                                double stepZ = (fleeLocation.getZ() - mobLocation.getZ()) / 20;
                                int steps = 0;  // Zähler für die Schritte
                                @Override
                                public void run() {
                                    if (entity.isValid() && player.getLocation().distance(entity.getLocation()) < 15 && steps < 20) {
                                        // Bewege den Mob in kleinen Schritten
                                        double newX = mobLocation.getX() + stepX;
                                        double newY = mobLocation.getY() + stepY;
                                        double newZ = mobLocation.getZ() + stepZ;

                                        // Setze die neue Position
                                        Block blockUnder = mob.getLocation().clone().subtract(0, 1, 0).getBlock();
                                        Block blockIn = mob.getLocation().clone().subtract(0, 0, 0).getBlock();
                                        int i = 0;
                                        if (!blockIn.isPassable()) {
                                            while ((blockUnder.isPassable() || !blockIn.isPassable()) && i > -20) {
                                                i--;
                                                blockUnder = mob.getLocation().clone().subtract(0, i + 1, 0).getBlock();
                                                blockIn = mob.getLocation().clone().subtract(0, i, 0).getBlock();
                                            }
                                        }
                                        else if (blockUnder.isPassable()) {
                                            while ((blockUnder.isPassable() || !blockIn.isPassable()) && i < 20) {
                                                i++;
                                                blockUnder = mob.getLocation().clone().subtract(0, i + 1, 0).getBlock();
                                                blockIn = mob.getLocation().clone().subtract(0, i, 0).getBlock();
                                            }
                                        }
                                        newY -= i;

                                        mob.teleport(new Location(mob.getWorld(), newX, newY, newZ));
                                        Vector fleeDirection = mobLocation.toVector().subtract(playerLocation.toVector()).normalize();
                                        entity.teleport(entity.getLocation().setDirection(fleeDirection));

                                        // Aktualisiere die aktuelle Position
                                        mobLocation.setX(newX);
                                        mobLocation.setY(newY);
                                        mobLocation.setZ(newZ);

                                        steps++;
                                    } else {
                                        cancel(); // Stoppe die Aufgabe, wenn der Mob das Ziel erreicht hat oder der Spieler zu weit entfernt ist
                                    }
                                }
                            }.runTaskTimer(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("ServerPerformance")), 0L, 1L); // Führt dies jede Tick (1 Ticks) aus
                        }
                    }
                }
            }
        } */
    }
    int RISE_HEIGHT = 3;
    int BLOCK_DELAY = 10;
    int WAIT_TIME = 5 * 20;

    private final Set<IronGolem> golemsInAction = new HashSet<>();

    @EventHandler
    public void onGolemAttack(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getDamager() instanceof IronGolem golem)) return;

        Player player = (Player) event.getEntity();
        if (!PlayerAbilitySeven.contains(player.getName())) return;

        // wenn schon am Hochbauen --> passiert nichts
        if (golemsInAction.contains(golem)) return;

        golemsInAction.add(golem);
        golem.setAI(false); // Bewegung stoppen

        Location startLocation = golem.getLocation();

        new BukkitRunnable() {
            int blocksPlaced = 0;

            @Override
            public void run() {
                if (blocksPlaced >= RISE_HEIGHT) {
                    // Wenn alle Blöcke gebaut wurden - oben 5 Sekunden warten
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            golem.setAI(true);
                            golemsInAction.remove(golem);
                        }
                    }.runTaskLater(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("ServerPerformance")), WAIT_TIME);
                    this.cancel();
                    return;
                }

                // Erde unter Golem setzen
                Location blockLocation = startLocation.clone().add(0, 0, 0);
                if (blockLocation.getBlock().isPassable() || blockLocation.getBlock().getType() == Material.DIRT) blockLocation.getBlock().setType(Material.DIRT);

                // Golem nach oben teleportieren
                golem.teleport(startLocation.add(0, 1, 0));

                blocksPlaced++;

                Vector lookDirection = player.getLocation().toVector().subtract(golem.getLocation().toVector()).normalize();
                golem.teleport(golem.getLocation().setDirection(lookDirection));
            }
        }.runTaskTimer(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("ServerPerformance")), 0, BLOCK_DELAY);
    }
}
