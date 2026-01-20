package core.healing.runtime;

import core.healing.application.locator.LocatorMapper;
import core.healing.application.port.HealingMonitor;
import core.healing.domain.HealingEngine;
import core.healing.application.port.GoldenStateStore;
import core.healing.domain.strategy.HealingStrategy;
import core.healing.infrastructure.config.HealingConfig;
import core.healing.infrastructure.golden.GoldenStateRecorder;
import core.healing.infrastructure.embedding.DefaultEmbeddingGenerator;
import core.healing.infrastructure.golden.extractor.JsDomSnapshotExtractor;
import core.healing.infrastructure.golden.repository.JsonFileGoldenStateRepository;
import core.healing.infrastructure.golden.visual.DefaultVisualSnapshotService;
import core.healing.infrastructure.locator.LocatorRepository;
import core.healing.infrastructure.adapter.monitor.CompositeHealingMonitor;
import core.healing.infrastructure.adapter.monitor.HtmlHealingMonitor;
import core.healing.infrastructure.adapter.monitor.LogHealingMonitor;
import core.healing.infrastructure.adapter.RagGoldenStateStore;
import core.healing.infrastructure.embedding.EmbeddingService;
import core.healing.application.port.VectorStoreAdapter;
import core.healing.infrastructure.embedding.LangChainVectorStore;
import core.platform.utils.Logger;

import java.io.File;
import java.util.List;

public final class HealingRuntime {

    private static HealingRuntime INSTANCE;
    private HealingMonitor monitor;
    private GoldenStateStore goldenStateStore;
    private HealingEngine engine;
    private VectorStoreAdapter vectorStore;


    private HealingRuntime() {
        HealingConfig config = HealingConfig.getInstance();
        initLocatorInfrastructure(config);
        this.vectorStore = initVectorStore(config);
        this.goldenStateStore = initGoldenStateStore();
        this.engine = initHealingEngine(config);
        this.monitor = initMonitoring();
    }

    public static synchronized HealingRuntime start() {
        if (INSTANCE == null) {
            INSTANCE = new HealingRuntime();
            Runtime.getRuntime().addShutdownHook(
                    new Thread(() -> INSTANCE.monitor.onFinish())
            );
        }
        return INSTANCE;
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

    public VectorStoreAdapter vectorStore() {
        return vectorStore;
    }

    private void initLocatorInfrastructure(HealingConfig config) {

        File dir = new File(config.getLocatorPath());
        if (!dir.exists()) {
            Logger.warn("[Healing] Locator path not found: %s", config.getLocatorPath());
            return;
        }

        LocatorRepository repo = LocatorRepository.getInstance();
        int count = scan(dir, repo);
        LocatorMapper.getInstance().buildIndex();

        Logger.info("[Healing] Loaded %d locator files", count);
    }

    private HealingMonitor initMonitoring() {
        return new CompositeHealingMonitor(
                List.of(new LogHealingMonitor(), new HtmlHealingMonitor("target/healing-report.html"))
        );
    }

    private HealingEngine initHealingEngine(HealingConfig config) {
        // --- Engine ---
        HealingStrategyRegistry registry = new HealingStrategyRegistry();
        HealingStrategyFactory factory = new HealingStrategyFactory(goldenStateStore, EmbeddingService.getInstance(), vectorStore);
        List<String> strategyNames = config.getStrategies();
        if (strategyNames == null || strategyNames.isEmpty()) {
            strategyNames = List.of(
                    "ExactAttributeStrategy",
                    "KeyBasedStrategy",
                    "TextBasedStrategy",
                    "CrossAttributeStrategy",
                    "SemanticValueStrategy",
                    "StructuralStrategy",
                    "NeighborStrategy",
                    "RagHealingStrategy"
            );
        }

        for (String name : strategyNames) {
            HealingStrategy s = factory.create(name);
            if (s != null) registry.register(s);
        }

        boolean parallel = "PARALLEL".equalsIgnoreCase(config.getExecutionMode());
        return new HealingEngine(registry.ordered(), parallel, config.getMaxHealingThreads());
    }

    private VectorStoreAdapter initVectorStore(HealingConfig config) {
        if (config.isVectorMemoryEnabled()) {
            Logger.info("[Healing] Vector Memory enabled. Initializing LangChainVectorStore.");
            return new LangChainVectorStore();
        }
        return null;
    }
    private GoldenStateStore initGoldenStateStore() {
        // --- Golden State ---
        JsonFileGoldenStateRepository repository = new JsonFileGoldenStateRepository("element-metadata.json");
        GoldenStateRecorder recorder = new GoldenStateRecorder(
                new JsDomSnapshotExtractor(),
                new DefaultEmbeddingGenerator(),
                new DefaultVisualSnapshotService(),
                repository
        );

        return new RagGoldenStateStore(recorder, repository);
    }

    private int scan(File dir, LocatorRepository repo) {
        int count = 0;
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) count += scan(f, repo);
            else if (f.getName().endsWith(".json")) {
                repo.loadFromFile(f.getAbsolutePath());
                count++;
            }
        }
        return count;
    }


}
