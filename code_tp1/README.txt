###############################################################################
#	     Explications reliées à l'exécution du TP1 d'INF4410              #
###############################################################################
#								              #
#	1 - Lancer rmiregistry et le serveur				      #
#	  1.1 - Lancer rmiregistry					      #
#         1.2 - Démarrer le serveur					      #
#								              #
#	2 - Lancer les différentes commandes du client			      #
#         2.1 - create							      #
#	  2.2 - list							      #
#         2.3 - syncLocalDir						      #
#	  2.4 - get							      #
#	  2.5 - lock							      #
#	  2.6 - push							      #
#								              #
###############################################################################

Notes: 	Tous les fichiers du côté serveur se retrouvent dans le répertoire
	"server_files" et tous les fichiers côté client dans le répertoire
	"client_files".

###############################################################################
#	          Partie 1 - Lancer rmiregistry et le serveur		      #
###############################################################################

------------------------- 1.1 - Lancer rmiregistry ----------------------------

Étape 1 - Aller dans le répertoire .../code_tp1/bin
Étape 2 - Entrer la commande "rmiregistry &"

------------------------- 1.2 - Démarrer le serveur ---------------------------

Étape 1 - Aller dans le répertoire .../code_tp1
Étape 2 - Faire le build en lancant la commande "ant"
Étape 3 - Entrer la commande "./server &"

###############################################################################
#	     Partie 2 - Lancer les différentes commandes du client	      #
###############################################################################

Note: 	Toutes les commandes du client doivent être lancées à partir du
	répertoire .../code_tp1.

-------------------------------- 2.1 - create ---------------------------------

Permet de créer un nouveau fichier vide chez le serveur.

Utilisation: ./client create NOM_DU_FICHIER

--------------------------------- 2.2 - list ----------------------------------

Renvoie la liste des noms des fichiers avec le propriéraire du verrou si tel
est le cas.

Utilisation: ./client list

----------------------------- 2.3 - syncLocalDir ------------------------------

Permet de récupérer les noms et les contenus de tous les fichiers du serveur.

Utilisation: ./client syncLocalDir

---------------------------------- 2.4 - get ----------------------------------

Permet de demander au serveur d'envoyer la dernière version du fichier 
spécifié.

Utilisation: ./client get NOM_DU_FICHIER

--------------------------------- 2.5 - lock ----------------------------------

Demande au serveur de verrouiller le fichier spécifié.

Utilisation: ./client lock NOM_DU_FICHIER

--------------------------------- 2.6 - push ----------------------------------

Envoie une ouvelle version du fichier spécifié au serveur.

Utilisation: ./client push NOM_DU_FICHIER

-------------------------------------------------------------------------------
