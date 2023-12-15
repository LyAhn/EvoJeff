package dev.cosgy.jmusicbot.slashcommands.owner;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jdautilities.commons.JDAUtilitiesInfo;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.utils.OtherUtil;
import com.sedmelluq.discord.lavaplayer.tools.PlayerLibrary;
import dev.cosgy.jmusicbot.slashcommands.OwnerCommand;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.utils.FileUpload;

public class DebugCmd extends OwnerCommand {
    private final static String[] PROPERTIES = {"java.version", "java.vm.name", "java.vm.specification.version",
            "java.runtime.name", "java.runtime.version", "java.specification.version", "os.arch", "os.name"};

    private final Bot bot;

public DebugCmd(Bot bot) {
         this.bot = bot;
         this.name = "debug";
         this.help = "Show debug information";
         this.aliases = bot.getConfig().getAliases(this.name);
         this.guildOnly = false;
     }

     @Override
     protected void execute(SlashCommandEvent event) {
         StringBuilder sb = new StringBuilder();
         sb.append("System properties:");
         for (String key : PROPERTIES)
             sb.append("\n ").append(key).append(" = ").append(System.getProperty(key));
         sb.append("\n\nJMusicBot JP information:")
                 .append("\n Version = ").append(OtherUtil.getCurrentVersion())
                 .append("\n Owner = ").append(bot.getConfig().getOwnerId())
                 .append("\n Prefix = ").append(bot.getConfig().getPrefix())
                 .append("\n AltPrefix = ").append(bot.getConfig().getAltPrefix())
                 .append("\n MaxSeconds = ").append(bot.getConfig().getMaxSeconds())
                 .append("\n NPImages = ").append(bot.getConfig().useNPImages())
                 .append("\n SongInStatus = ").append(bot.getConfig().getSongInStatus())
                 .append("\n StayInChannel = ").append(bot.getConfig().getStay())
                 .append("\n UseEval = ").append(bot.getConfig().useEval())
                 .append("\n UpdateAlerts = ").append(bot.getConfig().useUpdateAlerts());
         sb.append("\n\nDependency information:")
                 .append("\n JDA Version = ").append(JDAInfo.VERSION)
                 .append("\n JDA-Utilities Version = ").append(JDAUtilitiesInfo.VERSION)
                 .append("\n Lavaplayer Version = ").append(PlayerLibrary.VERSION);
         long total = Runtime.getRuntime().totalMemory() / 1024 / 1024;
         long used = total - (Runtime.getRuntime().freeMemory() / 1024 / 1024);
         sb.append("\n\nRuntime information:")
                 .append("\n Total Memory = ").append(total)
                 .append("\n Used Memory = ").append(used);
         sb.append("\n\nDiscord information:")
                 .append("\n ID = ").append(event.getJDA().getSelfUser().getId())
                 .append("\n Guilds = ").append(event.getJDA().getGuildCache().size())
                 .append("\n Users = ").append(event.getJDA().getUserCache().size());
         sb.append("\nIf you wish to send this file to a developer, please do so without editing it.")
                 .append("\nThis file does not contain any data that could be used to identify you or take over your account.");

         if (event.isFromGuild() || event.getGuild().getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_ATTACH_FILES)) {
             event.reply("debug information").queue();
             event.getChannel().sendFiles(FileUpload.fromData(sb.toString().getBytes(), "debug_information.txt")).queue();
         } else {
             event.reply("Debug information: ```\n" + sb + "\n```").queue();
         }
     }

     @Override
     protected void execute(CommandEvent event) {
         StringBuilder sb = new StringBuilder();
         sb.append("System properties:");
         for (String key : PROPERTIES)
             sb.append("\n ").append(key).append(" = ").append(System.getProperty(key));
         sb.append("\n\nJMusicBot JP information:")
                 .append("\n Version = ").append(OtherUtil.getCurrentVersion())
                 .append("\n Owner = ").append(bot.getConfig().getOwnerId())
                 .append("\n Prefix = ").append(bot.getConfig().getPrefix())
                 .append("\n AltPrefix = ").append(bot.getConfig().getAltPrefix())
                 .append("\n MaxSeconds = ").append(bot.getConfig().getMaxSeconds())
                 .append("\n NPImages = ").append(bot.getConfig().useNPImages())
                 .append("\n SongInStatus = ").append(bot.getConfig().getSongInStatus())
                 .append("\n StayInChannel = ").append(bot.getConfig().getStay())
                 .append("\n UseEval = ").append(bot.getConfig().useEval())
                 .append("\n UpdateAlerts = ").append(bot.getConfig().useUpdateAlerts());
         sb.append("\n\nDependency information:")
                 .append("\n JDA Version = ").append(JDAInfo.VERSION)
                 .append("\n JDA-Utilities Version = ").append(JDAUtilitiesInfo.VERSION)
                 .append("\n Lavaplayer Version = ").append(PlayerLibrary.VERSION);
         long total = Runtime.getRuntime().totalMemory() / 1024 / 1024;
         long used = total - (Runtime.getRuntime().freeMemory() / 1024 / 1024);
         sb.append("\n\nRuntime information:")
                 .append("\n Total Memory = ").append(total)
                 .append("\n Used Memory = ").append(used);
         sb.append("\n\nDiscord information:")
                 .append("\n ID = ").append(event.getJDA().getSelfUser().getId())
                 .append("\n Guilds = ").append(event.getJDA().getGuildCache().size())
                 .append("\n Users = ").append(event.getJDA().getUserCache().size());
         sb.append("\nIf you wish to send this file to a developer, please do so without editing it.");

                if (event.isFromType(ChannelType.PRIVATE)
                 || event.getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_ATTACH_FILES))
             event.getChannel().sendFiles(FileUpload.fromData(sb.toString().getBytes(), "debug_information.txt")).queue();
         else
             event.reply("Debug information: ```\n" + sb + "\n```");
     }
}