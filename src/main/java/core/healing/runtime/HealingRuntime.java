package core.healing.runtime;

import core.healing.HealingConfig;
import core.healing.application.port.HealingMonitor;
import core.healing.domain.HealingEngine;
import core.healing.domain.port.GoldenStateStore;
import core.healing.domain.strategy.*;
import core.healing.infrastructure.golden.GoldenStateRecorder;
import core.healing.infrastructure.golden.embedding.DefaultEmbeddingGenerator;
import core.healing.infrastructure.golden.extractor.JsDomSnapshotExtractor;
import core.healing.infrastructure.golden.repository.JsonFileGoldenStateRepository;
import core.healing.infrastructure.golden.visual.DefaultVisualSnapshotService;
import core.healing.infrastructure.monitor.CompositeHealingMonitor;
import core.healing.infrastructure.monitor.HtmlHealingMonitor;
import core.healing.infrastructure.monitor.LogHealingMonitor;
import core.healing.infrastructure.rag.RagGoldenStateStore;
import core.healing.rag.EmbeddingService;
import core.platform.utils.Logger;

import java.util.ArrayList;
import java.util.List;

public final class HealingRuntime {

    private static HealingRuntime INSTANCE;

    private final HealingMonitor monitor;
    private final GoldenStateStore goldenStateStore;
    private final HealingEngine engine;

    private HealingRuntime() {

        // --- Golden State ---
        JsonFileGoldenStateRepository repository = new JsonFileGoldenStateRepository("element-metadata.json");
        GoldenStateRecorder recorder = new GoldenStateRecorder(
                new JsDomSnapshotExtractor(),
                new DefaultEmbeddingGenerator(),
                new DefaultVisualSnapshotService(),
                repository
        );

        this.goldenStateStore = new RagGoldenStateStore(recorder, repository);

        // --- Engine ---
        HealingStrategyRegistry registry = new HealingStrategyRegistry();
        registry.registerAll(goldenStateStore);
        boolean parallel = "PARALLEL".equalsIgnoreCase(HealingConfig.getInstance().getExecutionMode());
        this.engine = new HealingEngine(registry.ordered(), parallel);

        // --- Monitor ---
        this.monitor = new CompositeHealingMonitor(
                List.of(new LogHealingMonitor(), new HtmlHealingMonitor("target/healing-report.html"))
        );

    }

    public static synchronized HealingRuntime start() {
        if (INSTANCE == null) {
            INSTANCE = new HealingRuntime();
        }
        return INSTANCE;
    }


    public static void end() {
        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> get().monitor().onFinish())
        );
    }


    public static HealingRuntime get() {
        if (INSTANCE == null) {
            throw new IllegalStateException(
                    "HealingRuntime not started. Call HealingRuntime.start()");
        }
        return INSTANCE;
    }

    public HealingMonitor monitor() {
        return monitor;
    }

    public GoldenStateStore goldenStateStore() {
        return goldenStateStore;
    }

    public HealingEngine engine() {
        return engine;
    }


}
