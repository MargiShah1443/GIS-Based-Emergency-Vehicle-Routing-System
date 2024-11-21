# GIS-Based-Emergency-Vehicle-Routing-System

### **Project Highlights**
1. **Objective:**  
   - Develop a Java-based system to calculate efficient routes for emergency vehicles.
   - Incorporate **GIS data** for geographic accuracy and **algorithms** like Dijkstra's, Bellman-Ford, and Floyd-Warshall for optimized pathfinding.

2. **Problem Statement:**  
   Urban traffic and complex networks delay emergency responses, risking lives. This system tackles such challenges by integrating geographic and traffic data to reduce response times.

3. **Key Features:**  
   - **Shortest Path Algorithms:** Handles dynamic traffic conditions, including negative edge weights.
   - **User-Friendly Interface:** Simplifies operations for emergency teams with real-time maps and turn-by-turn navigation.

---

### **Algorithm Analysis**
1. **Dijkstra's Algorithm:**  
   - Fast for non-negative weights; leverages a greedy approach.
   - Optimized using a priority queue for scalability in urban networks.

2. **Bellman-Ford Algorithm:**  
   - Suitable for graphs with **negative edge weights**, though slower than Dijkstra's.
   - Validates routes by iteratively relaxing edges over \( V-1 \) iterations.

3. **Floyd-Warshall Algorithm:**  
   - Comprehensive for **all-pairs shortest path** computation.
   - Memory-intensive but useful for smaller-scale, detailed route planning.

---

### **System Architecture**
- **GIS Integration:**  
   Maps urban layouts, incorporating static and real-time traffic data.
  
- **Routing Engine:**  
   Dynamically selects the best algorithm based on graph properties.
  
- **User Interface:**  
   - Displays the optimal path with route details and the 5 closest hospitals.
   - Employs Google Maps API for visualization.

---

### **Performance Metrics**
1. **Execution Time:**  
   - **Dijkstra’s:** Fastest overall.  
   - **Bellman-Ford:** Reliable but slower with large datasets.  
   - **Floyd-Warshall:** Versatile but resource-intensive for large graphs.

2. **Accuracy:**  
   Consistently validated with known routes, ensuring reliable outputs.

3. **Scalability:**  
   - **Dijkstra’s:** Efficient with larger graphs.  
   - **Bellman-Ford and Floyd-Warshall:** Encountered performance limitations under scalability.

---

### **Technical Implementation**
- **Graph Representation:**  
   Models intersections and roads as weighted nodes and edges, with weights reflecting traffic conditions.

- **Real-Time Routing:**  
   Combines real-time updates with static geographic data to improve accuracy.

- **Java and HTML Integration:**  
   Generates interactive HTML outputs using Google Maps, showcasing route details dynamically.

---

### **Project Impact**
This system is designed to significantly improve emergency response times by integrating advanced algorithms, real-time GIS data, and an intuitive interface. Its ability to handle varying traffic conditions and negative edge weights makes it a robust tool for urban emergency services.

![image](https://github.com/user-attachments/assets/09fbdb26-461b-4959-9a12-7231da658d86)

