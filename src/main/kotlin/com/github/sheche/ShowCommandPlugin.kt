package com.github.sheche

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.block.BlockState
import org.bukkit.block.CommandBlock
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class ShowCommandPlugin: JavaPlugin() {

    private var cmdName = "sc"

    override fun onEnable() {
        logger.info("${ChatColor.GREEN}Enabled.")
        server.getPluginCommand(cmdName)!!.setExecutor(this)
        Bukkit.getScheduler().runTaskTimer(this, ShowCommand(), 0L, 0L)
        load()
    }

    override fun onDisable() {
        save()
    }

    private val yaml:YamlConfiguration = YamlConfiguration()

    private fun save() {
        yaml.set("NAME", playerList)
    }

    private fun load() {
        yaml.get("NAME", playerList)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (cmdName.equals(command.name, true)) {
            if (sender is Player) {
                for (p in playerList) {
                    if (p == sender) {
                        playerList.remove(sender)
                        sender.sendMessage("ShowCommand off.")
                        return true
                    }
                }
                playerList.add(sender)
                sender.sendMessage("ShowCommand on.")
                return true
            }
        }

        return true
    }
}

var playerList:ArrayList<Player> = ArrayList()

class ShowCommand: Runnable {

    override fun run() {

        for (player:Player in playerList) {
            val block: BlockState = player.getTargetBlock(null, 5).state

            if (block is CommandBlock) {
                val command = block.command
                val splitCommand = command.split(" ")
                var resultCommand = ""

                for (i in splitCommand.indices) {
                    resultCommand = "${resultCommand}${setColor(splitCommand[i])} "
                }

                player.sendActionBar(resultCommand)
            }
        }
    }

    //TODO 아직 더 만들어야됨.
    private fun setColor(str:String): String {

        var word = str

        //TODO 개선좀 해야됨.
        val list:HashSet<String> = HashSet(listOf("execute", "/execute", "scoreboard", "/scoreboard", "tp", "/tp", "summon", "/summon", "setblock", "/setblock",
            "fill", "/fill", "clone", "/clone"))

        //명령어 시작
        if (list.contains(word)) {
            word = "${ChatColor.LIGHT_PURPLE}$word"
            return word
        }

        //대상
        else if (word[0] == '@') {
            if (word.length > 2) {
                val target = "${ChatColor.AQUA}${word.substring(0, 2)}"
                val startTerms = "${ChatColor.WHITE}${word[2]}"
                val endTerms = "${ChatColor.WHITE}${word[word.length-1]}"
                val mainTerms = "${ChatColor.GOLD}${word.substring(3, word.lastIndexOf("="))}"
                val equalSign = "${ChatColor.WHITE}="
                val termsContents = "${ChatColor.GREEN}${str.substring(str.lastIndexOf("=")+1, str.length-1)}"

                word = "$target$startTerms$mainTerms$equalSign$termsContents$endTerms"
            }

            else {
                word = "${ChatColor.AQUA}$word"
            }
        }

        //숫자
        if (isInt(word) || word == "~") {
            word = "${ChatColor.AQUA}$word"
        }

        else {
            word = "${ChatColor.WHITE}$word"
        }

        return word
    }

    private fun isInt(str:String): Boolean {

        try {
            str.toInt()
        }

        catch (e:Exception) {
            return false
        }

        return true
    }
}