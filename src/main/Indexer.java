/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author AsterAether
 */
public class Indexer {

    public static void main(String[] args) throws IOException {
        File curr = new File(System.getProperty("user.dir"));
        BufferedWriter writer = new BufferedWriter(new FileWriter(curr + File.separator + "Assets.java"));
        writer.append("public interface Assets {");
        writer.newLine();
        List<Line> list;
        appendFiles(curr, list = new LinkedList<Line>(), curr.getName() + "/");
        for (Line l : list) {
            boolean useExt = false;
            for (Line lin : list) {
                if (lin != l && lin.equals(l)) {
                    useExt = true;
                }
            }
            String name = useExt ? l.getNameWithExtension() : l.getNameWithoutExtension();
            String type = getExtensionClass(l.getExtension());
            writer.write("\tAssetDescriptor<" + type + "> " + name.toUpperCase() + " = new AssetDescriptor<>(\"" + l.getPath() + l.getFullName() + "\", " + type + ".class);");
            writer.newLine();
        }
        writer.append("}");
        writer.flush();
    }

    private static String getExtensionClass(String extension) {
        switch (extension) {
            case ".mp3":
                return "Music";
            case ".png":
            case ".jpg":
                return "Texture";
            case ".fnt":
                return "BitmapFont";
            case ".tmx":
                return "TiledMap";
            default:
                return "?";
        }
    }

    private static void appendFiles(File dir, List<Line> list, String path) throws IOException {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                appendFiles(file, list, path + file.getName() + "/");
            } else if (!file.getName().substring(0, file.getName().indexOf(".")).isEmpty() && !file.getName().contains(".java") && !file.getName().contains(".jar")) {
                list.add(new Line(path, file));
            }
        }
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
