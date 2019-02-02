package tech.plateau.videodemo;

public class VideoInfo {
    private String url;
    private String firstFrame; // the pause preview
    private int width;
    private int height;

    public VideoInfo(String url, int width, int height, String firstFrame) {
        this.url = url;
        this.firstFrame = firstFrame;
        this.width = width;
        this.height = height;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getFirstFrame() {
        return firstFrame;
    }

    public void setFirstFrame(String firstFrame) {
        this.firstFrame = firstFrame;
    }
}
