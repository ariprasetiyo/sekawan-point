package id.sekawan.point.util.mymodel;

public interface Taggable {
    Object getTag(String key);
    void putTag(String key, Object value);
}
