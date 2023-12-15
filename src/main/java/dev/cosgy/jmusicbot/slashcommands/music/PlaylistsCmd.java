/*
 * Copyright 2018 John Grosh (jagrosh).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.cosgy.jmusicbot.slashcommands.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jmusicbot.Bot;
import dev.cosgy.jmusicbot.slashcommands.MusicCommand;

import java.util.List;

/**
  * @author John Grosh <john.a.grosh@gmail.com>
  */
public class PlaylistsCmd extends MusicCommand {
     public PlaylistsCmd(Bot bot) {
         super(bot);
         this.name = "playlists";
         this.help = "Display available playlists";
         this.aliases = bot.getConfig().getAliases(this.name);
         this.guildOnly = true;
         this.beListening = false;
     }

     @Override
     public void doCommand(CommandEvent event) {
         String guildID = event.getGuild().getId();
         if (!bot.getPlaylistLoader().folderExists())
             bot.getPlaylistLoader().createFolder();
         if (!bot.getPlaylistLoader().folderGuildExists(guildID))
             bot.getPlaylistLoader().createGuildFolder(guildID);
         if (!bot.getPlaylistLoader().folderExists()) {
             event.reply(event.getClient().getWarning() + "The playlist folder could not be created because it does not exist.");
             return;
         }
         if (!bot.getPlaylistLoader().folderGuildExists(guildID)) {
             event.reply(event.getClient().getWarning() + "The playlist folder for this server does not exist and could not be created.");
             return;
         }
         List<String> list = bot.getPlaylistLoader().getPlaylistNames(guildID);
         if (list == null)
             event.reply(event.getClient().getError() + "Failed to load available playlists.");
         else if (list.isEmpty())
             event.reply(event.getClient().getWarning() + "There are no playlists in the playlist folder.");
         else {
             StringBuilder builder = new StringBuilder(event.getClient().getSuccess() + "Available playlists:\n");
             list.forEach(str -> builder.append("`").append(str).append("` "));
             builder.append("\n`").append(event.getClient().getTextualPrefix()).append("You can play the playlist by typing play playlist <name>`.");
             event.reply(builder.toString());
         }
     }

     @Override
     public void doCommand(SlashCommandEvent event) {
         String guildID = event.getGuild().getId();
         if (!bot.getPlaylistLoader().folderExists())
             bot.getPlaylistLoader().createFolder();
         if (!bot.getPlaylistLoader().folderGuildExists(guildID))
             bot.getPlaylistLoader().createGuildFolder(guildID);
         if (!bot.getPlaylistLoader().folderExists()) {
             event.reply(event.getClient().getWarning() + "The playlist folder could not be created because it does not exist.").queue();
             return;
         }
         if (!bot.getPlaylistLoader().folderGuildExists(guildID)) {
             event.reply(event.getClient().getWarning() + "The playlist folder for this server does not exist and could not be created.").queue();
             return;
         }
         List<String> list = bot.getPlaylistLoader().getPlaylistNames(guildID);
         if (list == null)
             event.reply(event.getClient().getError() + "Failed to load available playlists.").queue();
         else if (list.isEmpty())
             event.reply(event.getClient().getWarning() + "There are no playlists in the playlist folder.").queue();
         else {
             StringBuilder builder = new StringBuilder(event.getClient().getSuccess() + "Available playlists:\n");
             list.forEach(str -> builder.append("`").append(str).append("` "));
             builder.append("\n`").append(event.getClient().getTextualPrefix()).append("You can play the playlist by typing play playlist <name>`.");
             event.reply(builder.toString()).queue();
         }
     }
}