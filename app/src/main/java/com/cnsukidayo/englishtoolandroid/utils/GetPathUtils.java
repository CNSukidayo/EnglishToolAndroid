package com.cnsukidayo.englishtoolandroid.utils;

import android.content.Context;
import android.os.storage.StorageManager;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;

public class GetPathUtils {

    /**
     * @param mContext     上下文
     * @param is_removable true:外部存储 false:内部存储
     * @return 返回根路径
     */
    public static String getStoragePath(Context mContext, boolean is_removable) {
        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;

        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Method getState = storageVolumeClazz.getMethod("getState");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                String state = (String) getState.invoke(storageVolumeElement);

                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);

                if (is_removable == removable) {
                    return path;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }



}
