import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GraphLoader_bf {

    public static Graph_bf loadGraph(String hospitalFilePath, String graphFilePath) throws IOException {
        Graph_bf graph = new Graph_bf();
        Map<Integer, String> hospitalNameMap = new HashMap<>();

        // Loading hospitals
        try (BufferedReader br = new BufferedReader(new FileReader(hospitalFilePath))) {
            String line;
            int index = 0; // Start indexing from 0
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\s+"); // Splitting by whitespace

                // Extracting latitude and longitude
                double latitude = Double.parseDouble(parts[parts.length - 2]);
                double longitude = Double.parseDouble(parts[parts.length - 1]);

                // Reconstructing hospital name from remaining parts
                String name = String.join(" ", java.util.Arrays.copyOfRange(parts, 0, parts.length - 2));
                
                // Saving hospital name with its index
                hospitalNameMap.put(index, name);

                // Adding node to graph with name as "Hospital" + index
                graph.addNode("Hospital" + index, latitude, longitude);
                index++;
            }
        }

        // Loading graph data
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
                    
                    // Using "Hospital" + index for names
                    graph.addEdge("Hospital" + from, "Hospital" + to, distance);
                }
            }
        }

        // Setting hospital names for each node
        graph.setHospitalNames(hospitalNameMap);

        return graph;
    }
}