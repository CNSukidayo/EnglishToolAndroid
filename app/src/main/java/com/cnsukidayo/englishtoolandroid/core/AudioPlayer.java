package com.cnsukidayo.englishtoolandroid.core;

import java.io.File;
@Deprecated()
public interface AudioPlayer {
    /**
     * 播放音效(根据路径名称来播放音效)
     *
     * @param path 路径的字符串
     */
    void play(String path);

    /**
     * 播放音效(根据路径的File来播放音效_
     *
     * @param path File对象
     */
    void play(File path);

    /**
     * 暂停播放所有
     */
    void stop();

    /**
     * 销毁
     */
    void destroy();

}
