package bot;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Random;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class MemeCommands
implements CommandExecutor{

	private Properties memes;
	private Properties memeImgs;
	private static final String HOST_RAW_URL = "https://raw.githubusercontent.com/MCOfficer/EndlessSky-Discord-Bot/master";

	private ESBot bot;

	public MemeCommands(ESBot bot){
		this.bot = bot;
		readMemes();
	}

	@Command(aliases = {"-meme"}, description = "Posts meme X, or a random Endless Sky meme if no X is given.", usage = "-meme [X]", privateMessages = true)
	public void onMemeCommand(Guild guild, MessageChannel channel, String[] args){
		if(args.length == 0){
			channel.sendMessage(getRandomMeme()).queue();
		}
		else if(args.length == 1){
			if(!isImgMeme(args[0])){
				channel.sendMessage(getMeme(args[0])).queue();
			}
			else{
				String path = HOST_RAW_URL + "/data/memes/" + getImgMemePath(args[0]);
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(guild.getMember(bot.getSelf()).getColor());
				eb.setImage(path);
				channel.sendMessage(eb.build()).queue();
			}
		}
	}

	@Command(aliases = {"-memelist", "-memes", "-memeList"}, description = "PMs you the current list of memes.", usage = "-memelist", privateMessages = true)
	public void onListmemesCommand(Guild guild, User user, MessageChannel channel, Message message, String[] args){
		if(args.length == 0){
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("Available Memes:", "https://github.com/MCOfficer/EndlessSky-Discord-Bot/tree/master/data");
			eb.addField("Text-based Memes", getMemelist(), false);
			eb.addField("Image-based Memes", getMemelistImgs(), false);
			eb.setThumbnail(HOST_RAW_URL + "/thumbnails/meme.png");
			eb.setColor(guild.getMember(bot.getSelf()).getColor());
			user.openPrivateChannel().queue(c -> {
				c.sendMessage(eb.build()).queue();
			});
			if(channel instanceof TextChannel){
				message.delete().queue();
			}
		}
	}

	private void readMemes(){
		Properties memes = new Properties();
		try{
			memes.load(new URL(HOST_RAW_URL + "/data/memes.txt").openStream());
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		this.memes = memes;
		memes = new Properties();
		try{
			memes.load(new URL(HOST_RAW_URL + "/data/memeImgs.txt").openStream());
		}
		catch (FileNotFoundException e){
			e.printStackTrace();
		}
		catch (IOException e){
			e.printStackTrace();
		}
		this.memeImgs = memes;
	}

	private boolean isImgMeme(String key){
		return memeImgs.containsKey(key);
	}

	private String getMeme(String meme){
		return memes.getProperty(meme, "Please don't joke about that sort of thing.");
	}

	private String getRandomMeme(){
		Enumeration<?> keys = memes.propertyNames();
		Random rGen = new Random();
		int random = rGen.nextInt(memes.size());
		String key = (String) keys.nextElement();
		for(int i = 0; i < random; ++i){
			key = (String) keys.nextElement();
		}
		return memes.getProperty(key);
	}

	private String getImgMemePath(String string){
		return memeImgs.getProperty(string);
	}

	private String getMemelist(){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		memes.list(ps);
		ps.close();
		String output = new String(baos.toByteArray(), java.nio.charset.StandardCharsets.UTF_8);
		return output.substring(output.indexOf('\n'));
	}

	private String getMemelistImgs(){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		memeImgs.list(ps);
		ps.close();
		String output = new String(baos.toByteArray(), java.nio.charset.StandardCharsets.UTF_8);
		return output.substring(output.indexOf('\n'));
	}
}
