package app.stevenqiu.emojiview;

import android.app.Activity;
import android.os.Bundle;

import app.stevenqiu.emojiview.emoji.EmojiFileManager;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EmojiFileManager.INSTANCE.unzipEmoji();
        setContentView(R.layout.activity_main);
    }
}
