import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class EmergencyResponseGUI_fw {

    public static void main(String[] args) {
        try {
            // Load the graph 
            Graph_fw graph = GraphLoader_fw.loadGraph("Hospitals.txt", "GraphData.txt");

            // Run Floyd-Warshall algorithm to compute all pairs shortest paths
            graph.floydWarshall();

            // Create a Scanner object to get user input
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter victim latitude: ");
            double victimLat = scanner.nextDouble();
            System.out.print("Enter victim longitude: ");
            double victimLon = scanner.nextDouble();

            // Find the nearest hospital node
            String nearestHospitalNode = findNearestHospital(graph, victimLat, victimLon);
            double hospitalLat = graph.getNodeLatitude(nearestHospitalNode);
            double hospitalLon = graph.getNodeLongitude(nearestHospitalNode);
            String nearestHospital = graph.getHospitalNames().get(nearestHospitalNode);

            // Generate the HTML file
            generateHtml(victimLat, victimLon, hospitalLat, hospitalLon, nearestHospital, graph);

            // Print coordinates and hospital information
            System.out.println("Victim Coordinates: Latitude = " + victimLat + ", Longitude = " + victimLon);
            System.out.println("Nearest Hospital: " + nearestHospital);
            System.out.println("Hospital Coordinates: Latitude = " + hospitalLat + ", Longitude = " + hospitalLon);
            System.out.println("Emergency response details have been saved to profw.html");

        } catch (IOException e) {
            System.err.println("An error occurred while processing: " + e.getMessage());
        }
    }

    private static String findNearestHospital(Graph_fw graph, double victimLat, double victimLon) {
        double shortestDistance = Double.MAX_VALUE;
        String nearestHospital = null;
        for (Map.Entry<String, Graph_fw.Node> entry : graph.getNodes().entrySet()) {
            String hospitalNodeName = entry.getKey();
            double distanceInMiles = haversine(victimLat, victimLon, entry.getValue().getLatitude(), entry.getValue().getLongitude());
            if (distanceInMiles < shortestDistance) {
                shortestDistance = distanceInMiles;
                nearestHospital = hospitalNodeName;
            }
        }
        return nearestHospital;
    }

    private static double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 3959; // Radius of the Earth in miles
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance in miles
    }

private static void generateHtml(double victimLat, double victimLon, double hospitalLat, double hospitalLon, String hospitalName, Graph_fw graph) throws IOException {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter("profw.html"))) {
        writer.write("<!DOCTYPE html>");
        writer.write("<html lang=\"en\">");
        writer.write("<head>");
        writer.write("<meta charset=\"UTF-8\">");
        writer.write("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        writer.write("<title>Emergency Response with Directions</title>");
        writer.write("<script src=\"https://maps.googleapis.com/maps/api/js?key=AIzaSyCBKOQ1y3WtXQEv-ROLKehbN3JWIoIB5k4&callback=initMap\" async defer></script>");
        writer.write("<style>");
        writer.write("body { font-family: Arial, sans-serif; margin: 20px; }");
        writer.write(".container { display: flex;}");
        writer.write(".content { flex: 1; padding-right: 20px; }");
        writer.write("#map { height: 500px; width: 800px }"); 
        writer.write("</style>");
        writer.write("</head>");
        writer.write("<body>");
        writer.write("<div class=\"container\">");
        writer.write("<div class=\"content\">");
        writer.write("<h1>Emergency Response Details</h1>");
        writer.write("<p><strong>Victim Location:</strong></p>");
        writer.write("<p>Latitude: " + victimLat + ", Longitude: " + victimLon + "</p>");
        writer.write("<p><strong>Nearest Hospital:</strong></p>");
        writer.write("<p>" + hospitalName + "</p>");
        writer.write("<p><strong>Hospital Coordinates:</strong></p>");
        writer.write("<p>Latitude: " + hospitalLat + ", Longitude: " + hospitalLon + "</p>");
        writer.write("<p><strong>Shortest Path:</strong></p>");
        writer.write("<p>Victim Location -> " + hospitalName + "</p>");

        writer.write("<p><strong>5 Closest Distances to Hospitals:</strong></p>");
        
        // Collect distances and sort them
        List<HospitalDistance> hospitalDistances = new ArrayList<>();
        for (Map.Entry<String, Graph_fw.Node> entry : graph.getNodes().entrySet()) {
            String hospitalNodeName = entry.getKey();
            double distanceInMiles = haversine(victimLat, victimLon, entry.getValue().getLatitude(), entry.getValue().getLongitude());
            String hospital = graph.getHospitalNames().get(hospitalNodeName);
            hospitalDistances.add(new HospitalDistance(hospital, distanceInMiles));
        }

        // Sort distances and select the top 5 closest hospitals
        hospitalDistances.sort(Comparator.comparingDouble(HospitalDistance::getDistance));
        int count = 0;
        for (HospitalDistance hd : hospitalDistances) {
            if (count >= 5) break;
            writer.write("<p>" + hd.getHospitalName() + ": " + String.format("%.2f", hd.getDistance()) + " miles</p>");
            count++;
        }

        writer.write("<p><strong>Shortest distance is to '" + hospitalName + "'</strong></p>");

        writer.write("</div>");
        writer.write("<div id=\"map\"></div>");
        writer.write("</div>");
        writer.write("<script>");
        writer.write("function initMap() {");
        writer.write("  var victimLat = " + victimLat + ";");
        writer.write("  var victimLon = " + victimLon + ";");
        writer.write("  var hospitalLat = " + hospitalLat + ";");
        writer.write("  var hospitalLon = " + hospitalLon + ";");

        writer.write("  var victim = { lat: victimLat, lng: victimLon };");
        writer.write("  var hospital = { lat: hospitalLat, lng: hospitalLon };");

        writer.write("  var map = new google.maps.Map(document.getElementById('map'), {");
        writer.write("    zoom: 14,");
        writer.write("    center: victim");
        writer.write("  });");

        writer.write("  var victimMarker = new google.maps.Marker({");
        writer.write("    position: victim,");
        writer.write("    map: map,");
        writer.write("    title: 'Victim Location'");
        writer.write("  });");

        writer.write("  var hospitalMarker = new google.maps.Marker({");
        writer.write("    position: hospital,");
        writer.write("    map: map,");
        writer.write("    title: '" + hospitalName + "'");
        writer.write("  });");

        writer.write("  var directionsService = new google.maps.DirectionsService();");
        writer.write("  var directionsRenderer = new google.maps.DirectionsRenderer();");
        writer.write("  directionsRenderer.setMap(map);");

        writer.write("  var request = {");
        writer.write("    origin: victim,");
        writer.write("    destination: hospital,");
        writer.write("    travelMode: google.maps.TravelMode.DRIVING");
        writer.write("  };");

        writer.write("  directionsService.route(request, function(result, status) {");
        writer.write("    if (status === 'OK') {");
        writer.write("      directionsRenderer.setDirections(result);");
        writer.write("    } else {");
        writer.write("      console.error('Directions request failed due to ' + status);");
        writer.write("    }");
        writer.write("  });");

        writer.write("}");
        writer.write("</script>");
        writer.write("</body>");
        writer.write("</html>");
    }
}

    // Custom class to hold hospital name and distance
    private static class HospitalDistance {
        private final String hospitalName;
        private final double distance;

        public HospitalDistance(String hospitalName, double distance) {
            this.hospitalName = hospitalName;
            this.distance = distance;
        }

        public String getHospitalName() {
            return hospitalName;
        }

        public double getDistance() {
            return distance;
        }
    }
}
