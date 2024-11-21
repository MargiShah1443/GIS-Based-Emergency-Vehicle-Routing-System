import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class EmergencyResponseGUI_bf {

    public static void main(String[] args) {
        try {
            // Loading the graph 
            Graph_bf graph = GraphLoader_bf.loadGraph("Hospitals.txt", "GraphData.txt");

            // Creating a Scanner object to get user input
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter victim latitude: ");
            double victimLat = scanner.nextDouble();
            System.out.print("Enter victim longitude: ");
            double victimLon = scanner.nextDouble();

            // Finding the nearest hospital node
            String nearestHospitalNode = graph.findNearestHospital(victimLat, victimLon);
            double hospitalLat = graph.getNodeLatitude(nearestHospitalNode);
            double hospitalLon = graph.getNodeLongitude(nearestHospitalNode);
            String nearestHospital = graph.getHospitalNames().get(nearestHospitalNode);

            // Generating the HTML file
            generateHtml(victimLat, victimLon, hospitalLat, hospitalLon, nearestHospital, graph);

            // Printing coordinates and hospital information
            System.out.println("Victim Coordinates: Latitude = " + victimLat + ", Longitude = " + victimLon);
            System.out.println("Nearest Hospital: " + nearestHospital);
            System.out.println("Hospital Coordinates: Latitude = " + hospitalLat + ", Longitude = " + hospitalLon);
            System.out.println("Emergency response details have been saved to probf.html");

        } catch (IOException e) {
            System.err.println("An error occurred while processing: " + e.getMessage());
        }
    }

    private static void generateHtml(double victimLat, double victimLon, double hospitalLat, double hospitalLon, String hospitalName, Graph_bf graph) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("probf.html"))) {
            writer.write("<!DOCTYPE html>");
            writer.write("<html lang=\"en\">");
            writer.write("<head>");
            writer.write("<meta charset=\"UTF-8\">");
            writer.write("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
            writer.write("<title>Emergency Response with Directions</title>");
            writer.write("<script src=\"https://maps.googleapis.com/maps/api/js?key=AIzaSyCBKOQ1y3WtXQEv-ROLKehbN3JWIoIB5k4&callback=initMap\" async defer></script>");
            writer.write("<style>");
            writer.write("body { font-family: Arial, sans-serif; }");
            writer.write(".container { display: flex; }");
            writer.write(".content { flex: 1; padding-right: 20px; }");
            writer.write("#map { height: 500px; width: 800px; }");
            writer.write("</style>");
            writer.write("</head>");
            writer.write("<body>");
            writer.write("<h1>Emergency Response Details</h1>");
            writer.write("<div class=\"container\">");
            writer.write("<div class=\"content\">");
            writer.write("<p><strong>Victim Location:</strong></p>");
            writer.write("<p>Latitude: " + victimLat + ", Longitude: " + victimLon + "</p>");
            writer.write("<p><strong>Nearest Hospital:</strong></p>");
            writer.write("<p>" + hospitalName + "</p>");
            writer.write("<p><strong>Hospital Coordinates:</strong></p>");
            writer.write("<p>Latitude: " + hospitalLat + ", Longitude: " + hospitalLon + "</p>");
            writer.write("<p><strong>Shortest Path:</strong></p>");
            writer.write("<p>VictimLocation -> " + hospitalName + "</p>");

            // Creating a list to store hospital names and their distances
            List<Map.Entry<String, Double>> hospitalDistances = new ArrayList<>();

            for (Map.Entry<String, Graph_bf.Node> entry : graph.getNodes().entrySet()) {
                String hospitalNodeName = entry.getKey();
                double distanceInMiles = graph.haversine(victimLat, victimLon, entry.getValue().getLatitude(), entry.getValue().getLongitude());
                hospitalDistances.add(new AbstractMap.SimpleEntry<>(hospitalNodeName, distanceInMiles));
            }

            // Sorting the hospitals by distance (ascending order)
            hospitalDistances.sort(Map.Entry.comparingByValue());

            // Selecting the top 5 closest hospitals
            int numToDisplay = Math.min(hospitalDistances.size(), 5);
            List<Map.Entry<String, Double>> top5Hospitals = hospitalDistances.subList(0, numToDisplay);

            writer.write("<p><strong>5 Closest Hospitals:</strong></p>");
            writer.write("<select id='hospitalSelect' onchange='updateRoute()'>");
            for (Map.Entry<String, Double> entry : top5Hospitals) {
                String hospital = graph.getHospitalNames().get(entry.getKey());
                double lat = graph.getNodeLatitude(entry.getKey());
                double lon = graph.getNodeLongitude(entry.getKey());
                writer.write("<option value='" + lat + "," + lon + "'>" + hospital + ": " + String.format("%.2f", entry.getValue()) + " miles</option>");
            }
            writer.write("</select>");
            writer.write("<p><strong>Shortest distance is to '" + hospitalName + "'</strong></p>");

            
            writer.write("</div>");
            writer.write("<div id=\"map\"></div>");
            writer.write("</div>");

            writer.write("<script>");
            writer.write("var map, directionsService, directionsRenderer;");

            writer.write("function initMap() {");
            writer.write("  var victimLat = " + victimLat + ";");
            writer.write("  var victimLon = " + victimLon + ";");
            writer.write("  var hospitalLat = " + hospitalLat + ";");
            writer.write("  var hospitalLon = " + hospitalLon + ";");

            writer.write("  var victim = { lat: victimLat, lng: victimLon };");
            writer.write("  var hospital = { lat: hospitalLat, lng: hospitalLon };");

            writer.write("  map = new google.maps.Map(document.getElementById('map'), {");
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
            writer.write("  directionsService = new google.maps.DirectionsService();");
            writer.write("  directionsRenderer = new google.maps.DirectionsRenderer();");

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

            writer.write("function updateRoute() {");
            writer.write("  var select = document.getElementById('hospitalSelect');");
            writer.write("  var selectedValue = select.options[select.selectedIndex].value;");
            writer.write("  var latLon = selectedValue.split(',');");
            writer.write("  var hospitalLat = parseFloat(latLon[0]);");
            writer.write("  var hospitalLon = parseFloat(latLon[1]);");

            writer.write("  var hospital = { lat: hospitalLat, lng: hospitalLon };");

            writer.write("  var request = {");
            writer.write("    origin: { lat: " + victimLat + ", lng: " + victimLon + " },");
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
}
