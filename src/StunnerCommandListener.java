
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;


public class StunnerCommandListener extends PluginListener{
    private Logger log=Logger.getLogger("Minecraft");
    private StunnerTowns plugin;
    HashMap<Player, Player> acceptMap = new HashMap<Player, Player>();
    
    public StunnerCommandListener(){
        this.plugin = StunnerTowns.getInstance();
    }
    
    
    
        public boolean onCommand(Player player, String[] cmd){
            TownPlayer tp = plugin.getManager().getTownPlayer(player.getOfflineName());
        
            if(player.canUseCommand("/stunnertowns") && (cmd[0].equalsIgnoreCase("/town") || cmd[0].equalsIgnoreCase("/t"))){

    //                                  //
    //            Here Command          //
    //                                  //
                if(cmd.length == 1){
                    help(player);
                }
                if(cmd[1].equalsIgnoreCase("here")){
                    int chunkx = (int)player.getX() >> 4;
                    int chunkz = (int)player.getZ() >> 4;
                    String chunky = chunkx+":"+chunkz;
                    String townName = "Wilderness";
                    if(plugin.getManager().containsKey(chunky)){
                        townName = plugin.getManager().get(chunky);
                    }
                    Town town = plugin.getManager().getTown(townName);
                    
                    if(town != null){
                        int chunksallowed = (plugin.getConfig().getChunkMultiplier() * town.getMemberCount()) + town.getBonus(); 
                        player.sendMessage("§a[§b" + plugin.getConfig().getServerName() + "§a] §fThis Land is: §b" + town.getRankName()+ " " + townName + " Territory§f.");
                        player.sendMessage("§a"+town.getMayorName()+"§b:§f " + town.getOwner());
                        player.sendMessage("§a"+town.getAssistantName()+"§b:§f " + town.getAssistant());
                        player.sendMessage("§aFlags§b:§f " + town.getFlagString());
                        player.sendMessage("§aClaimed Land§b:§f " + String.valueOf(plugin.getManager().chunkAmount(town.getName())) + "/" + String.valueOf(chunksallowed));
                        player.sendMessage("§aMembers§b:§f " + town.getMembers().toString());
                        return true;
                    }
                     player.sendMessage("§a[§b" + plugin.getConfig().getServerName() + "§a] §fThis Land is: §b" + townName + " Territory§f.");
                    
                    return true;
                }

                

            
    //                                  //
    //            Create Town           //
    //                                  //            
            
            if(cmd[1].equalsIgnoreCase("create") && cmd.length > 2){
                if(tp != null){
                    player.sendMessage("§a[§b" + plugin.getConfig().getServerName() + "§a] §fYou are already in a town! Please leave it before starting a new one.");
                    return true;
                }
                String moneyname = "";
                if(plugin.getConfig().getUseDCO()){
                    moneyname = (String)etc.getLoader().callCustomHook("dCBalance", new Object[] {"Money-Name"});
                    Double pbalance = (Double)etc.getLoader().callCustomHook("dCBalance", new Object[] {"Account-Balance", player.getOfflineName()});
                    if(pbalance < plugin.getConfig().getTownCost()){
                        player.sendMessage("§a[§b" + plugin.getConfig().getServerName() + "§a] §fNot enough §b" + moneyname + " §fto start a town.");
                        player.sendMessage("    §b- §fYou need:  §b" + plugin.getConfig().getTownCost() + " " +  moneyname);
                        return true;
                    }
                }
                
                StringBuilder sb = new StringBuilder();
                for(int i = 2 ; i < cmd.length ; i++){
                    sb.append(cmd[i] + " ");
                }
                String townName = sb.toString().trim();
                MySQL mysql = new MySQL();
                if(mysql.keyExists(townName, "towns", "name")){
                    player.sendMessage("§a[§b" + plugin.getConfig().getServerName() + "§a] §fTown §a'§b"+townName+"§a'§f already exists.");
                    return true;
                }
                mysql.insertTownPlayer(player.getOfflineName(), townName);
                mysql.insertTown(townName, player.getOfflineName(), "");
                player.sendMessage("§a[§b" + plugin.getConfig().getServerName() + "§a] §fTown §a'§b"+townName+"§a'§f has been created!");
                if(plugin.getConfig().getUseDCO()){
                    etc.getLoader().callCustomHook("dCBalance", new Object[] {"Account-Withdraw", player.getOfflineName(), plugin.getConfig().getTownCost()});
                    player.sendMessage("    §b- §fYou have been charged:  §b" + plugin.getConfig().getTownCost() + " " +  moneyname);
                }
               
                return true;
            }
            

            
    //                                  //
    //            Accept Town           //
    //                                  //
            if(cmd[1].equalsIgnoreCase("accept") && tp == null ){
                if(!acceptMap.containsKey(player)){
                    player.sendMessage("§a[§b" + plugin.getConfig().getServerName() + "§a] §f No pending invites at this time.");
                    return true;
                }
                TownPlayer otp = plugin.getManager().getTownPlayer(acceptMap.get(player).getOfflineName());
                if(cmd[2].equalsIgnoreCase(otp.getTownName())){
                    MySQL mysql = new MySQL();
                    mysql.insertTownPlayer(player.getOfflineName(), otp.getTownName());
                    player.sendMessage("§a[§b" + plugin.getConfig().getServerName() + "§a] §bYou have accepted the invitation to " + otp.getTownName()+".");
                   acceptMap.get(player).sendMessage("§a[§b" + plugin.getConfig().getServerName() + "§a] §b"+player.getOfflineName()+"§f has accepted an invitation to your town.");
                   acceptMap.remove(player);
                }
            }
            

            

            

            
    //                                  //
    //            Leave Town            //
    //                                  //
//            if(cmd[1].equalsIgnoreCase("setbonus")){
//                
//            }
            
    //                                  //
    //            Leave Town            //
    //                                  //
//            if(cmd[1].equalsIgnoreCase("setbonus")){
//            
//            }
            
            
            
                if(player.isAdmin()){
                    if(cmd[1].equalsIgnoreCase("addbonus") && cmd.length == 4){
                        Town town = plugin.getManager().getTown(cmd[2]);
                        if(town != null){
                            town.addBonus(Integer.valueOf(cmd[3]));
                            player.sendMessage("§a[§b" + plugin.getConfig().getServerName() + "§a] §f Added bonus land plots to: §b" + town.getName());
                            return true;
                        }
                        player.sendMessage("§a[§b" + plugin.getConfig().getServerName() + "§a] §f Town does not exist: §b" + cmd[2]);
                        return true;
                    }
                    
                    if(cmd[1].equalsIgnoreCase("setbonus") && cmd.length == 4){
                        Town town = plugin.getManager().getTown(cmd[2]);
                        if(town != null){
                            town.setBonus(Integer.valueOf(cmd[3]));
                            player.sendMessage("§a[§b" + plugin.getConfig().getServerName() + "§a] §f Set bonus land plots of: §b" + town.getName());
                            return true;
                        }
                        player.sendMessage("§a[§b" + plugin.getConfig().getServerName() + "§a] §f Town does not exist: §b" + cmd[2]);
                        return true;
                    }
                } 
                help(player);
                return true;
            }
        return false;
    }
        
        
        public void help(Player player){
            player.sendMessage("§a[§b" + plugin.getConfig().getServerName() + "§a] §fStunnerTowns help");
            player.sendMessage("  §a- /t claim    §fClaim land for your town");
            player.sendMessage("  §a- /t unclaim  §fUnclaim land for your town");
            player.sendMessage("  §a- /t here  §fSee who owns the town where you are standing");
            player.sendMessage("  §a- /t unclaim  §fUnclaim land for your town");
            player.sendMessage("  §a- /t invite [playername]  §finvite a player to your town");
            player.sendMessage("  §a- /t accept [townname]  §faccept an invitation to a town");
            player.sendMessage("  §a- /t create  [townname]§fcreate a town");
            player.sendMessage("  §a- /t disband  §fdisband your town");
        }
    
}
