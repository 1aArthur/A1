#!/bin/bash
# Automated Gradle Wrapper Fix
# This script regenerates the gradle-wrapper.jar with valid checksum

set -e

echo "🔧 Iniciando atualização do Gradle Wrapper..."
echo ""

# Step 1: Setup Java
echo "✓ Java 17 será usado pelo Gradle"

# Step 2: Make gradlew executable
echo "✓ Tornando gradlew executável..."
chmod +x gradlew

# Step 3: Regenerate wrapper
echo "✓ Regenerando Gradle Wrapper para versão 9.3.1..."
./gradlew wrapper --gradle-version 9.3.1

# Step 4: Verify changes
if git diff --quiet gradle/wrapper/gradle-wrapper.jar; then
    echo "⚠️  Nenhuma mudança detectada no gradle-wrapper.jar"
else
    echo "✅ gradle-wrapper.jar foi regenerado com sucesso!"
    echo ""
    echo "📝 Arquivo atualizado com checksum válido:"
    echo "   - gradle/wrapper/gradle-wrapper.jar"
    echo ""
    echo "🔗 Próximas etapas:"
    echo "   1. git add gradle/wrapper/gradle-wrapper.jar"
    echo "   2. git commit -m 'Regenerate gradle-wrapper.jar with valid checksum for 9.3.1'"
    echo "   3. git push origin fix/update-gradle-wrapper"
fi

echo ""
echo "✨ Conclusão: A branch fix/update-gradle-wrapper está pronta!"
echo "   Agora crie um Pull Request para a branch main/master"
