# TP3_SPP

## Résumé du projet

Ce travail a été effectué dans le contexte du TP3 du module **Systèmes Parallèles et Performants (SPP)**. Il s'agit de créer un moteur de traitement d'image capable d'appliquer des filtres en mode **mono-fil** et **multi-fil**.

Deux filtres ont été mis en place :
- un filtre de tonalités de gris,
- un filtre pour l'extraction des contours fondé sur le gradient gaussien.

Suite à la mise en place des deux moteurs, nous avons effectué une évaluation de leurs performances.  
Les données démontrent que l'emploi du multi-thread permet de diminuer considérablement le délai de traitement, surtout pour les images de grande taille.  
Toutefois, au-delà d'une certaine quantité de threads, les bénéfices commencent à diminuer en raison de la surcharge liée à la synchronisation.
Ce TP nous a permis de mieux comprendre les enjeux liés à la parallélisation et à la gestion efficace des threads en Java.
