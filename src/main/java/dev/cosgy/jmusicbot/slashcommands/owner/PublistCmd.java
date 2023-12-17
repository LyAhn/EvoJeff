package dev.cosgy.jmusicbot.slashcommands.owner;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jmusicbot.Bot;
import dev.cosgy.jmusicbot.playlist.PubliclistLoader.Playlist;
import dev.cosgy.jmusicbot.slashcommands.OwnerCommand;
import dev.cosgy.jmusicbot.slashcommands.admin.AutoplaylistCmd;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
  * @author kosugikun
  */
public class PublistCmd extends OwnerCommand {
     private final Bot bot;

     public PublistCmd(Bot bot) {
         this.bot = bot;
         this.guildOnly = false;
         this.name = "publist";
         this.arguments = "<append|delete|make|all>";
         this.help = "Playlist management";
         this.aliases = bot.getConfig().getAliases(this.name);
         this.children = new OwnerCommand[]{
                 new ListCmd(),
                 new AppendlistCmd(),
                 new DeletelistCmd(),
                 new MakelistCmd()
         };
     }

     @Override
     protected void execute(SlashCommandEvent slashCommandEvent) {

     }

     @Override
     public void execute(CommandEvent event) {
         StringBuilder builder = new StringBuilder(event.getClient().getWarning() + " Playlist management command:\n");
         for (Command cmd : this.children)
             builder.append("\n`").append(event.getClient().getPrefix()).append(name).append(" ").append(cmd.getName())
                     .append(" ").append(cmd.getArguments() == null ? "" : cmd.getArguments()).append("` - ").append(cmd.getHelp());
         event.reply(builder.toString());
     }

     public static class DefaultlistCmd extends AutoplaylistCmd {
         public DefaultlistCmd(Bot bot) {
             super(bot);
             this.name = "setdefault";
             this.aliases = new String[]{"default"};
             this.arguments = "<playlistname|NONE>";
             this.guildOnly = true;
         }
     }

     public class MakelistCmd extends OwnerCommand {
         public MakelistCmd() {
             this.name = "make";
             this.aliases = new String[]{"create"};
             this.help = "Create a new playlist";
             this.arguments = "<name>";
             this.guildOnly = false;

             List<OptionData> options = new ArrayList<>();
             options.add(new OptionData(OptionType.STRING, "name", "playlist name", true));
             this.options = options;
         }

         @Override
         protected void execute(SlashCommandEvent event) {
             String pname = event.getOption("name").getAsString().replaceAll("\\s+", "_");
             if (bot.getPublistLoader().getPlaylist(pname) == null) {
                 try {
                     bot.getPublistLoader().createPlaylist(pname);
                     event.reply(event.getClient().getSuccess() + "You created a playlist with the name " + pname).queue();
                 } catch (IOException e) {
                     event.reply(event.getClient().getError() + "Could not create playlist.:" + e.getLocalizedMessage()).queue();
                 }
             } else
                 event.reply(event.getClient().getError() + "Playlist '" + pname + "' already exists!").queue();
         }

         @Override
         protected void execute(CommandEvent event) {
             String pname = event.getArgs().replaceAll("\\s+", "_");
             if (bot.getPublistLoader().getPlaylist(pname) == null) {
                 try {
                     bot.getPublistLoader().createPlaylist(pname);
                     event.reply(event.getClient().getSuccess() + "You created a playlist with the name " + pname);
                 } catch (IOException e) {
                     event.reply(event.getClient().getError() + "Unable to create playlist.:" + e.getLocalizedMessage());
                 }
             } else
                 event.reply(event.getClient().getError() + "Playlist '" + pname + "' already exists!");
         }
     }

     public class DeletelistCmd extends OwnerCommand {
         public DeletelistCmd() {
             this.name = "delete";
             this.aliases = new String[]{"remove"};
             this.help = "Delete an existing playlist";
             this.arguments = "<name>";
             this.guildOnly = false;

             List<OptionData> options = new ArrayList<>();
             options.add(new OptionData(OptionType.STRING, "name", "playlist name", true));
             this.options = options;
         }

         @Override
         protected void execute(SlashCommandEvent event) {
             String pname = event.getOption("name").getAsString().replaceAll("\\s+", "_");
             if (bot.getPublistLoader().getPlaylist(pname) == null)
                 event.reply(event.getClient().getError() + "Playlist `" + pname + "` does not exist!").queue();
             else {
                 try {
                     bot.getPublistLoader().deletePlaylist(pname);
                     event.reply(event.getClient().getSuccess() + "Playlist `" + pname + "` deleted!").queue();
                 } catch (IOException e) {
                     event.reply(event.getClient().getError() + "Failed to delete playlist: " + e.getLocalizedMessage()).queue();
                 }
             }
         }


@Override
         protected void execute(CommandEvent event) {
             String pname = event.getArgs().replaceAll("\\s+", "_");
             if (bot.getPublistLoader().getPlaylist(pname) == null)
                 event.reply(event.getClient().getError() + "Playlist `" + pname + "` does not exist!");
             else {
                 try {
                     bot.getPublistLoader().deletePlaylist(pname);
                     event.reply(event.getClient().getSuccess() + "Playlist '" + pname + "' deleted.!");
                 } catch (IOException e) {
                     event.reply(event.getClient().getError() + " Unable to delete playlist: " + e.getLocalizedMessage());
                 }
             }
         }
     }

     public class AppendlistCmd extends OwnerCommand {
         public AppendlistCmd() {
             this.name = "append";
             this.aliases = new String[]{"add"};
             this.help = "Add songs to an existing playlist";
             this.arguments = "<name> <URL> | <URL> | ...";
             this.guildOnly = false;
             List<OptionData> options = new ArrayList<>();
             options.add(new OptionData(OptionType.STRING, "name", "playlist name", true));
             options.add(new OptionData(OptionType.STRING, "url", "URL", true));
             this.options = options;
         }

         @Override
         protected void execute(SlashCommandEvent event) {
             String pname = event.getOption("name").getAsString();
             Playlist playlist = bot.getPublistLoader().getPlaylist(pname);
             if (playlist == null)
                 event.reply(event.getClient().getError() + "Playlist '" + pname + "' does not exist!").queue();
             else {
                 StringBuilder builder = new StringBuilder();
                 playlist.getItems().forEach(item -> builder.append("\r\n").append(item));
                 String[] urls = event.getOption("url").getAsString().split("\\|");
                 for (String url : urls) {
                     String u = url.trim();
                     if (u.startsWith("<") && u.endsWith(">"))
                         u = u.substring(1, u.length() - 1);
                     builder.append("\r\n").append(u);
                 }
                 try {
                     bot.getPublistLoader().writePlaylist(pname, builder.toString());
                     event.reply(event.getClient().getSuccess() + urls.length + " Item added to playlist '" + pname + "'!").queue();
                 } catch (IOException e) {
                     event.reply(event.getClient().getError() + "Unable to add to playlist: " + e.getLocalizedMessage()).queue();
                 }
             }
         }

         @Override
         protected void execute(CommandEvent event) {
             String[] parts = event.getArgs().split("\\s+", 2);
             if (parts.length < 2) {
                 event.reply(event.getClient().getError() + "Please include the playlist name and URL to add to.");
                 return;
             }
             String pname = parts[0];
             Playlist playlist = bot.getPublistLoader().getPlaylist(pname);
             if (playlist == null)
                 event.reply(event.getClient().getError() + "Playlist '" + pname + "' does not exist!");
             else {
                 StringBuilder builder = new StringBuilder();
                 playlist.getItems().forEach(item -> builder.append("\r\n").append(item));
                 String[] urls = parts[1].split("\\|");
                 for (String url : urls) {
                     String u = url.trim();
                     if (u.startsWith("<") && u.endsWith(">"))
                         u = u.substring(1, u.length() - 1);
                     builder.append("\r\n").append(u);
                 }
                 try {
                     bot.getPublistLoader().writePlaylist(pname, builder.toString());
                     event.reply(event.getClient().getSuccess() + urls.length + "Added item to playlist '" + pname + "'!");
                 } catch (IOException e) {
                     event.reply(event.getClient().getError() + " Could not add to playlist: " + e.getLocalizedMessage());
                 }
             }
         }
     }

     public class ListCmd extends OwnerCommand {
         public ListCmd() {
             this.name = "all";
             this.aliases = new String[]{"available", "list"};
             this.help = "Display all available playlists.";
             this.guildOnly = true;
         }


        @Override
         protected void execute(SlashCommandEvent event) {
             if (!bot.getPublistLoader().folderExists())
                 bot.getPublistLoader().createFolder();
             if (!bot.getPublistLoader().folderExists()) {
                 event.reply(event.getClient().getWarning() + "The playlist folder could not be created because it does not exist.").queue();
                 return;
             }
             List<String> list = bot.getPublistLoader().getPlaylistNames();
             if (list == null)
                 event.reply(event.getClient().getError() + "Failed to load available playlists.").queue();
             else if (list.isEmpty())
                 event.reply(event.getClient().getWarning() + "There are no playlists in the playlist folder.").queue();
             else {
                 StringBuilder builder = new StringBuilder(event.getClient().getSuccess() + "Available playlists:\n");
                 list.forEach(str -> builder.append("`").append(str).append("` "));
                 event.reply(builder.toString()).queue();
             }
         }

         @Override
         protected void execute(CommandEvent event) {
             if (!bot.getPublistLoader().folderExists())
                 bot.getPublistLoader().createFolder();
             if (!bot.getPublistLoader().folderExists()) {
                 event.reply(event.getClient().getWarning() + "The playlist folder could not be created because it does not exist.");
                 return;
             }
             List<String> list = bot.getPublistLoader().getPlaylistNames();
             if (list == null)
                 event.reply(event.getClient().getError() + "Failed to load available playlists.");
             else if (list.isEmpty())
                 event.reply(event.getClient().getWarning() + "There are no playlists in the playlist folder.");
             else {
                 StringBuilder builder = new StringBuilder(event.getClient().getSuccess() + "Available playlists:\n");
                 list.forEach(str -> builder.append("`").append(str).append("` "));
                 event.reply(builder.toString());
             }
         }
     }
}