package com.lib.common.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SPUtils {

    @IntDef({EditorType.INT, EditorType.STRING, EditorType.FLOAT, EditorType.LONG, EditorType.BOOLEAN, EditorType.CLEAR, EditorType.REMOVE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EditorType {
        int INT = 1;
        int STRING = 2;
        int FLOAT = 3;
        int LONG = 4;
        int BOOLEAN = 5;
        int CLEAR = 6;
        int REMOVE = 7;
    }

    public static class Holder {
        private static final SPUtils INSTANCE = new SPUtils();
    }

    public static SPUtils getInstance() {
        return Holder.INSTANCE;
    }

    private static final boolean USE_THREAD_POOL = true;
    private ConcurrentHashMap<String, SharedPreferences> mSPSources = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Object>> mCacheMaps = new ConcurrentHashMap<>();
    private ExecutorService mExecutorService;

    private SPUtils() {
        mExecutorService = Executors.newSingleThreadExecutor();
    }

    public int getInt(String fileName, String key, int defaultValue) {
        ConcurrentHashMap<String, Object> cacheMap = getCache(fileName);
        Integer cacheValue = null;
        if (cacheMap != null) {
            cacheValue = (Integer) cacheMap.get(key);
        }
        if (cacheValue == null || !useThreadPool()) {
            SharedPreferences source = getSource(fileName);
            return source != null ? source.getInt(key, defaultValue) : defaultValue;
        } else {
            return cacheValue;
        }
    }

    public boolean getBoolean(String fileName, String key, boolean defaultValue) {
        ConcurrentHashMap<String, Object> cacheMap = getCache(fileName);
        Boolean cacheValue = null;
        if (cacheMap != null) {
            cacheValue = (Boolean) cacheMap.get(key);
        }
        if (cacheValue == null || !useThreadPool()) {
            SharedPreferences source = getSource(fileName);
            return source != null ? source.getBoolean(key, defaultValue) : defaultValue;
        } else {
            return cacheValue;
        }
    }

    public String getString(String fileName, String key, String defaultValue) {
        ConcurrentHashMap<String, Object> cacheMap = getCache(fileName);
        String cacheValue = null;
        if (cacheMap != null) {
            cacheValue = (String) cacheMap.get(key);;
        }
        if (cacheValue == null || !useThreadPool()) {
            SharedPreferences source = getSource(fileName);
            return source != null ? source.getString(key, defaultValue) : defaultValue;
        } else {
            return cacheValue;
        }
    }

    public long getLong(String fileName, String key, long defaultValue) {
        ConcurrentHashMap<String, Object> cacheMap = getCache(fileName);
        Long cacheValue = null;
        if (cacheMap != null) {
            cacheValue = (Long) cacheMap.get(key);
        }
        if (cacheValue == null || !useThreadPool()) {
            SharedPreferences source = getSource(fileName);
            return source != null ? source.getLong(key, defaultValue) : defaultValue;
        } else {
            return cacheValue;
        }
    }

    public float getFloat(String fileName, String key, float defaultValue) {
        ConcurrentHashMap<String, Object> cacheMap = getCache(fileName);
        Float cacheValue = null;
        if (cacheMap != null) {
            cacheValue = (Float) cacheMap.get(key);
        }
        if (cacheValue == null || !useThreadPool()) {
            SharedPreferences source = getSource(fileName);
            return source != null ? source.getFloat(key, defaultValue) : defaultValue;
        } else {
            return cacheValue;
        }
    }

    public void putInt(String fileName, String key, int value) {
        doRealWork(fileName, new SpEditor.Builder().putInt(key, value).build());
    }

    public void putBoolean(String fileName, String key, boolean value) {
        doRealWork(fileName, new SpEditor.Builder().putBoolean(key, value).build());
    }

    public void putString(String fileName, String key, String value) {
        doRealWork(fileName, new SpEditor.Builder().putString(key, value).build());
    }

    public void putLong(String fileName, String key, long value) {
        doRealWork(fileName, new SpEditor.Builder().putLong(key, value).build());
    }

    public void putFloat(String fileName, String key, float value) {
        doRealWork(fileName, new SpEditor.Builder().putFloat(key, value).build());
    }

    public void remove(String fileName, String key) {
        doRealWork(fileName, new SpEditor.Builder().remove(key).build());
    }

    public void clear(String fileName) {
        doRealWork(fileName, new SpEditor.Builder().clear().build());
    }

    public boolean contains(String fileName, String key) {
        ConcurrentHashMap<String, Object> cacheMap = getCache(fileName);
        if (useThreadPool() && cacheMap != null && cacheMap.get(key) != null) {
            return true;
        } else {
            SharedPreferences source = getSource(fileName);
            return source != null && source.contains(key);
        }
    }

    public void doRealWork(String fileName, SpEditor spEditor) {
        SharedPreferences source = getSource(fileName);
        SharedPreferences.Editor editor = source.edit();
        ConcurrentHashMap<String, Object> cacheMap = getCache(fileName);
        for (SpEditor.Value value : spEditor.getValues()) {
            switch (value.editorType) {
                case EditorType.INT:
                    if (cacheMap != null) {
                        cacheMap.put(value.key, value.value);
                    }
                    editor.putInt(value.key, (Integer) value.value);
                    break;
                case EditorType.STRING:
                    if (cacheMap != null) {
                        if (value.value != null) {
                            cacheMap.put(value.key, value.value);
                        } else {
                            cacheMap.remove(value.key);
                        }
                    }
                    editor.putString(value.key, (String) value.value);
                    break;
                case EditorType.FLOAT:
                    if (cacheMap != null) {
                        cacheMap.put(value.key, value.value);
                    }
                    editor.putFloat(value.key, (Float) value.value);
                    break;
                case EditorType.LONG:
                    if (cacheMap != null) {
                        cacheMap.put(value.key, value.value);
                    }
                    editor.putLong(value.key, (Long) value.value);
                    break;
                case EditorType.BOOLEAN:
                    if (cacheMap != null) {
                        cacheMap.put(value.key, value.value);
                    }
                    editor.putBoolean(value.key, (Boolean) value.value);
                    break;
                case EditorType.REMOVE:
                    if (cacheMap != null) {
                        cacheMap.remove(value.key);
                    }
                    editor.remove(value.key);
                    break;
                case EditorType.CLEAR:
                    mCacheMaps.remove(fileName);
                    editor.clear();
                    break;
                default:
                    break;
            }
        }
        doEditorWork(editor);
    }

    private void doEditorWork(SharedPreferences.Editor editor) {
        if (useThreadPool()) {
            mExecutorService.execute(new CommitRunnable(editor));
        } else {
            editor.apply();
        }
    }

    private ConcurrentHashMap<String, Object> getCache(String fileName) {
        ConcurrentHashMap<String, Object> cacheMap = mCacheMaps.get(fileName);
        if (cacheMap == null) {
            cacheMap = new ConcurrentHashMap<>();
            mCacheMaps.put(fileName, cacheMap);
        }
        return cacheMap;
    }

    private SharedPreferences getSource(String fileName) {
        SharedPreferences source = mSPSources.get(fileName);
        if (source == null) {
            source = createSource(fileName);
            mSPSources.put(fileName, source);
        }
        return source;
    }

    private SharedPreferences createSource(String fileName) {
        return Utils.getApp().getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    private boolean useThreadPool() {
        return USE_THREAD_POOL;
    }

    private class CommitRunnable implements Runnable {

        private SharedPreferences.Editor editor;

        CommitRunnable(SharedPreferences.Editor editor) {
            this.editor = editor;
        }

        @Override
        public void run() {
            if (editor != null) {
                editor.commit();
            }
        }
    }

    public static class SpEditor {

        private List<Value> values = new ArrayList<>();

        SpEditor(SpEditor.Builder builder) {
            this.values = builder.values;
        }

        List<Value> getValues() {
            return values;
        }

        public static class Builder {

            private List<Value> values = new ArrayList<>();

            public Builder putInt(String key, int value) {
                values.add(new Value(EditorType.INT, key, value));
                return this;
            }

            public Builder putString(String key, String value) {
                values.add(new Value(EditorType.STRING, key, value));
                return this;
            }

            public Builder putFloat(String key, float value) {
                values.add(new Value(EditorType.FLOAT, key, value));
                return this;
            }

            public Builder putLong(String key, long value) {
                values.add(new Value(EditorType.LONG, key, value));
                return this;
            }

            public Builder putBoolean(String key, boolean value) {
                values.add(new Value(EditorType.BOOLEAN, key, value));
                return this;
            }

            public Builder remove(String key) {
                values.add(new Value(EditorType.REMOVE, key, null));
                return this;
            }

            public Builder clear() {
                values.add(new Value(EditorType.CLEAR, null, null));
                return this;
            }

            public SpEditor build() {
                return new SpEditor(this);
            }
        }

        private static class Value {

            @EditorType int editorType;
            String key;
            Object value;

            Value(@EditorType int editorType, String key, Object value) {
                this.editorType = editorType;
                this.key = key;
                this.value = value;
            }
        }
    }
}
