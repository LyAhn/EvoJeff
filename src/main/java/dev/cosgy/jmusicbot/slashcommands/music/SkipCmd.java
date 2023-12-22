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
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.RequestMetadata;
import dev.cosgy.jmusicbot.slashcommands.MusicCommand;

/**
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class SkipCmd extends MusicCommand {
    public SkipCmd(Bot bot) {
        super(bot);
        this.name = "skip";
        this.help = "Request to skip the currently playing song";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.beListening = true;
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event) {
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();

        RequestMetadata rm = handler.getRequestMetadata();
        if (event.getAuthor().getIdLong() == rm.getOwner()) {
            event.reply(event.getClient().getSuccess() + "**" + (handler.getPlayer().getPlayingTrack().getInfo().uri.contains("https://stream.gensokyoradio.net/" ) ? "Gensokyo Radio" : handler.getPlayer().getPlayingTrack().getInfo().title) + "** Skipped.");
            handler.getPlayer().stopTrack();
        } else {
            // Number of people in voice chat (not including bots and speaker mutes)
            int listeners = (int) event.getSelfMember().getVoiceState().getChannel().getMembers().stream()
                    .filter(m -> !m.getUser().isBot() && !m.getVoiceState().isDeafened() && m.getUser().getIdLong() != handler.getRequestMetadata().getOwner()). count();

            // message to send
            String msg;

            // Get the current vote and see if it includes the sender of the message
            if (handler.getVotes().contains(event.getAuthor().getId())) {
                msg = event.getClient().getWarning() + "The currently playing song has been requested to be skipped. `[";
            } else {
                msg = event.getClient().getSuccess() + "You requested to skip the current song.`[";
                handler.getVotes().add(event.getAuthor().getId());
            }

            // Get the number of people on voice chat who have voted to skip
            int skippers = (int) event.getSelfMember().getVoiceState().getChannel().getMembers().stream()
                    .filter(m -> handler.getVotes().contains(m.getUser().getId())).count();

            int required = (int) Math.ceil(listeners * bot.getSettingsManager().getSettings(event.getGuild()).getSkipRatio());
            msg += skippers + " votes, " + required + "/" + listeners + " required]`";

            // If the required number of votes is different from the number of people in the voice chat
            if (required != listeners) {
                // add message
                msg += "The number of skip requests is " + skippers + ". To skip, " + required + "/" + listeners + "are required.]`";
            } else {
                msg = "";
            }

            // Whether the current number of voters has reached the required number of votes
            if (skippers >= required) {
                msg += "\n" + event.getClient().getSuccess() + "**" + (handler.getPlayer().getPlayingTrack().getInfo().uri.contains("https://stream.gensokyoradio .net/") ? "Gensokyo Radio" : handler.getPlayer().getPlayingTrack().getInfo().title)
                        + "**Skipped." + (rm.getOwner() == 0L ? "(Autoplay)" : "(**" + rm.user.username + "**Requested)");
                handler.getPlayer().stopTrack();
            }
            event.reply(msg);
        }
    }

    @Override
    public void doCommand(SlashCommandEvent event) {
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();

        RequestMetadata rm = handler.getRequestMetadata();
        if (event.getUser().getIdLong() == rm.getOwner()) {
            event.reply(event.getClient().getSuccess() + "**" + (handler.getPlayer().getPlayingTrack().getInfo().uri.contains("https://stream.gensokyoradio.net/" ) ? "Gensokyo Radio" : handler.getPlayer().getPlayingTrack().getInfo().title) + "** Skipped.").queue();
            handler.getPlayer().stopTrack();
        } else {
            // Number of people in voice chat (not including bots and speaker mutes)
            int listeners = (int) event.getGuild().getSelfMember().getVoiceState().getChannel().getMembers().stream()
                    .filter(m -> !m.getUser().isBot() && !m.getVoiceState().isDeafened() && m.getUser().getIdLong() != handler.getRequestMetadata().getOwner()). count();

            // message to send
            String msg;

            // Get the current vote and see if it includes the sender of the message
            if (handler.getVotes().contains(event.getUser().getId())) {
                msg = event.getClient().getWarning() + "The currently playing song has been requested to be skipped. `[";
            } else {
                msg = event.getClient().getSuccess() + "You requested to skip the current song.`[";
                handler.getVotes().add(event.getUser().getId());
            }

            // Get the number of people on voice chat who have voted to skip
            int skippers = (int) event.getGuild().getSelfMember().getVoiceState().getChannel().getMembers().stream()
                    .filter(m -> handler.getVotes().contains(m.getUser().getId())).count();

            // Number of votes required (number of people in voice chat x 0.55)
            int required = (int) Math.ceil(listeners * .55);
            
            // If the required number of votes differs from the number of people in the voice chat
             if (required != listeners) {
                 // add message
                 msg += "The number of skip requests is " + skippers + ". To skip, " + required + "/" + listeners + "are required.]`";
             } else {
                 msg = "";
             }

             // Whether the current number of voters has reached the required number of votes
             if (skippers >= required) {
                 msg += "\n" + event.getClient().getSuccess() + "**" + (handler.getPlayer().getPlayingTrack().getInfo().uri.contains("https://stream.gensokyoradio .net/") ? "Gensokyo Radio" : handler.getPlayer().getPlayingTrack().getInfo().title)
                         + "**Skipped." + (rm.getOwner() == 0L ? "(Autoplay)" : "(**" + rm.user.username + "**Requested)");
                 handler.getPlayer().stopTrack();
             }
             event.reply(msg).queue();
         }
     }
}