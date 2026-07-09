#!/bin/bash
# Script para regenerar o Gradle Wrapper com checksum válido

set -e

echo "Atualizando Gradle Wrapper para versão 9.3.1..."
chmod +x gradlew
./gradlew wrapper --gradle-version 9.3.1

echo "✓ Gradle Wrapper atualizado com sucesso!"
echo ""
echo "Agora execute os seguintes comandos para fazer commit e push:"
echo "git add gradle/wrapper/gradle-wrapper.jar gradle/wrapper/gradle-wrapper-download.sh"
echo "git commit -m 'Update Gradle Wrapper to 9.3.1 with valid checksum'"
echo "git push origin fix/update-gradle-wrapper"
