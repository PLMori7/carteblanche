package ca.polymtl.inf4410.tp2.server;

/**
 *
 *
 * Classe servant à définir la configuration d'un serveur de calcul.
 *
 * @author Pier-Luc Morissette
 *
 */
public class ComputingServerConfig {

    //ComputingServerConfig attributes
    private int port; // Server Port
    private int taskSizeCapacity; // Maximum Task Size (max number of operations)
    private int maliceLevel; // Mischievious level (from 1 to 10)

    //ComputingServerConfig constructor
    public ComputingServerConfig(int port, int taskSizeCapacity, int maliceLevel) {
        this.port = port;
        this.taskSizeCapacity = taskSizeCapacity;
        this.maliceLevel = maliceLevel;
    }

    //Port getter & setter
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        if ( (port <= 5050) || (port >= 5000) ) {
            this.port = port;
        } else {
            System.err.println("Erreur: Veuillez entrez un port entre 5000 et 5050." );
        }
    }

    //taskSizeCapacity getter & setter
    public int getTaskSizeCapacity() {
        return taskSizeCapacity;
    }

    public void setTaskSizeCapacity(int taskSizeCapacity) {
        this.taskSizeCapacity = taskSizeCapacity;
    }

    //maliceLevel getter & setter
    public int getMaliceLevel() {
        return maliceLevel;
    }

    public void setMaliceLevel(int maliceLevel) {
        if ( (maliceLevel <= 10) || (maliceLevel >= 0) ) {
            this.maliceLevel = maliceLevel;
        } else {
            System.err.println("Erreur: Veuillez entrez un entier entre 1 et 10." );
        }
    }

}
