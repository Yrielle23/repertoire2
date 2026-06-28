#!/bin/bash

echo "🔨 Compilation du framework SPRIT..."

# 1. Nettoyage
rm -rf build/classes/*
mkdir -p build/classes

# 2. Compilation des sources
echo " Compilation des fichiers Java..."
JAVA_SOURCES=$(find src/main/java -name '*.java')
if [ -z "$JAVA_SOURCES" ]; then
    echo " Aucune source Java trouvée"
    exit 1
fi
javac -cp "lib/*" -d build/classes $JAVA_SOURCES 2>/dev/null

# Vérifier si la compilation a réussi
if [ $? -eq 0 ]; then
    echo " Compilation réussie"
else
    echo " Erreur de compilation (voir les sources Java ou les dépendances)"
fi

# 3. Création du fichier JAR
echo " Création du JAR..."
jar cf build/sprit-framework.jar -C build/classes .

# 4. Installer le JAR dans le dépôt Maven local
if command -v mvn >/dev/null 2>&1; then
    echo " Installation du JAR dans le dépôt Maven local..."
    mvn install:install-file -Dfile=build/sprit-framework.jar \
        -DgroupId=com.sprit -DartifactId=framework -Dversion=1.0.0 \
        -Dpackaging=jar -DgeneratePom=true >/dev/null 2>&1
    if [ $? -eq 0 ]; then
        echo " JAR installé dans ~/.m2/repository/com/sprit/framework/1.0.0"
    else
        echo " Échec de l'installation Maven, vérifie que Maven est installé"
    fi
else
    echo "Maven non trouvé, impossible d'installer le JAR localement"
fi

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
fi