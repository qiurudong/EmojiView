package app.stevenqiu.emojiview.emoji;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import app.stevenqiu.emojiview.MApplication;

/**
 * Created by Stevenqiu on 2016/8/9.
 */
public enum EmojiFileManager {
    INSTANCE;
    private final String TAG = EmojiFileManager.class.getSimpleName();
    //表情压缩文件在assets中的目录
    private final String EMOJI_ZIP_ASSETS_PATH = "emoji/emoji.zip";
    //表情json文件在assets中的目录
    private final String EMOJI_JSON_ASSETS_PATH = "emoji/emoji.json";
    private final String APP_LOCAL_PATH = MApplication.getAppContext().getFilesDir().getAbsolutePath();
    //表情解压的目录
    private final String EMOJI_UNZIP_LOCAL_PATH = APP_LOCAL_PATH+"/emoji/";
    //表情对应的三个文件夹
    private final String EMOJI_ICON_PATH = "/icon/";
    private final String EMOJI_GIF_PATH = "/gif/";


    public boolean unZipEmoji(String assetName, String outputDirectory, boolean isReWrite){
        try {
            Util.unZipAssetFile(assetName, outputDirectory, isReWrite);
            return true;
        }catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean unzipEmoji(){
        File localEmojiDir = new File(EMOJI_UNZIP_LOCAL_PATH);
        SharedPreferences sp = MApplication.getAppContext().getSharedPreferences(Contsants.SPPATH, Context.MODE_PRIVATE);
        //后期迭代版本如果需要删除默认表情，操作json，增加默认表情则需要更改UNZIP_EMOJI_SUCCESS key，让其重新不覆盖式解压
        if(!localEmojiDir.exists() || !sp.getBoolean(Contsants.SPKEY.UNZIP_EMOJI_SUCCESS, false)){
            boolean unZipSuccess = unZipEmoji(EMOJI_ZIP_ASSETS_PATH, APP_LOCAL_PATH, false);
            if (unZipSuccess) {
                sp.edit().putBoolean(Contsants.SPKEY.UNZIP_EMOJI_SUCCESS, true).apply();
            }
        }
        if(localEmojiDir.exists() && sp.getBoolean(Contsants.SPKEY.UNZIP_EMOJI_SUCCESS, false)){
            Log.i(TAG, "unzip emoji.zip success");
            return true;
        }else{
            Log.i(TAG, "unzip emoji.zip failed");
            return false;
        }
    }

    /**
     * 解析asset中的表情json文件，可以在此处理表情的上下架
     * @return
     */
    public List<EmotionModel> parseEmojiByLocalEmojiJson(){
        EmotionModelList emotionModelList = parseEmojiByAssetsEmojiJson();
        if(Util.isNull(emotionModelList)) return null;
        return emotionModelList.getEmotionModel();
    }


    public EmotionModelList parseEmojiByAssetsEmojiJson(){
        String json = null;
        try {
            InputStream is = MApplication.getAppContext().getAssets().open(EMOJI_JSON_ASSETS_PATH);
            byte [] buffer = new byte[is.available()] ;
            is.read(buffer);
            json = new String(buffer,"utf-8");
            is.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return parseEmojiJson(json);
    }

    public EmotionModelList parseEmojiJson(String json){
        if(Util.isNull(json)){
            return null;
        }
        Gson gson = new Gson();
        EmotionModelList emojiCaseList = gson.fromJson(json,EmotionModelList.class);
        return  emojiCaseList;
    }


    public String getEmojiCasePathByCaseId(long caseId){
        return EMOJI_UNZIP_LOCAL_PATH+caseId;
    }

    public String getEmojiIconPathByCaseId(long caseId){
        return getEmojiCasePathByCaseId(caseId)+EMOJI_ICON_PATH;
    }

    public String getEmojiGifPathByCaseId(long caseId){
        return getEmojiCasePathByCaseId(caseId)+EMOJI_GIF_PATH;
    }


    public String getEmojiIconPath(long caseId, long emojiId){
        return getEmojiIconPathByCaseId(caseId)+emojiId+".png";
    }

    public String getEmojiGifPath(long caseId, long emojiId){
        return getEmojiGifPathByCaseId(caseId)+emojiId+".gif";
    }

    public boolean isEmojiGifFileExists(long caseId, long emojiId){
        return  Util.fileExists(getEmojiGifPath(caseId, emojiId));
    }


    public boolean isEmojiIconFileExists(long caseId, long emojiId){
        return  Util.fileExists(getEmojiIconPath(caseId, emojiId));
    }
}

