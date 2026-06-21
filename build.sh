#!/bin/bash

echo " Compilation du framework SPRIT..."

# 1. Nettoyage
rm -rf build/classes/*
mkdir -p build/classes

# 2. Compilation des sources
echo " Compilation des fichiers Java..."
javac -cp "lib/*" -d build/classes src/main/java/com/sprit/**/*.java 2>/dev/null

# Vérifier si la compilation a réussi
if [ $? -eq 0 ]; then
    echo " Compilation réussie"
else
    echo " Erreur de compilation (peut être normal si les fichiers n'existent pas encore)"
fi

# 3. Création du fichier JAR
echo " Création du JAR..."
jar cf build/sprit-framework.jar -C build/classes .

# 4. Afficher le résultat
if [ -f build/sprit-framework.jar ]; then
    echo " Framework compilé : build/sprit-framework.jar"
    ls -la build/sprit-framework.jar
    
    # 5. Copier vers l'application de test (si le chemin existe)
if [ -d "../repertoire1/src/main/webapp/WEB-INF/lib" ]; then
    cp build/sprit-framework.jar ../repertoire1/src/main/webapp/WEB-INF/lib/
    echo " JAR copié vers l'application de test"
else
    echo " Chemin non trouvé: ../repertoire1/src/main/webapp/WEB-INF/lib"
    echo " Le JAR est disponible dans : build/sprit-framework.jar"
fi