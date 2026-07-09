# Gradle Wrapper Fix - Instruções

## Problema
O GitHub Actions está falhando com o erro:
```
✗ Found unknown Gradle Wrapper JAR files:
  a5e75118d96b4eac2100876f6af6a5ca5029cd440f87736425350fb4cf308b42 gradle/wrapper/gradle-wrapper.jar
```

Isso significa que o arquivo `gradle-wrapper.jar` tem um checksum inválido e não corresponde a nenhuma versão conhecida do Gradle.

## Solução

### Opção 1: Executar o script localmente (Recomendado)

1. Clone o repositório (se ainda não tiver):
```bash
git clone https://github.com/1aArthur/A1.git
cd A1
```

2. Mude para a branch fix:
```bash
git checkout fix/update-gradle-wrapper
```

3. Execute o script:
```bash
bash fix-gradle-wrapper.sh
```

4. Faça commit e push das alterações:
```bash
git add gradle/wrapper/
git commit -m "Update Gradle Wrapper to 9.3.1 with valid checksum"
git push origin fix/update-gradle-wrapper
```

5. Abra um Pull Request para a branch `main` (ou `master`)

### Opção 2: Fazer manualmente

Se preferir fazer manualmente, execute os seguintes comandos:

```bash
# 1. Dê permissão de execução ao gradlew
chmod +x gradlew

# 2. Regenere o Gradle Wrapper
./gradlew wrapper --gradle-version 9.3.1

# 3. Faça commit das mudanças
git add gradle/wrapper/
git commit -m "Update Gradle Wrapper to 9.3.1 with valid checksum"
git push origin fix/update-gradle-wrapper
```

## O que o script faz

- ✅ Regenera o `gradle-wrapper.jar` com o checksum válido para Gradle 9.3.1
- ✅ Atualiza as propriedades do wrapper se necessário
- ✅ Garante que o arquivo JAR tenha um checksum reconhecido pelo GitHub Actions

## Próximas etapas

1. Após executar o script, crie um Pull Request da branch `fix/update-gradle-wrapper` para `main`
2. Verifique se o GitHub Actions passa na validação do Gradle Wrapper
3. Merge do Pull Request
4. A branch `fix/update-gradle-wrapper` pode ser deletada após o merge

## Referências
- [Gradle Wrapper Validation - GitHub Actions](https://github.com/gradle/actions/blob/main/docs/wrapper-validation.md)
- [Documentação Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html)
