import java.util.*;

public class Graph_fw {
    private final Map<String, Node> nodes = new HashMap<>();
    private final Map<String, String> hospitalNames = new HashMap<>();
    private double[][] distances;
    private String[][] next;

    public void addNode(String name, double latitude, double longitude) {
        nodes.put(name, new Node(name, latitude, longitude));
    }

    public void addEdge(String from, String to, int distanceInMiles) {
        Node fromNode = nodes.get(from);
        Node toNode = nodes.get(to);
        if (fromNode != null && toNode != null) {
            fromNode.addEdge(toNode, distanceInMiles);
            toNode.addEdge(fromNode, distanceInMiles); // Assuming undirected graph
        } else {
            System.out.println("Error: One or both nodes not found: " + from + ", " + to);
        }
    }

    public void floydWarshall() {
        int n = nodes.size();
        distances = new double[n][n];
        next = new String[n][n];
        String[] nodeNames = nodes.keySet().toArray(new String[0]);

        // Initialize distances and next arrays
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    distances[i][j] = 0;
                } else {
                    distances[i][j] = Double.MAX_VALUE;
                }
                next[i][j] = null;
            }
        }

        // Fill distances with edge weights
        for (int i = 0; i < n; i++) {
            for (Edge edge : nodes.get(nodeNames[i]).getEdges()) {
                int j = indexOfNode(nodeNames, edge.getTo().getName());
                distances[i][j] = edge.getDistance();
                next[i][j] = edge.getTo().getName();
            }
        }

        // Floyd-Warshall algorithm
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (distances[i][k] != Double.MAX_VALUE && distances[k][j] != Double.MAX_VALUE) {
                        if (distances[i][j] > distances[i][k] + distances[k][j]) {
                            distances[i][j] = distances[i][k] + distances[k][j];
                            next[i][j] = next[i][k];
                        }
                    }
                }
            }
        }
    }

    private int indexOfNode(String[] nodeNames, String nodeName) {
        for (int i = 0; i < nodeNames.length; i++) {
            if (nodeNames[i].equals(nodeName)) {
                return i;
            }
        }
        return -1;
    }

    public double getDistance(String from, String to) {
        int i = indexOfNode(nodes.keySet().toArray(new String[0]), from);
        int j = indexOfNode(nodes.keySet().toArray(new String[0]), to);
        return distances[i][j];
    }

    public String getNextNode(String from, String to) {
        int i = indexOfNode(nodes.keySet().toArray(new String[0]), from);
        int j = indexOfNode(nodes.keySet().toArray(new String[0]), to);
        return next[i][j];
    }

    public void setHospitalNames(Map<Integer, String> nameMap) {
        for (Map.Entry<Integer, String> entry : nameMap.entrySet()) {
            String nodeName = "Hospital" + entry.getKey();
            Node node = nodes.get(nodeName);
            if (node != null) {
                hospitalNames.put(nodeName, entry.getValue());
            }
        }
    }

    public double getNodeLatitude(String nodeName) {
        Node node = nodes.get(nodeName);
        return node != null ? node.getLatitude() : Double.NaN;
    }

    public double getNodeLongitude(String nodeName) {
        Node node = nodes.get(nodeName);
        return node != null ? node.getLongitude() : Double.NaN;
    }

    public Map<String, String> getHospitalNames() {
        return hospitalNames;
    }

    public Map<String, Node> getNodes() {
        return nodes;
    }

    public static class Node {
        private final String name;
        private final double latitude;
        private final double longitude;
        private final List<Edge> edges;

        public Node(String name, double latitude, double longitude) {
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
            this.edges = new ArrayList<>();
        }

        public void addEdge(Node to, int distance) {
            edges.add(new Edge(to, distance));
        }

        public String getName() {
            return name;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public List<Edge> getEdges() {
            return edges;
        }
    }

    private static class Edge {
        private final Node to;
        private final int distance;

        public Edge(Node to, int distance) {
            this.to = to;
            this.distance = distance;
        }

        public Node getTo() {
            return to;
        }

        public int getDistance() {
            return distance;
        }
    }
}
