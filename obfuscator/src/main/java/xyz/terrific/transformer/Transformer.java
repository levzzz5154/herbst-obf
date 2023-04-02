package xyz.terrific.transformer;

public abstract class Transformer {
    protected boolean isJarFile;

    public Transformer(Boolean isJarFile) {
        this.isJarFile = isJarFile;
    }

    public abstract void transform();
}
