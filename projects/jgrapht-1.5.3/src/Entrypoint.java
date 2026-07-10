
import org.jgrapht.*;
import org.jgrapht.alg.clique.*;
import org.jgrapht.alg.clustering.*;
import org.jgrapht.alg.color.*;
import org.jgrapht.alg.connectivity.*;
import org.jgrapht.alg.cycle.*;
import org.jgrapht.alg.densesubgraph.*;
import org.jgrapht.alg.drawing.*;
import org.jgrapht.alg.drawing.model.*;
import org.jgrapht.alg.flow.*;
import org.jgrapht.alg.interfaces.*;
import org.jgrapht.alg.lca.*;
import org.jgrapht.alg.linkprediction.*;
import org.jgrapht.alg.isomorphism.*;
import org.jgrapht.alg.matching.*;
import org.jgrapht.alg.matching.blossom.v5.*;
import org.jgrapht.alg.partition.*;
import org.jgrapht.alg.planar.*;
import org.jgrapht.alg.scoring.*;
import org.jgrapht.alg.shortestpath.*;
import org.jgrapht.alg.similarity.*;
import org.jgrapht.alg.spanning.*;
import org.jgrapht.alg.tour.*;
import org.jgrapht.alg.vertexcover.*;
import org.jgrapht.alg.util.*;
import org.jgrapht.generate.*;
import org.jgrapht.generate.netgen.*;
import org.jgrapht.graph.*;
import org.jgrapht.util.*;

import java.util.*;
import java.util.function.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class Entrypoint {
    private static ThreadPoolExecutor executor;

    public static void entrypoint() throws Exception {
        List<AbstractBaseGraph<Integer, DefaultEdge>> graphs = new ArrayList<>();
        graphs.addAll(generateGraphs(() -> new DefaultUndirectedGraph(SupplierUtil.createIntegerSupplier(), SupplierUtil.createDefaultEdgeSupplier(), false)));
        graphs.addAll(generateGraphs(() -> new DefaultUndirectedWeightedGraph(SupplierUtil.createIntegerSupplier(), SupplierUtil.createDefaultEdgeSupplier())));
        graphs.addAll(generateGraphs(() -> new SimpleGraph(SupplierUtil.createIntegerSupplier(), SupplierUtil.createDefaultEdgeSupplier(), true)));
        graphs.addAll(generateGraphs(() -> new SimpleDirectedWeightedGraph(SupplierUtil.createIntegerSupplier(), SupplierUtil.createDefaultEdgeSupplier())));
        graphs.addAll(generateGraphs(() -> new DirectedAcyclicGraph(SupplierUtil.createIntegerSupplier(), SupplierUtil.createDefaultEdgeSupplier(), true)));

        Graph<Integer, DefaultEdge> undirectedTree = new AsUndirectedGraph<>(graphs.getLast());

        for(AbstractBaseGraph<Integer, DefaultEdge> graph: graphs) {
            Set<Integer> vertices = graph.vertexSet();
            Integer min = Collections.min(vertices);
            Integer max = Collections.max(vertices);

            clique(graph);
            clustering(graph);
            connectivity(graph);
            color(graph);
            cycle(graph);
            denseSubgraph(graph, max + 1, max + 2);
            drawing(graph);
            flow(graph, min, max);
            lca(graph, min, max);
            linkPrediction(graph, min, max);
            isomorphism(graph, (Graph<Integer, DefaultEdge>)graph.clone());
            matching(graph);
            planar(graph);
            scoring(graph, min);
            shortestPath(graph, min, max);
            similarity(graph, min, undirectedTree, Collections.min(undirectedTree.vertexSet()));
            spanning(graph, min);
            tour(graph);
            vertexCover(graph);
        }

        try {
          Graph<String, DefaultEdge> pyramid = new SimpleGraph<>(DefaultEdge.class);

          pyramid.addVertex("A");
          pyramid.addVertex("B");
          pyramid.addVertex("C");
          pyramid.addVertex("D");
          pyramid.addVertex("E");
          pyramid.addEdge("A", "B");
          pyramid.addEdge("B", "C");
          pyramid.addEdge("C", "D");
          pyramid.addEdge("D", "E");
          pyramid.addEdge("E", "A");
          pyramid.addEdge("A", "C");
          pyramid.addEdge("A", "D");
          BergeGraphInspector<String,DefaultEdge> inspector = new BergeGraphInspector<>();
          inspector.isBerge(pyramid, true);
          inspector.getCertificate();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

    }

//    public static void demos() {
//        try {
//            CompleteGraphDemo.main(new String[]{});
//            DependencyDemo.test(false);
//            DependencyDemo.test(true);
//            DirectedGraphDemo.main(new String[]{});
//            GraphBuilderDemo.main(new String[]{});
//            HelloJGraphT.main(new String[]{});
//            LabeledEdges.main(new String[]{});
//            ParberryKnightTour parberryTour = new ParberryKnightTour(6, 6);
//            parberryTour.getTour();
//
//        } catch(Throwable exception) {
//            exception.printStackTrace(System.err);
//        }
//    }

    public static List<AbstractBaseGraph<Integer, DefaultEdge>> generateGraphs(Supplier<AbstractBaseGraph<Integer, DefaultEdge>> graphSupplier) {
        List<AbstractBaseGraph<Integer, DefaultEdge>> graphs = new ArrayList<>();

        try {
            GnmRandomGraphGenerator<Integer, DefaultEdge>graphGenerator = new GnmRandomGraphGenerator<>(5,10);
            AbstractBaseGraph<Integer, DefaultEdge> graph = graphSupplier.get();
            graphGenerator.generateGraph(graph, null);
            graphs.add(graph);
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            HyperCubeGraphGenerator<Integer, DefaultEdge> graphGenerator = new HyperCubeGraphGenerator<>(4);
            AbstractBaseGraph<Integer, DefaultEdge> graph = graphSupplier.get();
            graphGenerator.generateGraph(graph, null);
            graphs.add(graph);
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            LinearGraphGenerator<Integer, DefaultEdge> graphGenerator = new LinearGraphGenerator<>(5);
            AbstractBaseGraph<Integer, DefaultEdge> graph = graphSupplier.get();
            graphGenerator.generateGraph(graph, null);
            graphs.add(graph);
        } catch(Throwable exception) { exception.printStackTrace(System.err); }


        try {
            NetworkGeneratorConfigBuilder builder = new NetworkGeneratorConfigBuilder();
            builder.setBipartiteMatchingProblemParams(6, 6);
            NetworkGenerator<Integer, DefaultEdge> graphGenerator = new NetworkGenerator<>(builder.build());
            AbstractBaseGraph<Integer, DefaultEdge> graph = graphSupplier.get();
            graphGenerator.generateBipartiteMatchingProblem(graph);
            graphs.add(graph);
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            NetworkGeneratorConfigBuilder builder = new NetworkGeneratorConfigBuilder();
            builder.setMaximumFlowProblemParams(5, 5, 5);
            NetworkGenerator<Integer, DefaultEdge> graphGenerator = new NetworkGenerator<>(builder.build());

            AbstractBaseGraph<Integer, DefaultEdge> graph = graphSupplier.get();
            graphGenerator.generateMaxFlowProblem(graph);
            graphs.add(graph);
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            RandomRegularGraphGenerator<Integer, DefaultEdge> graphGenerator = new RandomRegularGraphGenerator<>(5,4);
            AbstractBaseGraph<Integer, DefaultEdge> graph = graphSupplier.get();
            graphGenerator.generateGraph(graph, null);
            graphs.add(graph);
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            RingGraphGenerator<Integer, DefaultEdge> graphGenerator = new RingGraphGenerator<>(5);
            AbstractBaseGraph<Integer, DefaultEdge> graph = graphSupplier.get();
            graphGenerator.generateGraph(graph, null);
            graphs.add(graph);
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            // Graph with negative edge weight
            AbstractBaseGraph<Integer, DefaultEdge> graph = graphSupplier.get();
            graph.addVertex(1);
            graph.addVertex(2);
            graph.setEdgeWeight(graph.addEdge(1, 2), -1);
            graphs.add(graph);
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        return graphs;
    }

    public static <V,E> void clique(Graph<V,E> graph) {
        try {
            BronKerboschCliqueFinder<V,E> cliqueAlgorithm = new BronKerboschCliqueFinder<>(graph);
            Iterator<Set<V>> cliques = cliqueAlgorithm.iterator();
            while(cliques.hasNext()) { cliques.next(); }
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            ChordalGraphMaxCliqueFinder<V,E> cliqueAlgorithm = new ChordalGraphMaxCliqueFinder<>(graph);
            cliqueAlgorithm.getClique();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            CliqueMinimalSeparatorDecomposition<V,E> cliqueAlgorithm = new CliqueMinimalSeparatorDecomposition<>(graph);
            cliqueAlgorithm.getMinimalTriangulation();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            DegeneracyBronKerboschCliqueFinder<V,E> cliqueAlgorithm = new DegeneracyBronKerboschCliqueFinder<>(graph);
            Iterator<Set<V>> cliques = cliqueAlgorithm.iterator();
            while(cliques.hasNext()) { cliques.next(); }
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            PivotBronKerboschCliqueFinder<V,E> cliqueAlgorithm = new PivotBronKerboschCliqueFinder<>(graph);
            Iterator<Set<V>> cliques = cliqueAlgorithm.iterator();
            while(cliques.hasNext()) { cliques.next(); }
        } catch(Throwable exception) { exception.printStackTrace(System.err); }
    }

    public static <V,E> void clustering(Graph<V,E> graph) {
        try {
            GirvanNewmanClustering<V,E> clusteringAlgorithm = new GirvanNewmanClustering<>(graph, 3);
            clusteringAlgorithm.getClustering().getClusters();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            GreedyModularityAlgorithm<V,E> clusteringAlgorithm = new GreedyModularityAlgorithm<>(graph);
            clusteringAlgorithm.getClustering().getClusters();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            KSpanningTreeClustering<V,E> clusteringAlgorithm = new KSpanningTreeClustering<>(graph, 3);
            clusteringAlgorithm.getClustering().getClusters();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            LabelPropagationClustering<V,E> clusteringAlgorithm = new LabelPropagationClustering<>(graph);
            clusteringAlgorithm.getClustering().getClusters();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            NaiveGreedyModularityAlgorithm<V,E> clusteringAlgorithm = new NaiveGreedyModularityAlgorithm<>(graph);
            clusteringAlgorithm.getClustering().getClusters();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }
    }

    public static <V,E> void color(Graph<V,E> graph) {
        try {
            BrownBacktrackColoring<V,E> coloring = new BrownBacktrackColoring<>(graph);
            coloring.getColoring().getColors();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            ChordalGraphColoring<V,E> coloring = new ChordalGraphColoring<>(graph);
            coloring.getColoring().getColors();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            GreedyColoring<V,E> coloring = new GreedyColoring<>(graph);
            coloring.getColoring().getColors();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            LargestDegreeFirstColoring<V,E> coloring = new LargestDegreeFirstColoring<>(graph);
            coloring.getColoring().getColors();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            RandomGreedyColoring<V,E> coloring = new RandomGreedyColoring<>(graph);
            coloring.getColoring().getColors();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            SaturationDegreeColoring<V,E> coloring = new SaturationDegreeColoring<>(graph);
            coloring.getColoring().getColors();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            SmallestDegreeLastColoring<V,E> coloring = new SmallestDegreeLastColoring<>(graph);
            coloring.getColoring().getColors();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }
    }

    public static <V,E> void connectivity(Graph<V,E> graph) {
        try {
            BiconnectivityInspector<V,E> connectivity = new BiconnectivityInspector<>(graph);
            connectivity.getConnectedComponents();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            BlockCutpointGraph<V,E> connectivity = new BlockCutpointGraph<>(graph);
            connectivity.getCutpoints();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            ConnectivityInspector<V,E> connectivity = new ConnectivityInspector<>(graph);
            connectivity.connectedSets();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            KConnectivityFlowAlgorithm<V,E> connectivity = new KConnectivityFlowAlgorithm<>(graph);
            connectivity.getEdgeConnectivity();
            connectivity.getVertexConnectivity();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }
    }

    public static <V,E> void cycle(Graph<V,E> graph) {

        // Incomplete coverage. Hard to cover completely.
        // try {
        //    VertexToIntegerMapping<V> vertexMapping = Graphs.getVertexToIntegerMapping(graph);
        //    Map<V, Integer> vertexMap = vertexMapping.getVertexMap();
        //    AhujaOrlinSharmaCyclicExchangeLocalAugmentation<V,E> cycle = new AhujaOrlinSharmaCyclicExchangeLocalAugmentation<V,E>(graph, 10, vertexMap, true);
        //    cycle.getLocalAugmentationCycle();
        // } catch(Throwable exception) { exception.printStackTrace(System.err); }

        // Does not terminate.
        // try {
        //   BergeGraphInspector<V,E> cycle = new BergeGraphInspector<>();
        //   cycle.isBerge(graph);
        // } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            QueueBFSFundamentalCycleBasis<V,E> cycle = new QueueBFSFundamentalCycleBasis<>(graph);
            cycle.getCycleBasis();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            StackBFSFundamentalCycleBasis<V,E> cycle = new StackBFSFundamentalCycleBasis<>(graph);
            cycle.getCycleBasis();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            ChinesePostman<V,E> cycle = new ChinesePostman<>();
            cycle.getCPPSolution(graph);
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            ChordalGraphMinimalVertexSeparatorFinder<V,E> cycle = new ChordalGraphMinimalVertexSeparatorFinder<>(graph);
            cycle.getMinimalSeparatorsWithMultiplicities();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            ChordalityInspector<V,E> cycle = new ChordalityInspector<>(graph);
            cycle.getPerfectEliminationOrder();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            WeakChordalityInspector<V,E> cycle = new WeakChordalityInspector<>(graph);
            cycle.getCertificate();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            CycleDetector<V,E> cycle = new CycleDetector<>(graph);
            cycle.detectCycles();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            HawickJamesSimpleCycles<V,E> cycle = new HawickJamesSimpleCycles<>(graph);
            cycle.setPathLimit(10);
            cycle.findSimpleCycles(cycleVertices -> {});
            cycle.countSimpleCycles();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            HowardMinimumMeanCycle<V,E> cycle = new HowardMinimumMeanCycle<>(graph, 10);
            cycle.getCycleMean();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            JohnsonSimpleCycles<V,E> cycle = new JohnsonSimpleCycles<>(graph);
            cycle.findSimpleCycles(cycleVertices -> {});
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            PatonCycleBase<V,E> cycle = new PatonCycleBase<>(graph);
            cycle.getCycleBasis().getCycles();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            SzwarcfiterLauerSimpleCycles<V,E> cycle = new SzwarcfiterLauerSimpleCycles<>(graph);
            cycle.findSimpleCycles();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            TarjanSimpleCycles<V,E> cycle = new TarjanSimpleCycles<>(graph);
            cycle.findSimpleCycles();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            TiernanSimpleCycles<V,E> cycle = new TiernanSimpleCycles<>(graph);
            cycle.findSimpleCycles();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }
    }

    public static <V,E> void denseSubgraph(Graph<V,E> graph, V s, V t) {
        try {
            GoldbergMaximumDensitySubgraphAlgorithm<V, E> denseSubgraphAlgorithm = new GoldbergMaximumDensitySubgraphAlgorithm<>(graph, s, t, 0.001f);
            denseSubgraphAlgorithm.calculateDensest();
            denseSubgraphAlgorithm.getDensity();
        } catch (Throwable exception) {  exception.printStackTrace(System.err); }
    }

    public static <V,E> void drawing(Graph<V,E> graph) {

        Comparator<V> vertexComparator = VertexDegreeComparator.of(graph);

        try {
            CircularLayoutAlgorithm2D<V,E> layoutAlgorithm = new CircularLayoutAlgorithm2D(0.5f, vertexComparator);
            layoutAlgorithm.layout(graph, new MapLayoutModel2D(new Box2D(10, 10)));
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            IndexedFRLayoutAlgorithm2D<V,E> layoutAlgorithm = new IndexedFRLayoutAlgorithm2D();
            layoutAlgorithm.layout(graph, new MapLayoutModel2D(new Box2D(10, 10)));
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            FRLayoutAlgorithm2D<V,E> layoutAlgorithm = new FRLayoutAlgorithm2D();
            layoutAlgorithm.layout(graph, new MapLayoutModel2D(new Box2D(10, 10)));
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            FRLayoutAlgorithm2D<V,E> layoutAlgorithm = new FRLayoutAlgorithm2D();
            layoutAlgorithm.layout(graph, new MapLayoutModel2D(new Box2D(10, 10)));
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            RescaleLayoutAlgorithm2D<V,E> layoutAlgorithm = new RescaleLayoutAlgorithm2D(2.0f);
            layoutAlgorithm.layout(graph, new MapLayoutModel2D(new Box2D(10, 10)));
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            PartitioningAlgorithm.Partitioning<V> partitioning = null;
            partitioning = (new BipartitePartitioning(graph)).getPartitioning();

            try {
                BarycenterGreedyTwoLayeredBipartiteLayout2D<V,E> layoutAlgorithm = new BarycenterGreedyTwoLayeredBipartiteLayout2D(partitioning.getPartition(0), vertexComparator, true);
                layoutAlgorithm.layout(graph, new MapLayoutModel2D(new Box2D(10, 10)));
            } catch(Throwable exception) { exception.printStackTrace(System.err); }

            try {
                MedianGreedyTwoLayeredBipartiteLayout2D<V,E> layoutAlgorithm = new MedianGreedyTwoLayeredBipartiteLayout2D(partitioning.getPartition(0), vertexComparator, true);
                layoutAlgorithm.layout(graph, new MapLayoutModel2D(new Box2D(10, 10)));
            } catch(Throwable exception) { exception.printStackTrace(System.err); }

            try {
                TwoLayeredBipartiteLayout2D<V,E> layoutAlgorithm = (new TwoLayeredBipartiteLayout2D());
                layoutAlgorithm.layout(graph, new MapLayoutModel2D(new Box2D(10, 10)));
                layoutAlgorithm.withVertical(true).withFirstPartition(partitioning.getPartition(0));
                layoutAlgorithm.layout(graph, new MapLayoutModel2D(new Box2D(10, 10)));
            } catch(Throwable exception) { exception.printStackTrace(System.err); }
        } catch(Throwable exception) { exception.printStackTrace(System.err); }
    }

    public static <V,E> void flow(Graph<V, E> graph, V from, V to) {
        try {
            BoykovKolmogorovMFImpl flow = new BoykovKolmogorovMFImpl(graph);
            flow.getMaximumFlow(from, to).getValue();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            DinicMFImpl flow = new DinicMFImpl(graph);
            flow.getMaximumFlow(from, to).getValue();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            EdmondsKarpMFImpl flow = new EdmondsKarpMFImpl(graph);
            flow.getMaximumFlow(from, to).getValue();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            GusfieldEquivalentFlowTree flow = new GusfieldEquivalentFlowTree(graph);
            flow.getMaximumFlow(from, to).getValue();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            GusfieldGomoryHuCutTree flow = new GusfieldGomoryHuCutTree(graph);
            flow.getCutEdges();
            flow.calculateMinCut();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            Set<V> oddVertices = graph.vertexSet().stream()
                    .filter(vertex -> graph.degreeOf(vertex) % 2 != 0)
                    .collect(Collectors.toSet());
            PadbergRaoOddMinimumCutset flow = new PadbergRaoOddMinimumCutset(graph);
            flow.calculateMinCut(oddVertices, true);
            flow.calculateMinCut(oddVertices, false);
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            PushRelabelMFImpl flow = new PushRelabelMFImpl(graph);
            flow.getMaximumFlow(from, to).getValue();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }
    }

    public static <V,E> void lca(Graph<V, E> graph, V u, V v) {
        try {
            BinaryLiftingLCAFinder<V,E> lcaFinder = new BinaryLiftingLCAFinder<>(graph, u);
            lcaFinder.getLCA(u, v);
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            EulerTourRMQLCAFinder<V,E> lcaFinder = new EulerTourRMQLCAFinder<>(graph, u);
            lcaFinder.getLCA(u, v);
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            HeavyPathLCAFinder<V,E> lcaFinder = new HeavyPathLCAFinder<>(graph, u);
            lcaFinder.getLCA(u, v);
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            NaiveLCAFinder<V,E> lcaFinder = new NaiveLCAFinder<>(graph);
            lcaFinder.getLCA(u, v);
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            TarjanLCAFinder<V,E> lcaFinder = new TarjanLCAFinder<>(graph, u);
            lcaFinder.getLCA(u, v);
        } catch(Throwable exception) { exception.printStackTrace(System.err); }
    }

    public static <V,E> void linkPrediction(Graph<V, E> graph, V u, V v) {
        try {
            AdamicAdarIndexLinkPrediction<V,E> prediction = new AdamicAdarIndexLinkPrediction<>(graph);
            prediction.predict(u, v);
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            CommonNeighborsLinkPrediction<V,E> prediction = new CommonNeighborsLinkPrediction<>(graph);
            prediction.predict(u, v);
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            HubDepressedIndexLinkPrediction<V,E> prediction = new HubDepressedIndexLinkPrediction<>(graph);
            prediction.predict(u, v);
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            HubPromotedIndexLinkPrediction<V,E> prediction = new HubPromotedIndexLinkPrediction<>(graph);
            prediction.predict(u, v);
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            JaccardCoefficientLinkPrediction<V,E> prediction = new JaccardCoefficientLinkPrediction<>(graph);
            prediction.predict(u, v);
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            LeichtHolmeNewmanIndexLinkPrediction<V,E> prediction = new LeichtHolmeNewmanIndexLinkPrediction<>(graph);
            prediction.predict(u, v);
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            PreferentialAttachmentLinkPrediction<V,E> prediction = new PreferentialAttachmentLinkPrediction<>(graph);
            prediction.predict(u, v);
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            ResourceAllocationIndexLinkPrediction<V,E> prediction = new ResourceAllocationIndexLinkPrediction<>(graph);
            prediction.predict(u, v);
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            SorensenIndexLinkPrediction	<V,E> prediction = new SorensenIndexLinkPrediction<>(graph);
            prediction.predict(u, v);
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            SaltonIndexLinkPrediction<V,E> prediction = new SaltonIndexLinkPrediction<>(graph);
            prediction.predict(u, v);
        } catch(Throwable exception) { exception.printStackTrace(System.err); }
    }

    public static <V,E> void isomorphism(Graph<V, E> graph1, Graph<V, E> graph2) {
        try {
            AHUUnrootedTreeIsomorphismInspector<V,E> inspector = new AHUUnrootedTreeIsomorphismInspector<>(graph1, graph2);
            Iterator<GraphMapping<V, E>> iterator = inspector.getMappings();
            while(iterator.hasNext()) { iterator.next(); }
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            ColorRefinementIsomorphismInspector<V,E> inspector = new ColorRefinementIsomorphismInspector<>(graph1, graph2);
            Iterator<GraphMapping<V, E>> iterator = inspector.getMappings();
            while(iterator.hasNext()) { iterator.next(); }
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            VF2GraphIsomorphismInspector<V,E> inspector = new VF2GraphIsomorphismInspector<V,E>(graph1, graph2);
            Iterator<GraphMapping<V, E>> iterator = inspector.getMappings();
            while(iterator.hasNext()) { iterator.next(); }
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            VF2SubgraphIsomorphismInspector inspector = new VF2SubgraphIsomorphismInspector(graph1, graph2);
            Iterator<GraphMapping<V, E>> iterator = inspector.getMappings();
            while(iterator.hasNext()) { iterator.next(); }
        } catch(Throwable exception) { exception.printStackTrace(System.err); }
    }

    public static <V,E> void matching(Graph<V, E> graph) {
        try {
            KolmogorovWeightedMatching matchingAlgorithm = new KolmogorovWeightedMatching(graph);
            matchingAlgorithm.getMatching().getEdges();
            matchingAlgorithm.testOptimality();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            KolmogorovWeightedPerfectMatching matchingAlgorithm = new KolmogorovWeightedPerfectMatching(graph);
            matchingAlgorithm.getMatching().getEdges();
            matchingAlgorithm.getDualSolution();
            matchingAlgorithm.testOptimality();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            DenseEdmondsMaximumCardinalityMatching matchingAlgorithm = new DenseEdmondsMaximumCardinalityMatching(graph);
            matchingAlgorithm.getMatching().getEdges();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            GreedyMaximumCardinalityMatching matchingAlgorithm = new GreedyMaximumCardinalityMatching(graph, true);
            matchingAlgorithm.getMatching().getEdges();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            GreedyWeightedMatching matchingAlgorithm = new GreedyWeightedMatching(graph, true);
            matchingAlgorithm.getMatching().getEdges();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            PathGrowingWeightedMatching matchingAlgorithm = new PathGrowingWeightedMatching(graph);
            matchingAlgorithm.getMatching().getEdges();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            SparseEdmondsMaximumCardinalityMatching matchingAlgorithm = new SparseEdmondsMaximumCardinalityMatching(graph);
            matchingAlgorithm.getMatching().getEdges();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        // Matching that requires partinioning
        BipartitePartitioning<V,E> bipartitePartitioning = new BipartitePartitioning<>(graph);
        PartitioningAlgorithm.Partitioning<V> partitioning = bipartitePartitioning.getPartitioning();

        if(partitioning != null) {
            System.out.println(partitioning);
            int partions = partitioning.getNumberPartitions();
            int min = 0;
            int max = partions - 1;

            Set<V> partition1 = partitioning.getPartition(min);
            Set<V> partition2 = partitioning.getPartition(max);

            try {
                HopcroftKarpMaximumCardinalityBipartiteMatching matchingAlgorithm = new HopcroftKarpMaximumCardinalityBipartiteMatching(graph, partition1, partition2);
                matchingAlgorithm.getMatching().getEdges();
            } catch(Throwable exception) { exception.printStackTrace(System.err); }

            try {
                KuhnMunkresMinimalWeightBipartitePerfectMatching matchingAlgorithm = new KuhnMunkresMinimalWeightBipartitePerfectMatching(graph, partition1, partition2);
                matchingAlgorithm.getMatching().getEdges();
            } catch(Throwable exception) { exception.printStackTrace(System.err); }

            try {
                MaximumWeightBipartiteMatching matchingAlgorithm = new MaximumWeightBipartiteMatching(graph, partition1, partition2);
                matchingAlgorithm.getMatching().getEdges();
            } catch(Throwable exception) { exception.printStackTrace(System.err); }
        }
    }

    public static <V,E> void planar(Graph<V, E> graph) {
        try {
            BoyerMyrvoldPlanarityInspector<V,E> algorithm = new BoyerMyrvoldPlanarityInspector<>(graph);
            algorithm.isPlanar();
            try { algorithm.getKuratowskiSubdivision(); } catch(Throwable exception) { exception.printStackTrace(System.err); }
            try { algorithm.getEmbedding().getGraph(); } catch(Throwable exception) { exception.printStackTrace(System.err); }
        } catch(Throwable exception) { exception.printStackTrace(System.err); }
    }

    public static <V,E> void scoring(Graph<V, E> graph, V vertex) {
        try {
            ApBetweennessCentrality<V, E> scoring = new ApBetweennessCentrality<>(graph);
            scoring.getVertexScore(vertex);
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            BetweennessCentrality<V, E> scoring = new BetweennessCentrality<>(graph);
            scoring.getVertexScore(vertex);
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            ClosenessCentrality<V, E> scoring = new ClosenessCentrality<>(graph);
            scoring.getVertexScore(vertex);
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            ClusteringCoefficient<V, E> scoring = new ClusteringCoefficient<>(graph);
            scoring.getVertexScore(vertex);
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            Coreness<V, E> scoring = new Coreness<>(graph);
            scoring.getVertexScore(vertex);
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            EdgeBetweennessCentrality<V, E> scoring = new EdgeBetweennessCentrality<>(graph);
            scoring.getScores();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            EigenvectorCentrality<V, E> scoring = new EigenvectorCentrality<>(graph);
            scoring.getVertexScore(vertex);
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            HarmonicCentrality<V, E> scoring = new HarmonicCentrality<>(graph);
            scoring.getVertexScore(vertex);
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            KatzCentrality<V, E> scoring = new KatzCentrality<>(graph);
            scoring.getVertexScore(vertex);
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            ClusteringCoefficient<V, E> scoring = new ClusteringCoefficient<>(graph);
            scoring.getVertexScore(vertex);
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            PageRank<V, E> scoring = new PageRank<>(graph);
            scoring.getVertexScore(vertex);
        } catch(Throwable exception) { exception.printStackTrace(System.err); }
    }

    public static <V,E> void shortestPath(Graph<V, E> graph, V from, V to) {
        try { DagAllPathsCounter.countAllPaths(graph, from, to); } catch(Throwable exception) { exception.printStackTrace(System.err); }
        try { BellmanFordShortestPath.findPathBetween(graph, from, to).getEdgeList(); } catch(Throwable exception) { exception.printStackTrace(System.err); }
        try { BFSShortestPath.findPathBetween(graph, from, to).getEdgeList(); } catch(Throwable exception) { exception.printStackTrace(System.err); }
        try { DijkstraShortestPath.findPathBetween(graph, from, to).getEdgeList(); } catch(Throwable exception) { exception.printStackTrace(System.err); }
        try { IntVertexDijkstraShortestPath.findPathBetween((Graph<Integer, E>) graph, (Integer)from, (Integer)to).getEdgeList(); } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            AllDirectedPaths<V,E> shortestPathAlgorithm = new AllDirectedPaths<>(graph);
            shortestPathAlgorithm.getAllPaths(from, to, true, 2);
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            ALTAdmissibleHeuristic<V,E> heuristic = new ALTAdmissibleHeuristic(graph, Set.of(from,to));
            AStarShortestPath shortestPathAlgorithm = new AStarShortestPath(graph, heuristic);
            shortestPathAlgorithm.getPath(from, to).getEdgeList();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            ALTAdmissibleHeuristic<V,E> heuristic = new ALTAdmissibleHeuristic(graph, Set.of(from,to));
            BidirectionalAStarShortestPath shortestPathAlgorithm = new BidirectionalAStarShortestPath(graph, heuristic);
            shortestPathAlgorithm.getPath(from, to).getEdgeList();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            ContractionHierarchyBidirectionalDijkstra shortestPathAlgorithm = new ContractionHierarchyBidirectionalDijkstra(graph, executor);
            shortestPathAlgorithm.getPath(from, to).getEdgeList();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            DeltaSteppingShortestPath shortestPathAlgorithm = new DeltaSteppingShortestPath(graph, executor);
            shortestPathAlgorithm.getPath(from, to).getEdgeList();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            FloydWarshallShortestPaths shortestPathAlgorithm = new FloydWarshallShortestPaths(graph);
            shortestPathAlgorithm.getPath(from, to).getEdgeList();
            shortestPathAlgorithm.getFirstHop(from, to);
            shortestPathAlgorithm.getLastHop(from, to);
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            GraphMeasurer graphMeasurer = new GraphMeasurer(graph);
            graphMeasurer.getDiameter();
            graphMeasurer.getRadius();
            graphMeasurer.getGraphCenter();
            graphMeasurer.getGraphPeriphery();
            graphMeasurer.getGraphPseudoPeriphery();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            JohnsonShortestPaths shortestPathAlgorithm = new JohnsonShortestPaths(graph);
            shortestPathAlgorithm.getPath(from, to).getEdgeList();
            shortestPathAlgorithm.getPaths(from).getPath(to).getEdgeList();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            TransitNodeRoutingShortestPath shortestPathAlgorithm = new TransitNodeRoutingShortestPath(graph, executor);
            shortestPathAlgorithm.getPath(from, to).getEdgeList();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        // many-to-many shortest paths algorithms
        try {
            CHManyToManyShortestPaths shortestPathAlgorithm = new CHManyToManyShortestPaths(graph, executor);
            shortestPathAlgorithm.getPaths(from).getPath(to).getEdgeList();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            DijkstraManyToManyShortestPaths shortestPathAlgorithm = new DijkstraManyToManyShortestPaths(graph);
            shortestPathAlgorithm.getPaths(from).getPath(to).getEdgeList();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            DefaultManyToManyShortestPaths shortestPathAlgorithm = new DefaultManyToManyShortestPaths(graph);
            shortestPathAlgorithm.getPaths(from).getPath(to).getEdgeList();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            MartinShortestPath<V,E> shortestPathAlgorithm = new MartinShortestPath<>(graph, edge -> {return new double[]{10.0, 2.5};});
            shortestPathAlgorithm.getPaths(from, to).get(0).getEdgeList();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        // k shortest paths algorithms
        try {
            BhandariKDisjointShortestPaths<V,E> shortestPathAlgorithm = new BhandariKDisjointShortestPaths<>(graph);
            shortestPathAlgorithm.getPaths(from, to, 3).forEach(path -> path.getEdgeList());
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            EppsteinKShortestPath<V,E> shortestPathAlgorithm = new EppsteinKShortestPath<>(graph);
            shortestPathAlgorithm.getPaths(from, to, 3).forEach(path -> path.getEdgeList());
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            SuurballeKDisjointShortestPaths<V,E> shortestPathAlgorithm = new SuurballeKDisjointShortestPaths<>(graph);
            shortestPathAlgorithm.getPaths(from, to, 3).forEach(path -> path.getEdgeList());
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            YenKShortestPath<V,E> shortestPathAlgorithm = new YenKShortestPath<>(graph);
            shortestPathAlgorithm.getPaths(from, to, 3).forEach(path -> path.getEdgeList());
        } catch(Throwable exception) { exception.printStackTrace(System.err); }
    }

    public static <V,E> void similarity(Graph<V, E> graph1, V root1, Graph<V, E> graph2, V root2) {
        try {
            ZhangShashaTreeEditDistance<V,E> similarity = new ZhangShashaTreeEditDistance<>(graph1, root1, graph2, root2);
            similarity.getDistance();
            similarity.getEditOperationLists();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }
    }

    public static <V,E> void spanning(Graph<V, E> graph, V root) {
        try {
            Map<V, Double> vertexWheights = graph.vertexSet().stream().collect(Collectors.toMap(
                    vertex -> vertex,
                    vertex -> 1.0
            ));

            AhujaOrlinSharmaCapacitatedMinimumSpanningTree<V,E> spanningAlgorithm =
                    new AhujaOrlinSharmaCapacitatedMinimumSpanningTree(graph, root, 2.0, vertexWheights, 10, 50);
            CapacitatedSpanningTreeAlgorithm.CapacitatedSpanningTree<V,E> spanningTree = spanningAlgorithm.getCapacitatedSpanningTree();
            spanningAlgorithm = new AhujaOrlinSharmaCapacitatedMinimumSpanningTree(spanningTree, graph, root, 2.0, vertexWheights, 10);
            spanningAlgorithm.getCapacitatedSpanningTree();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            KruskalMinimumSpanningTree<V,E> spanningAlgorithm = new KruskalMinimumSpanningTree(graph);
            spanningAlgorithm.getSpanningTree().getEdges();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            BoruvkaMinimumSpanningTree<V,E> spanningAlgorithm = new BoruvkaMinimumSpanningTree(graph);
            spanningAlgorithm.getSpanningTree().getEdges();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            GreedyMultiplicativeSpanner<V,E> spanningAlgorithm = new GreedyMultiplicativeSpanner(graph, 3);
            spanningAlgorithm.getSpanner().getWeight();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }
    }

    public static <V,E> void tour(Graph<V, E> graph) {
        try {
            ChristofidesThreeHalvesApproxMetricTSP<V,E> tourAlgorithm = new ChristofidesThreeHalvesApproxMetricTSP();
            tourAlgorithm.getTour(graph).getEdgeList();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            FarthestInsertionHeuristicTSP<V,E> tourAlgorithm = new FarthestInsertionHeuristicTSP();
            tourAlgorithm.getTour(graph).getEdgeList();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }
        try {
            GreedyHeuristicTSP<V,E> tourAlgorithm = new GreedyHeuristicTSP();
            tourAlgorithm.getTour(graph).getEdgeList();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            HeldKarpTSP<V,E> tourAlgorithm = new HeldKarpTSP();
            tourAlgorithm.getTour(graph).getEdgeList();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            NearestInsertionHeuristicTSP<V,E> tourAlgorithm = new NearestInsertionHeuristicTSP();
            tourAlgorithm.getTour(graph).getEdgeList();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            NearestNeighborHeuristicTSP<V,E> tourAlgorithm = new NearestNeighborHeuristicTSP();
            tourAlgorithm.getTour(graph).getEdgeList();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            PalmerHamiltonianCycle<V,E> tourAlgorithm = new PalmerHamiltonianCycle();
            tourAlgorithm.getTour(graph).getEdgeList();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            TwoOptHeuristicTSP<V,E> tourAlgorithm = new TwoOptHeuristicTSP();
            tourAlgorithm.getTour(graph).getEdgeList();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }
    }

    public static <V,E> void vertexCover(Graph<V, E> graph) {
        try {
            BarYehudaEvenTwoApproxVCImpl<V,E> vertexCoverAlgorithm = new BarYehudaEvenTwoApproxVCImpl(graph);
            vertexCoverAlgorithm.getVertexCover().getWeight();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            ClarksonTwoApproxVCImpl<V,E> vertexCoverAlgorithm = new ClarksonTwoApproxVCImpl(graph);
            vertexCoverAlgorithm.getVertexCover().getWeight();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            EdgeBasedTwoApproxVCImpl<V,E> vertexCoverAlgorithm = new EdgeBasedTwoApproxVCImpl(graph);
            vertexCoverAlgorithm.getVertexCover().getWeight();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            GreedyVCImpl<V,E> vertexCoverAlgorithm = new GreedyVCImpl(graph);
            vertexCoverAlgorithm.getVertexCover().getWeight();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }

        try {
            RecursiveExactVCImpl<V,E> vertexCoverAlgorithm = new RecursiveExactVCImpl(graph);
            vertexCoverAlgorithm.getVertexCover().getWeight();
        } catch(Throwable exception) { exception.printStackTrace(System.err); }
    }

    public static void main(String[] args) throws Exception {
        Entrypoint.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
        try {
            Entrypoint.entrypoint();
        } finally {
            executor.shutdown();
        }
    }
}
