###############################################################################
#	     Explications reliées à l'exécution du TP2 d'INF4410              #
###############################################################################
#								              #
#	1 - Lancer rmiregistry sur le port 5000				      #
#								              #       
#	2 - Lancer des serveurs de calculs				      #
#								              #
#	3 - Envoyer une tâche au répartiteur				      #
#								              #
###############################################################################


###############################################################################
#	          Partie 1 - Lancer rmiregistry sur le port 5000	      #
###############################################################################


Étape 1 - Aller dans le répertoire ... /code_tp2/bin
Étape 2 - Entrer la commande "rmiregistry 5000 &"


###############################################################################
#	     Partie 2 - Lancer les différentes commandes du client	      #
###############################################################################

Étape 1 - S'assurer d'être dans le répertoire .../code_tp2
Étape 2 - Lancer un nouveau serveur avec la commande 
	  "./computingServer NAME PORT CAPACITY MALICE_LEVEL &"
	  où :
		NAME : Le nom du serveur,
		PORT : Le port du serveur (entier entre 5001 et 5050),
		CAPACITY : La taille de tâche maximale supportée,
		MALICE_LEVEL : Niveau de malice (entier entre 1 et 10).

	  Ex.: "./computingServer Server1 5027 25 2 &"


###############################################################################
#	  	   Partie 3 - Envoyer une tâche au répartiteur      	      #
###############################################################################

Étape 1 - S'assurer d'être dans le répertoire .../code_tp2
Étape 2 - Lancer un nouveau serveur avec la commande 
	  "java -jar dispatcher.jar PATH SAFE_MODE HOST PORT"
	  où :
		PATH : Emplacement du fichier à partir de root ("/"),
		SAFE_MODE : Mode sécurisé (1 pour le sécurisé 0 pour 
			    non-sécurisé),
		HOST : Adresse de l'hôrte,
		PORT: Port de rmiregistry (5000 dans notre cas).

	  Ex.: "java -jar dispatcher.jar /home/pierluc/Polytechnique/Hiver2016
		/INF4410/carteblanche/code_tp2/donnees/benchmark/donnees-60.txt"
 1 localhost 5000"
