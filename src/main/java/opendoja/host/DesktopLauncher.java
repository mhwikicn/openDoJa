package opendoja.host;

import com.nttdocomo.ui.IApplication;

public final class DesktopLauncher {
    private DesktopLauncher() {
    }

    public static IApplication launch(Class<? extends IApplication> applicationClass, String... args) {
        LaunchConfig config = LaunchConfig.builder(applicationClass)
                .args(args)
                .build();
        return launch(config);
    }

    public static IApplication launch(LaunchConfig config) {
        DoJaRuntime runtime = null;
        boolean launched = false;
        try {
            DoJaRuntime.prepareLaunch(config);
            runtime = DoJaRuntime.bootstrap(config);
            IApplication app = config.applicationClass().getDeclaredConstructor().newInstance();
            runtime.attachApplication(app);
            runtime.startApplication();
            launched = true;
            return app;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to launch " + config.applicationClass().getName(), e);
        } finally {
            if (!launched && runtime != null) {
                runtime.abortLaunch();
            }
            DoJaRuntime.clearPreparedLaunch();
        }
    }

    public static void main(String[] args) throws ClassNotFoundException {
        if (args.length == 0) {
            throw new IllegalArgumentException("Usage: DesktopLauncher <fully.qualified.IApplicationClass> [args...]");
        }
        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        Class<?> rawClass = contextLoader == null
                ? Class.forName(args[0], false, DesktopLauncher.class.getClassLoader())
                : Class.forName(args[0], false, contextLoader);
        if (!IApplication.class.isAssignableFrom(rawClass)) {
            throw new IllegalArgumentException(args[0] + " does not extend com.nttdocomo.ui.IApplication");
        }
        @SuppressWarnings("unchecked")
        Class<? extends IApplication> applicationClass = (Class<? extends IApplication>) rawClass;
        String[] appArgs = new String[Math.max(0, args.length - 1)];
        if (appArgs.length > 0) {
            System.arraycopy(args, 1, appArgs, 0, appArgs.length);
        }
        LaunchConfig config = LaunchConfig.builder(applicationClass)
                .args(appArgs)
                .exitOnShutdown(true)
                .build();
        launch(config);
        DoJaRuntime runtime = DoJaRuntime.current();
        if (runtime != null) {
            try {
                runtime.awaitShutdown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.exit(0);
    }
}
