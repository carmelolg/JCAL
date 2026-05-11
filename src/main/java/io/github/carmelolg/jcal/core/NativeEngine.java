package io.github.carmelolg.jcal.core;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gateway to the native Rust CA engine bundled in the JAR.
 *
 * <p>On first class-load the static initialiser attempts to load
 * {@code libjcal_rs_ffi} from the following sources, in order:
 * <ol>
 *   <li>Via {@code System.loadLibrary("jcal_rs_ffi")} — honours
 *       {@code java.library.path} (used during development and CI).</li>
 *   <li>By extracting the platform-specific library embedded in the JAR
 *       ({@code /native/<os-arch>/libjcal_rs_ffi.{so,dylib,dll}}) to a
 *       temporary directory and calling {@code System.load()}.</li>
 * </ol>
 *
 * <p>If both attempts fail the native path is disabled and {@link #isAvailable()}
 * returns {@code false}.  All calls to the Java API then fall back silently to
 * the pure-Java execution engine.
 *
 * <p>This class is not instantiable — use it as a static factory for
 * {@link NativeAutomaton} instances.
 *
 * @author Carmelo La Gamba
 * @see NativeRule
 * @see NativeAutomaton
 */
public final class NativeEngine {

    private static final Logger logger = LoggerFactory.getLogger(NativeEngine.class);

    /** {@code true} when the native library was loaded successfully. */
    private static final boolean AVAILABLE;

    static {
        boolean loaded = false;
        try {
            System.loadLibrary("jcal_rs_ffi");
            loaded = true;
            logger.info("jcal_rs_ffi loaded via java.library.path");
        } catch (UnsatisfiedLinkError e) {
            logger.debug("loadLibrary failed, trying embedded resource: {}", e.getMessage());
            try {
                loaded = extractAndLoad();
            } catch (Exception ex) {
                logger.warn("Native Rust engine unavailable: {}", ex.getMessage());
            }
        }
        AVAILABLE = loaded;
    }

    private NativeEngine() {}

    // ─────────────────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns {@code true} when the native Rust engine was loaded successfully.
     *
     * @return availability flag
     */
    public static boolean isAvailable() {
        return AVAILABLE;
    }

    /**
     * Creates a 2-D automaton with the given parameters.
     *
     * @param rows         number of rows
     * @param cols         number of columns
     * @param neighborhood 0 = Moore, 1 = Von Neumann
     * @param ruleId       rule identifier (see {@link NativeRule#getId()})
     * @return a new {@link NativeAutomaton}; caller must {@link NativeAutomaton#close()} it
     */
    public static NativeAutomaton create2d(int rows, int cols, int neighborhood, int ruleId) {
        long h = jcalCreate2d(rows, cols, neighborhood, ruleId);
        return new NativeAutomaton(h, 2, rows * cols);
    }

    /**
     * Creates a 3-D automaton.
     *
     * @param d0           size of first dimension
     * @param d1           size of second dimension
     * @param d2           size of third dimension
     * @param neighborhood 0 = Moore, 1 = Von Neumann
     * @param ruleId       rule identifier
     * @return a new {@link NativeAutomaton}
     */
    public static NativeAutomaton create3d(int d0, int d1, int d2, int neighborhood, int ruleId) {
        long h = jcalCreate3d(d0, d1, d2, neighborhood, ruleId);
        return new NativeAutomaton(h, 3, d0 * d1 * d2);
    }

    /**
     * Creates a 4-D automaton.
     *
     * @param d0           size of first dimension
     * @param d1           size of second dimension
     * @param d2           size of third dimension
     * @param d3           size of fourth dimension
     * @param neighborhood 0 = Moore, 1 = Von Neumann
     * @param ruleId       rule identifier
     * @return a new {@link NativeAutomaton}
     */
    public static NativeAutomaton create4d(int d0, int d1, int d2, int d3, int neighborhood, int ruleId) {
        long h = jcalCreate4d(d0, d1, d2, d3, neighborhood, ruleId);
        return new NativeAutomaton(h, 4, d0 * d1 * d2 * d3);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Package-private dispatch — called by NativeAutomaton
    // ─────────────────────────────────────────────────────────────────────────

    static void initCells(NativeAutomaton a, int[] states) {
        switch (a.getDimensions()) {
            case 2 -> jcalInitCells2d(a.getHandle(), states, states.length);
            case 3 -> jcalInitCells3d(a.getHandle(), states, states.length);
            case 4 -> jcalInitCells4d(a.getHandle(), states, states.length);
            default -> throw new IllegalArgumentException("Unsupported dimensions: " + a.getDimensions());
        }
    }

    static void run(NativeAutomaton a, int steps) {
        switch (a.getDimensions()) {
            case 2 -> jcalRun2d(a.getHandle(), steps);
            case 3 -> jcalRun3d(a.getHandle(), steps);
            case 4 -> jcalRun4d(a.getHandle(), steps);
            default -> throw new IllegalArgumentException("Unsupported dimensions: " + a.getDimensions());
        }
    }

    static void getGrid(NativeAutomaton a, int[] out) {
        switch (a.getDimensions()) {
            case 2 -> jcalGetGrid2d(a.getHandle(), out, out.length);
            case 3 -> jcalGetGrid3d(a.getHandle(), out, out.length);
            case 4 -> jcalGetGrid4d(a.getHandle(), out, out.length);
            default -> throw new IllegalArgumentException("Unsupported dimensions: " + a.getDimensions());
        }
    }

    static void free(NativeAutomaton a) {
        switch (a.getDimensions()) {
            case 2 -> jcalFree2d(a.getHandle());
            case 3 -> jcalFree3d(a.getHandle());
            case 4 -> jcalFree4d(a.getHandle());
            default -> throw new IllegalArgumentException("Unsupported dimensions: " + a.getDimensions());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // JNI declarations — 2-D
    // ─────────────────────────────────────────────────────────────────────────

    private static native long   jcalCreate2d(int rows, int cols, int neighborhood, int ruleId);
    private static native void   jcalInitCells2d(long handle, int[] states, int count);
    private static native void   jcalStep2d(long handle);
    private static native void   jcalRun2d(long handle, int steps);
    private static native void   jcalGetGrid2d(long handle, int[] out, int count);
    private static native void   jcalFree2d(long handle);

    // ─────────────────────────────────────────────────────────────────────────
    // JNI declarations — 3-D
    // ─────────────────────────────────────────────────────────────────────────

    private static native long   jcalCreate3d(int d0, int d1, int d2, int neighborhood, int ruleId);
    private static native void   jcalInitCells3d(long handle, int[] states, int count);
    private static native void   jcalStep3d(long handle);
    private static native void   jcalRun3d(long handle, int steps);
    private static native void   jcalGetGrid3d(long handle, int[] out, int count);
    private static native void   jcalFree3d(long handle);

    // ─────────────────────────────────────────────────────────────────────────
    // JNI declarations — 4-D
    // ─────────────────────────────────────────────────────────────────────────

    private static native long   jcalCreate4d(int d0, int d1, int d2, int d3, int neighborhood, int ruleId);
    private static native void   jcalInitCells4d(long handle, int[] states, int count);
    private static native void   jcalStep4d(long handle);
    private static native void   jcalRun4d(long handle, int steps);
    private static native void   jcalGetGrid4d(long handle, int[] out, int count);
    private static native void   jcalFree4d(long handle);

    // ─────────────────────────────────────────────────────────────────────────
    // Library extraction helper
    // ─────────────────────────────────────────────────────────────────────────

    private static boolean extractAndLoad() throws IOException {
        String resourcePath = nativeResourcePath();
        InputStream is = NativeEngine.class.getResourceAsStream(resourcePath);
        if (is == null) {
            logger.debug("Native resource not found in JAR: {}", resourcePath);
            return false;
        }
        String suffix = resourcePath.substring(resourcePath.lastIndexOf('.'));
        Path tmp = Files.createTempFile("jcal_rs_ffi", suffix);
        tmp.toFile().deleteOnExit();
        Files.copy(is, tmp, StandardCopyOption.REPLACE_EXISTING);
        is.close();
        System.load(tmp.toAbsolutePath().toString());
        logger.info("jcal_rs_ffi loaded from embedded resource: {}", resourcePath);
        return true;
    }

    private static String nativeResourcePath() {
        String os = System.getProperty("os.name", "").toLowerCase();
        String arch = System.getProperty("os.arch", "").toLowerCase();
        String classifier;
        String prefix = "lib";
        String ext;
        if (os.contains("mac") || os.contains("darwin")) {
            classifier = arch.contains("aarch64") || arch.contains("arm") ? "darwin-aarch64" : "darwin-x86_64";
            ext = ".dylib";
        } else if (os.contains("win")) {
            classifier = arch.contains("aarch64") ? "win-aarch64" : "win-x86_64";
            prefix = "";
            ext = ".dll";
        } else {
            classifier = arch.contains("aarch64") ? "linux-aarch64" : "linux-x86_64";
            ext = ".so";
        }
        return "/native/" + classifier + "/" + prefix + "jcal_rs_ffi" + ext;
    }
}
