package index;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by Thomas Neumann on 19.03.2017.
 */
public class Indexer {
    public static final List<String> DEFAULT_EXCLUDED = new ArrayList<>();
    public static final Map<String, String> DEFAULT_MAPPINGS = new HashMap<>();
    public static final Map<String, String> DEFAULT_PREFIX = new HashMap<>();

    static {
        DEFAULT_EXCLUDED.add(".java");
        DEFAULT_EXCLUDED.add(".jar");
        DEFAULT_EXCLUDED.add(".scmp");

        DEFAULT_MAPPINGS.put(".mp3", "Music");
        DEFAULT_MAPPINGS.put(".ogg", "Music");
        DEFAULT_MAPPINGS.put(".wav", "Music");
        DEFAULT_MAPPINGS.put(".png", "Texture");
        DEFAULT_MAPPINGS.put(".jpg", "Texture");
        DEFAULT_MAPPINGS.put(".ttf", "BitmapFont");
        DEFAULT_MAPPINGS.put(".fnt", "BitmapFont");
        DEFAULT_MAPPINGS.put(".atlas", "TextureAtlas");
        DEFAULT_MAPPINGS.put(".tmx", "TiledMap");

        DEFAULT_PREFIX.put("prototype", "PT_");
    }

    public static final List<String> excluded = new ArrayList<>();
    public static final Map<String, String> mappings = new HashMap<>();
    public static final Map<String, String> prefix = new HashMap<>();

    public Indexer() {
        this(true);
    }

    public Indexer(boolean defaultValues) {
        this(null, null, null, defaultValues);
    }

    public Indexer(List<String> excluded, Map<String, String> mappings, Map<String, String> prefix, boolean defaultValues) {
        if (defaultValues) {
            this.excluded.addAll(DEFAULT_EXCLUDED);
            this.mappings.putAll(DEFAULT_MAPPINGS);
            this.prefix.putAll(DEFAULT_PREFIX);
        }
        if (excluded != null)
            this.excluded.addAll(excluded);
        if (mappings != null)
            this.mappings.putAll(mappings);
        if (prefix != null)
            this.prefix.putAll(prefix);
    }

    private static String getExtension(String name) {
        if (!name.contains(".")) {
            return null;
        }
        return name.substring(name.indexOf("."), name.length());
    }

    private static String getName(String nameWithExtension) {
        if (!nameWithExtension.contains(".")) {
            return nameWithExtension;
        }
        return nameWithExtension.substring(0, nameWithExtension.indexOf("."));
    }

    public void index(File top, File out) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(out));
        List<Line> list;
        appendFiles(top, list = new LinkedList<>(), top.getName() + "/");

        writer.append("public abstract class " + getName(out.getName()) + " {");
        writer.newLine();
        writer.write("\tprivate static BiMap<String, AssetDescriptor> assetMap;\n" +
                "\n" +
                "\tpublic static AssetDescriptor get(String name) {\n" +
                "\t\treturn getAssetMap().get(name);\n" +
                "\t}\n" +
                "\n" +
                "\tpublic static String getName(AssetDescriptor descriptor) {\n" +
                "\t\treturn getAssetMap().inverse().get(descriptor);\n" +
                "\t}\n" +
                "\tprivate static BiMap<String, AssetDescriptor> getAssetMap() {\n" +
                "\t\tif(assetMap != null)\n" +
                "\t\t\treturn assetMap;\n" +
                "\t\telse\n" +
                "\t\t{\n" +
                "\t\t\tassetMap = HashBiMap.create();\n");
        for (Line l : list) {
            boolean useExt = false;
            for (Line lin : list) {
                if (lin != l && lin.equals(l)) {
                    useExt = true;
                }
            }
            String name = useExt ? l.getNameWithExtension() : l.getNameWithoutExtension();
            for (Map.Entry<String, String> en : prefix.entrySet()) {
                if (l.getPath().contains(en.getKey()))
                    name = en.getValue() + name;
            }
            name = name.replaceAll("-", "_");
            writer.write("\t\t\tassetMap.put(\"" + name.toUpperCase() + "\", " + name.toUpperCase() + ");");
            writer.newLine();
        }
        writer.write(
                "\t\t\treturn assetMap;\n" +
                        "\t\t}\n" +
                        "\t}");
        writer.newLine();


        for (Line l : list) {
            boolean useExt = false;
            for (Line lin : list) {
                if (lin != l && lin.equals(l)) {
                    useExt = true;
                }
            }
            String name = useExt ? l.getNameWithExtension() : l.getNameWithoutExtension();
            for (Map.Entry<String, String> en : prefix.entrySet()) {
                if (l.getPath().contains(en.getKey()))
                    name = en.getValue() + name;
            }
            name = name.replaceAll("-", "_");
            String type = getExtensionClass(l.getExtension());
            writer.write("\tpublic static AssetDescriptor<" + type + "> " + name.toUpperCase() + " = new AssetDescriptor<>(\"" + l.getPath() + l.getFullName() + "\", " + type + ".class);");
            writer.newLine();
        }
        writer.append("}");
        writer.flush();
    }

    private String getExtensionClass(String extension) {
        return mappings.getOrDefault(extension, "?");
    }

    private void appendFiles(File dir, List<Line> list, String path) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                appendFiles(file, list, path + file.getName() + "/");
            } else if (isExcluded(getExtension(file.getName()))) {
                list.add(new Line(path, file));
            }
        }
    }

    private boolean isExcluded(String extension) {
        if (extension == null)
            return true;
        return !excluded.contains(extension);
    }


    private static class Line {

        private final String path;
        private final File f;

        public Line(String path, File f) {
            this.path = path;
            this.f = f;
        }

        public String getFullName() {
            return f.getName();
        }

        public String getExtension() {
            if (!f.getName().contains(".")) {
                return null;
            }
            return f.getName().substring(f.getName().indexOf("."), f.getName().length());
        }

        public String getNameWithExtension() {
            return f.getName().replace('.', '_');
        }

        public String getNameWithoutExtension() {
            return f.getName().contains(".") ? f.getName().substring(0, f.getName().indexOf(".")) : f.getName();
        }

        public String getPath() {
            return path;
        }

        public File getF() {
            return f;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Line other = (Line) obj;
            if (this.f.getName().contains(".") && other.f.getName().contains(".")) {
                if (this.f.getName().substring(0, this.f.getName().indexOf('.')).equals(other.f.getName().substring(0, other.f.getName().indexOf('.')))) {
                    return true;
                }
            } else if (!this.f.getName().contains(".") && !other.f.getName().contains(".")) {
                if (this.f.getName().equals(other.f.getName())) {
                    return true;
                }
            } else {
                return false;
            }
            return false;
        }

    }
}
