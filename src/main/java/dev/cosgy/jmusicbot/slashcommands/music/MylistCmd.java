package dev.cosgy.jmusicbot.slashcommands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jmusicbot.Bot;
import dev.cosgy.jmusicbot.playlist.MylistLoader;
import dev.cosgy.jmusicbot.slashcommands.DJCommand;
import dev.cosgy.jmusicbot.slashcommands.MusicCommand;
import dev.cosgy.jmusicbot.util.StackTraceUtil;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
  * @author kosugikun
  */
public class MylistCmd extends MusicCommand {

     public MylistCmd(Bot bot) {
         super(bot);
         this.guildOnly = false;
         this.name = "mylist";
         this.arguments = "<append|delete|make|all>";
         this.help = "Manage your own playlist";
         this.aliases = bot.getConfig().getAliases(this.name);
         this.children = new MusicCommand[]{
                 new MakelistCmd(bot),
                 new DeletelistCmd(bot),
                 new AppendlistCmd(bot),
                 new ListCmd(bot)
         };
     }

     @Override
     public void doCommand(CommandEvent event) {

         StringBuilder builder = new StringBuilder(event.getClient().getWarning() + "My list management command:\n");
         for (Command cmd : this.children)
             builder.append("\n`").append(event.getClient().getPrefix()).append(name).append(" ").append(cmd.getName())
                     .append(" ").append(cmd.getArguments() == null ? "" : cmd.getArguments()).append("` - ").append(cmd.getHelp());
         event.reply(builder.toString());
     }

     @Override
     public void doCommand(SlashCommandEvent slashCommandEvent) {
     }

     public static class MakelistCmd extends DJCommand {
         public MakelistCmd(Bot bot) {
             super(bot);
             this.name = "make";
             this.aliases = new String[]{"create"};
             this.help = "Create new playlist";
             this.arguments = "<name>";
             this.guildOnly = true;
             this.ownerCommand = false;

             List<OptionData> options = new ArrayList<>();
             options.add(new OptionData(OptionType.STRING, "name", "playlist name", true));
             this.options = options;
         }

         @Override
         public void doCommand(CommandEvent event) {

             String pName = event.getArgs().replaceAll("\\s+", "_");
             String userId = event.getAuthor().getId();

             if (pName.isEmpty()) {
                 event.replyError("Please specify a playlist name.");
                 return;
             }

             if (bot.getMylistLoader().getPlaylist(userId, pName) == null) {
                 try {
                     bot.getMylistLoader().createPlaylist(userId, pName);
                     event.reply(event.getClient().getSuccess() + "My List `" + pName + "`Created");
                 } catch (IOException e) {
                     if (event.isOwner() || event.getMember().isOwner()) {
                         event.replyError("An error occurred while loading the song.\n" +
                                 "**Error content: " + e.getLocalizedMessage() + "**");
                         StackTraceUtil.sendStackTrace(event.getTextChannel(), e);
                         return;
                     }

                     event.reply(event.getClient().getError() + "My list could not be created.:" + e.getLocalizedMessage());
                 }
             } else {
                 event.reply(event.getClient().getError() + "My List `" + pName + "` already exists");
             }
         }

         @Override
         public void doCommand(SlashCommandEvent event) {
             String pName = event.getOption("name").getAsString().replaceAll("\\s+", "_");
             String userId = event.getUser().getId();

             if (pName.isEmpty()) {
                 event.reply(event.getClient().getError() + "Please specify a playlist name.").queue();
                 return;
             }

             if (bot.getMylistLoader().getPlaylist(userId, pName) == null) {
                 try {
                     bot.getMylistLoader().createPlaylist(userId, pName);
                     event.reply(event.getClient().getSuccess() + "My List `" + pName + "`Created").queue();
                 } catch (IOException e) {
                     if (event.getClient().getOwnerId() == event.getMember().getId() || event.getMember().isOwner()) {
                         event.reply(event.getClient().getError() + "An error occurred while loading the song.\n" +
                                 "**Error content: " + e.getLocalizedMessage() + "**").queue();
                         StackTraceUtil.sendStackTrace(event.getTextChannel(), e);
                         return;
                     }

                     event.reply(event.getClient().getError() + "My list could not be created.:" + e.getLocalizedMessage()).queue();
                 }
             } else {
                 event.reply(event.getClient().getError() + "My List `" + pName + "` already exists").queue();
             }
         }
     }

    public static class DeletelistCmd extends MusicCommand {
        public DeletelistCmd(Bot bot) {
            super(bot);
            this.name = "delete";
            this.aliases = new String[]{"remove"};
            this.help = "Delete existing My List";
            this.arguments = "<name>";
            this.guildOnly = true;
            this.ownerCommand = false;

            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "name", "playlist name", true));
            this.options = options;
        }

        @Override
        public void doCommand(CommandEvent event) {

            String pName = event.getArgs().replaceAll("\\s+", "_");
            String userId = event.getAuthor().getId();
            if (!pName.equals("")) {
                if (bot.getMylistLoader().getPlaylist(userId, pName) == null)
                    event.reply(event.getClient().getError() + "My list does not exist:`" + pName + "`");
                else {
                    try {
                        bot.getMylistLoader().deletePlaylist(userId, pName);
                        event.reply(event.getClient().getSuccess() + "My list deleted:`" + pName + "`");
                    } catch (IOException e) {
                        event.reply(event.getClient().getError() + " Failed to delete my list: " + e.getLocalizedMessage());
                    }
                }
            } else {
                event.reply(event.getClient().getError() + "Please include the name of my list");
            }
        }

        @Override
        public void doCommand(SlashCommandEvent event) {
            String pName = event.getOption("name").getAsString().replaceAll("\\s+", "_");
            String userId = event.getUser().getId();

            if (bot.getMylistLoader().getPlaylist(userId, pName) == null)
                event.reply(event.getClient().getError() + "My list does not exist:`" + pName + "`").queue();
            else {
                try {
                    bot.getMylistLoader().deletePlaylist(userId, pName);
                    event.reply(event.getClient().getSuccess() + "My list deleted:`" + pName + "`").queue();
                } catch (IOException e) {
                    event.reply(event.getClient().getError() + " Failed to delete my list: " + e.getLocalizedMessage()).queue();
                }
            }
        }
    }

    public static class AppendlistCmd extends MusicCommand {
        public AppendlistCmd(Bot bot) {
            super(bot);
            this.name = "append";
            this.aliases = new String[]{"add"};
            this.help = "Add song to existing My List";
            this.arguments = "<name> <URL> | <URL> | ...";
            this.guildOnly = true;
            this.ownerCommand = false;
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "name", "playlist name", true));
            options.add(new OptionData(OptionType.STRING, "url", "URL", true));
            this.options = options;
        }

        @Override
        public void doCommand(CommandEvent event) {

            String[] parts = event.getArgs().split("\\s+", 2);
            String userId = event.getAuthor().getId();
            if (parts.length < 2) {
                event.reply(event.getClient().getError() + "Please include the My List name and URL to which you want to add.");
                return;
            }
            String pName = parts[0];
            MylistLoader.Playlist playlist = bot.getMylistLoader().getPlaylist(userId, pName);
            if (playlist == null)
                event.reply(event.getClient().getError() + "My list does not exist:`" + pName + "`");
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
                    bot.getMylistLoader().writePlaylist(userId, pName, builder.toString());
                    event.reply(event.getClient().getSuccess() + urls.length + " Added item to my list:`" + pName + "`");
                } catch (IOException e) {
                    event.reply(event.getClient().getError() + " Could not add to my list: " + e.getLocalizedMessage());
                }
            }
        }

        @Override
         public void doCommand(SlashCommandEvent event) {
             String userId = event.getUser().getId();
             String pname = event.getOption("name").getAsString();
             MylistLoader.Playlist playlist = bot.getMylistLoader().getPlaylist(userId, pname);
             if (playlist == null)
                 event.reply(event.getClient().getError() + "My list does not exist:`" + pname + "`").queue();
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
                     bot.getMylistLoader().writePlaylist(userId, pname, builder.toString());
                     event.reply(event.getClient().getSuccess() + urls.length + " Added item to my list:`" + pname + "`").queue();
                 } catch (IOException e) {
                     event.reply(event.getClient().getError() + " Could not add to my list: " + e.getLocalizedMessage()).queue();
                 }
             }
         }
     }

     public static class ListCmd extends MusicCommand {
         public ListCmd(Bot bot) {
             super(bot);
             this.name = "all";
             this.aliases = new String[]{"available", "list"};
             this.help = "Show all available My Lists";
             this.guildOnly = true;
             this.ownerCommand = false;
         }

         @Override
         public void doCommand(CommandEvent event) {
             String userId = event.getAuthor().getId();

             if (!bot.getMylistLoader().folderUserExists(userId))
                 bot.getMylistLoader().createUserFolder(userId);
             if (!bot.getMylistLoader().folderUserExists(userId)) {
                 event.reply(event.getClient().getWarning() + "My list folder could not be created because it does not exist.");
                 return;
             }
             List<String> list = bot.getMylistLoader().getPlaylistNames(userId);
             if (list == null)
                 event.reply(event.getClient().getError() + "Failed to load available My Lists.");
             else if (list.isEmpty())
                 event.reply(event.getClient().getWarning() + "There are no playlists in my list folder.");
             else {
                 StringBuilder builder = new StringBuilder(event.getClient().getSuccess() + "My list available:\n");
                 list.forEach(str -> builder.append("`").append(str).append("` "));
                 event.reply(builder.toString());
             }
         }

         @Override
         public void doCommand(SlashCommandEvent event) {
             String userId = event.getUser().getId();

             if (!bot.getMylistLoader().folderUserExists(userId))
                 bot.getMylistLoader().createUserFolder(userId);
             if (!bot.getMylistLoader().folderUserExists(userId)) {
                 event.reply(event.getClient().getWarning() + "My list folder could not be created because it does not exist.").queue();
                 return;
             }
             List<String> list = bot.getMylistLoader().getPlaylistNames(userId);
             if (list == null)
                 event.reply(event.getClient().getError() + "Failed to load any available My Lists.").queue();
             else if (list.isEmpty())
                 event.reply(event.getClient().getWarning() + "There are no playlists in my list folder.").queue();
             else {
                 StringBuilder builder = new StringBuilder(event.getClient().getSuccess() + "My list available:\n");
                 list.forEach(str -> builder.append("`").append(str).append("` "));
                 event.reply(builder.toString()).queue();
             }
         }
     }
}