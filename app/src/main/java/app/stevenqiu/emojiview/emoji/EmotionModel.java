package app.stevenqiu.emojiview.emoji;

import java.util.List;

/**
 * Created by Stevenqiu on 2016/8/9.
 */

public class EmotionModel {
    private long modelId;
    private List<Emotion> emotion;
    public long getModelId() {
        return modelId;
    }

    public void setModelId(long modelId) {
        this.modelId = modelId;
    }

    public List<Emotion> getEmotion() {
        return emotion;
    }

    public void setEmotion(List<Emotion> emotion) {
        this.emotion = emotion;
    }

}





