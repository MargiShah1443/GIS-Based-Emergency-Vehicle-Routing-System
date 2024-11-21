import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GraphLoader_di {

    public static Graph_di loadGraph(String hospitalFilePath, String graphFilePath) throws IOException {
        Graph_di graph = new Graph_di();
        Map<Integer, String> hospitalNameMap = new HashMap<>();

        // Load hospitals
        try (BufferedReader br = new BufferedReader(new FileReader(hospitalFilePath))) {
            String line;
            int index = 0; // Start indexing from 0
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\s+"); // Split by whitespace

                // Extract latitude and longitude
                double latitude = Double.parseDouble(parts[parts.length - 2]);
                double longitude = Double.parseDouble(parts[parts.length - 1]);

                // Reconstruct hospital name from remaining parts
                String name = String.join(" ", java.util.Arrays.copyOfRange(parts, 0, parts.length - 2));
                
                // Save hospital name with its index
                hospitalNameMap.put(index, name);

                // Add node to graph with name as "Hospital" + index
                graph.addNode("Hospital" + index, latitude, longitude);
                index++;
            }
        }

        // Load graph data
        try (BufferedReader br = new BufferedReader(new FileReader(graphFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#")) continue; // Skip comments
                String[] parts = line.split(" ");
                if (parts.length == 4) {
                    int from = Integer.parseInt(parts[0]);
                    int to = Integer.parseInt(parts[1]);
                    int distance = Integer.parseInt(parts[2]);
                    int type = Integer.parseInt(parts[3]);
                    
                    // Use "Hospital" + index for names
                    graph.addEdge("Hospital" + from, "Hospital" + to, distance);
                }
            }
        }

        // Set hospital names for each node
        graph.setHospitalNames(hospitalNameMap);

        return graph;
    }
}