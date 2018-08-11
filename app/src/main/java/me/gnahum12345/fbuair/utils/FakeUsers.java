package me.gnahum12345.fbuair.utils;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.models.SocialMedia;
import me.gnahum12345.fbuair.models.User;

import me.gnahum12345.fbuair.managers.MyUserManager;

import static me.gnahum12345.fbuair.utils.ImageUtils.drawableToBitmap;
import static me.gnahum12345.fbuair.utils.Utils.dateFormatter;

public class FakeUsers {

    // placeholder users to add
/*    public User katherine = new User();
    public User raul = new User();
    public User sanura = new User();
    public User vanessa = new User();
    public User gabriella = new User();
    public User emma = new User();
    public User martha = new User();
    public User jacob = new User();
    public User david = new User();
    public User girum = new User();
    public User nneka = new User();
    public User natalie = new User();
    public User mackenzie = new User();
    public User leonardo = new User();*/

    public List<User> fakeUsersList = new ArrayList<>();

    Context context;

    public FakeUsers(Context context) {
        this.context = context;
        setUserInfo();
    }

    public void setFakeHistory() {
        for (User user : fakeUsersList) {
            MyUserManager.getInstance().addFakeUser(user);
        }
    }

    private void setUserInfo() {
        User katherine = new User();
        katherine.setId();
        katherine.setName("Katherine Lazar");
        katherine.setOrganization("Facebook");
        katherine.setPhoneNumber("2328394839");
        katherine.setEmail("katherinelazar@gmail.com");
        katherine.setNumConnections(38);
        setProfileImage(katherine, R.drawable.photo_katherine);
        katherine.setTimeAddedToHistory(dateFormatter.format(Calendar.getInstance().getTime()));

        SocialMedia facebookKatherine = new SocialMedia();
        facebookKatherine.setName("Facebook");
        facebookKatherine.setUsername(katherine.getName());
        facebookKatherine.setProfileUrl("https://www.facebook.com/katherine.lazar1");
        katherine.addSocialMedia(facebookKatherine);

        SocialMedia githubKatherine = new SocialMedia();
        githubKatherine.setName("Github");
        githubKatherine.setUsername("katherinelazar");
        katherine.addSocialMedia(githubKatherine);

        SocialMedia linkedInKatherine = new SocialMedia();
        linkedInKatherine.setName("LinkedIn");
        linkedInKatherine.setUsername(katherine.getName());
        linkedInKatherine.setProfileUrl("https://www.linkedin.com/in/katherineelenalazar/");
        katherine.addSocialMedia(linkedInKatherine);
        fakeUsersList.add(katherine);

        User raul = new User();
        raul.setId();
        raul.setName("Raul Dagir");
        raul.setPhoneNumber("2328394839");
        raul.setEmail("raul234@gmail.com");
        raul.setNumConnections(76);
        setProfileImage(raul, R.drawable.photo_raul);

        Calendar calendar = Calendar.getInstance();
        calendar.set(2018, 7, 28, 0, 0);
        raul.setTimeAddedToHistory(dateFormatter.format(calendar.getTime()));

        SocialMedia facebookRaul = new SocialMedia();
        facebookRaul.setName("Facebook");
        facebookRaul.setUsername(raul.getName());
        facebookRaul.setProfileUrl("https://www.facebook.com/raul.gd.35");
        raul.addSocialMedia(facebookRaul);

        SocialMedia instagramRaul = new SocialMedia();
        instagramRaul.setName("Instagram");
        instagramRaul.setUsername("raullgd");
        raul.addSocialMedia(instagramRaul);

        SocialMedia snapchatRaul = new SocialMedia();
        snapchatRaul.setName("Snapchat");
        snapchatRaul.setUsername("raullgd");
        raul.addSocialMedia(snapchatRaul);

        SocialMedia soundcloudRaul = new SocialMedia();
        soundcloudRaul.setName("Soundcloud");
        soundcloudRaul.setUsername("Raul GD");
        soundcloudRaul.setProfileUrl("https://soundcloud.com/raul-gd");
        raul.addSocialMedia(soundcloudRaul);
        fakeUsersList.add(raul);
    }

    // sets profile image from resource drawable id
    void setProfileImage(User user, int drawableId) {
        user.setProfileImage(BitmapFactory.decodeResource(context.getResources(), drawableId));
    }

    // sets default profile image
    void setProfileImage(User user) {
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getRandomColor();
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(Character.toString(user.getName().toCharArray()[0]).toUpperCase(),
                        color);
        user.setProfileImage(drawableToBitmap(drawable));
        user.setColor(color);
    }
}
