import java.util.*;

public class Graph_bf {
    private final Map<String, Node> nodes = new HashMap<>();
    private final Map<String, String> hospitalNames = new HashMap<>();

    public void addNode(String name, double latitude, double longitude) {
        nodes.put(name, new Node(name, latitude, longitude));
    }

    public void addEdge(String from, String to, double distanceInMiles) {
        Node fromNode = nodes.get(from);
        Node toNode = nodes.get(to);
        if (fromNode != null && toNode != null) {
            fromNode.addEdge(toNode, distanceInMiles);
            toNode.addEdge(fromNode, distanceInMiles); 
        } else {
            System.out.println("Error: One or both nodes not found: " + from + ", " + to);
        }
    }

    public Map<String, Double> bellmanFord(String start) {
        Node source = nodes.get(start);
        if (source == null) {
            throw new IllegalArgumentException("Start node not found: " + start);
        }

        Map<String, Double> distances = new HashMap<>();
        for (String nodeName : nodes.keySet()) {
            distances.put(nodeName, Double.MAX_VALUE);
        }
        distances.put(start, 0.0);

        int numNodes = nodes.size();
        for (int i = 1; i < numNodes; i++) {
            for (Node node : nodes.values()) {
                for (Edge edge : node.getEdges()) {
                    if (distances.get(node.getName()) != Double.MAX_VALUE) {
                        double newDist = distances.get(node.getName()) + edge.getDistance();
                        if (newDist < distances.get(edge.getTo().getName())) {
                            distances.put(edge.getTo().getName(), newDist);
                        }
                    }
                }
            }
        }

        for (Node node : nodes.values()) {
            for (Edge edge : node.getEdges()) {
                if (distances.get(node.getName()) != Double.MAX_VALUE) {
                    double newDist = distances.get(node.getName()) + edge.getDistance();
                    if (newDist < distances.get(edge.getTo().getName())) {
                        throw new IllegalArgumentException("Graph contains a negative-weight cycle");
                    }
                }
            }
        }

        return distances;
    }

    public String findNearestHospital(double victimLat, double victimLon) {
        String nearestHospitalNode = null;
        double minDistance = Double.MAX_VALUE;

        for (Node node : nodes.values()) {
            double distance = haversine(victimLat, victimLon, node.getLatitude(), node.getLongitude());
            if (distance < minDistance) {
                minDistance = distance;
                nearestHospitalNode = node.getName();
            }
        }

        return nearestHospitalNode;
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

    public double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 3958; // Radius of the Earth in miles

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance in miles
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

        public void addEdge(Node to, double distance) {
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
        private final double distance;

        public Edge(Node to, double distance) {
            this.to = to;
            this.distance = distance;
        }

        public Node getTo() {
            return to;
        }

        public double getDistance() {
            return distance;
        }
    }
}
