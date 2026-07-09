#!/bin/bash
# Script to regenerate Gradle Wrapper with valid checksum for version 9.3.1
# This fixes the GitHub Actions validation failure

set -e

echo "==========================================="
echo "Gradle Wrapper Checksum Fix"
echo "==========================================="
echo ""
echo "Step 1: Setting up Java 17..."
# Java should already be available in most environments

echo "Step 2: Making gradlew executable..."
chmod +x gradlew

echo "Step 3: Regenerating Gradle Wrapper to version 9.3.1..."
./gradlew wrapper --gradle-version 9.3.1

echo ""
echo "✅ Gradle Wrapper has been successfully regenerated!"
echo ""
echo "The following files have been updated:"
echo "  - gradle/wrapper/gradle-wrapper.jar (with valid checksum)"
echo "  - gradle/wrapper/gradle-wrapper.properties"
echo ""
echo "Next steps:"
echo "1. Review the changes: git status"
echo "2. Commit: git add gradle/wrapper/ && git commit -m 'Update Gradle Wrapper to 9.3.1 with valid checksum'"
echo "3. Push: git push origin fix/update-gradle-wrapper"
echo ""
echo "==========================================="
