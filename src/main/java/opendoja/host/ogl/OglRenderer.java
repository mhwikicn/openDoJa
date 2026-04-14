package opendoja.host.ogl;

public final class OglRenderer {
    private final AcrodeaOglMatrixExtension acrodeaMatrixExtension = new AcrodeaOglMatrixExtension();

    public boolean acceptsExtensionMatrixMode(int mode) {
        return acrodeaMatrixExtension.acceptsMatrixMode(mode);
    }

    public void beginDrawing() {
        acrodeaMatrixExtension.beginDrawing();
    }

    public boolean usesExtensionMatrices(boolean standardModelViewConfigured, boolean standardProjectionConfigured) {
        return acrodeaMatrixExtension.usesMatrices(standardModelViewConfigured, standardProjectionConfigured);
    }

    public float[] extensionWorldMatrix() {
        return acrodeaMatrixExtension.worldMatrix();
    }

    public float[] extensionCameraMatrix() {
        return acrodeaMatrixExtension.cameraMatrix();
    }

    public void loadExtensionMatrix(int mode, float[] matrix) {
        acrodeaMatrixExtension.loadMatrix(mode, matrix);
    }

    public void multiplyExtensionMatrix(int mode, float[] matrix) {
        acrodeaMatrixExtension.multiplyMatrix(mode, matrix);
    }

    public void pushExtensionMatrix(int mode) {
        acrodeaMatrixExtension.pushMatrix(mode);
    }

    public boolean popExtensionMatrix(int mode) {
        return acrodeaMatrixExtension.popMatrix(mode);
    }
}
